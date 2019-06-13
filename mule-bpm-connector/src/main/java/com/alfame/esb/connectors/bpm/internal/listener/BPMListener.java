package com.alfame.esb.connectors.bpm.internal.listener;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.slf4j.LoggerFactory.getLogger;

import com.alfame.esb.bpm.activity.queue.api.BPMActivity;
import com.alfame.esb.bpm.activity.queue.api.BPMActivityAttributes;
import com.alfame.esb.bpm.activity.queue.api.BPMActivityQueue;
import com.alfame.esb.bpm.activity.queue.api.BPMActivityQueueFactory;
import com.alfame.esb.connectors.bpm.internal.BPMQueueDescriptor;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.component.location.ConfigurationComponentLocator;
import org.mule.runtime.api.component.location.Location;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.message.ErrorType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.api.scheduler.SchedulerConfig;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.api.tx.TransactionException;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.execution.OnError;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.execution.OnTerminate;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

@Alias( "listener" )
@EmitsResponse
@MediaType( value = ANY, strict = false )
public class BPMListener extends Source< Serializable, BPMActivityAttributes > {

	private static final Logger LOGGER = getLogger( BPMListener.class );

	@ParameterGroup( name = "queue" )
	private BPMQueueDescriptor queueDescriptor;

	@Parameter
	@Optional( defaultValue = "4" )
	private int numberOfConsumers;

	@Inject
	private ConfigurationComponentLocator componentLocator;

	private Scheduler scheduler;

	@Inject
	private SchedulerConfig schedulerConfig;

	@Inject
	private SchedulerService schedulerService;

	private Semaphore semaphore;
	private ComponentLocation location;

	private List< Consumer > consumers;

	@Override
	public void onStart( SourceCallback< Serializable, BPMActivityAttributes > sourceCallback ) throws MuleException {

		startConsumers( sourceCallback );

	}

	@Override
	public void onStop() {

		if( consumers != null ) {
			consumers.forEach( Consumer::stop );
		}

		if( scheduler != null ) {
			scheduler.shutdownNow();
		}

	}

	@OnSuccess
	public void onSuccess( @ParameterGroup( name = "Response", showInDsl = true ) BPMResponseBuilder messageBuilder, CorrelationInfo correlationInfo, SourceCallbackContext ctx ) {

		LOGGER.info( (String)messageBuilder.getContent().getValue() );

	}

	@OnError
	public void onError( Error error, CorrelationInfo correlationInfo, SourceCallbackContext ctx ) {

		final ErrorType errorType = error.getErrorType();
		String msg = errorType.getNamespace() + ":" + errorType.getIdentifier() + ": " + error.getDescription();


	}

	@OnTerminate
	public void onTerminate() {

		semaphore.release();

	}

	private void startConsumers( SourceCallback< Serializable, BPMActivityAttributes > sourceCallback ) {
		createScheduler();
		consumers = new ArrayList<>( numberOfConsumers );
		semaphore = new Semaphore( getMaxConcurrency(), false );
		for( int i = 0; i < numberOfConsumers; i++ ) {
			final Consumer consumer = new Consumer( sourceCallback );
			consumers.add( consumer );
			scheduler.submit( consumer::start );
		}

	}

	private void createScheduler() {
		scheduler = schedulerService.customScheduler( schedulerConfig
				.withMaxConcurrentTasks( numberOfConsumers )
				.withName( "bpm-listener-flow" + location.getRootContainerName() )
				.withWaitAllowed( true )
				.withShutdownTimeout( queueDescriptor.getTimeout(), queueDescriptor.getTimeoutUnit() ) );
	}

	private int getMaxConcurrency() {
		Flow flow = (Flow) componentLocator.find( Location.builder().globalName( location.getRootContainerName() ).build() ).get();
		return flow.getMaxConcurrency();
	}

	private class Consumer {

		private final SourceCallback< Serializable, BPMActivityAttributes > sourceCallback;
		private final AtomicBoolean stop = new AtomicBoolean( false );

		public Consumer( SourceCallback< Serializable, BPMActivityAttributes > sourceCallback ) {
			this.sourceCallback = sourceCallback;
		}

		public void start() {
			final long timeout = queueDescriptor.getQueueTimeoutInMillis();

			while( isAlive() ) {

				SourceCallbackContext ctx = sourceCallback.createContext();
				try {

					semaphore.acquire();
					final BPMActivityQueue queue = BPMActivityQueueFactory.getInstance( queueDescriptor.getQueueName() );
					BPMActivity value = queue.pop();

					if( value != null ) {
						LOGGER.info( "test" );
					}
					if( value == null ) {
						cancel( ctx );
						continue;
					}

					String correlationId = null;
					Result.Builder resultBuilder = Result.<Serializable, BPMActivityAttributes >builder();

					correlationId = value.getCorrelationId().orElse( null );

					resultBuilder.output( value );
					resultBuilder.attributes( new BPMActivityAttributes( queueDescriptor.getQueueName(), correlationId ) );

					Result< Serializable, BPMActivityAttributes > result = resultBuilder.build();

					ctx.setCorrelationId( correlationId );

					if( isAlive() ) {
						sourceCallback.handle( result, ctx );
					} else {
						cancel( ctx );
					}

				} catch( InterruptedException e ) {

					stop();
					cancel( ctx );
					LOGGER.info( "Consumer for <bpm:listener> on flow '{}' was interrupted. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName() );

				} catch( Exception e ) {

					cancel( ctx );
					if( LOGGER.isErrorEnabled() ) {
						LOGGER.error( format( "Consumer for <bpm:listener> on flow '%s' found unexpected exception. Consuming will continue '", location.getRootContainerName() ), e );
					}

				}

			}

		}

		private void cancel( SourceCallbackContext ctx ) {
			/*try {
				ctx.getTransactionHandle().rollback();
			} catch( TransactionException e ) {
				if( LOGGER.isWarnEnabled() ) {
					LOGGER.warn( "Failed to rollback transaction: " + e.getMessage(), e );
				}
			}*/
			semaphore.release();
		}

		private boolean isAlive() {
			return !stop.get() && !currentThread().isInterrupted();
		}

		public void stop() {
			stop.set( true );
		}

	}

}
