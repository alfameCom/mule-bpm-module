package com.alfame.esb.connectors.bpm.internal.listener;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.slf4j.LoggerFactory.getLogger;

import com.alfame.esb.bpm.activity.queue.api.*;
import com.alfame.esb.connectors.bpm.internal.BPMQueueDescriptor;
import com.alfame.esb.connectors.bpm.internal.connection.BPMConnection;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.component.location.ConfigurationComponentLocator;
import org.mule.runtime.api.component.location.Location;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.message.ErrorType;
import org.mule.runtime.api.metadata.DataType;
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
import org.mule.runtime.extension.api.annotation.param.*;
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

	@Connection
	private ConnectionProvider< BPMConnection > connectionProvider;

	private Scheduler scheduler;

	@Inject
	private SchedulerConfig schedulerConfig;

	@Inject
	private SchedulerService schedulerService;

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
	public void onSuccess( @ParameterGroup( name = "Response", showInDsl = true ) BPMSuccessResponseBuilder responseBuilder, CorrelationInfo correlationInfo, SourceCallbackContext ctx ) {

		LOGGER.trace( (String)responseBuilder.getContent().getValue() );

		String payload = (String)responseBuilder.getContent().getValue();
		BPMActivityResponse response = new BPMActivityResponse( new TypedValue<>( payload, DataType.STRING) );

		BPMConnection connection = ctx.getConnection();
		connection.getResponseCallback().submitResponse( response );

	}

	@OnError
	public void onError( @ParameterGroup( name = "Error Response", showInDsl = true) BPMErrorResponseBuilder errorResponseBuilder, Error error, CorrelationInfo correlationInfo, SourceCallbackContext ctx ) {

		final ErrorType errorType = error.getErrorType();
		String msg = errorType.getNamespace() + ":" + errorType.getIdentifier() + ": " + error.getDescription();
		LOGGER.error( msg );

		BPMActivityResponse response = new BPMActivityResponse( error.getCause() );

		BPMConnection connection = ctx.getConnection();
		connection.getResponseCallback().submitResponse( response );

	}

	@OnTerminate
	public void onTerminate() {
	}

	private void startConsumers( SourceCallback< Serializable, BPMActivityAttributes > sourceCallback ) {
		createScheduler();
		consumers = new ArrayList<>( numberOfConsumers );
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

			while( isAlive() ) {

				SourceCallbackContext ctx = sourceCallback.createContext();
				if ( ctx == null ) {
					LOGGER.warn( "Consumer for <bpm:listener> on flow '{}' no callback context. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName() );
					stop();
					continue;
				}
				
				try {

					LOGGER.trace( "Consumer for <bpm:listener> on flow '{}' acquiring activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName() );
					final BPMConnection connection = connect( ctx );
					if ( connection == null ) {
						LOGGER.warn( "Consumer for <bpm:listener> on flow '{}' no connection provider available. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName() );
						stop();
						cancel( ctx );
						continue;
					}
					
					final BPMActivityQueue queue = BPMActivityQueueFactory.getInstance( queueDescriptor.getQueueName() );
					BPMActivity activity = queue.pop( queueDescriptor.getTimeout(), queueDescriptor.getTimeoutUnit() );
					
					if( activity == null ) {
						LOGGER.trace( "Consumer for <bpm:listener> on flow '{}' acquired no activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName() );
						cancel( ctx );
						continue;
					} else {
						LOGGER.debug( "Consumer for <bpm:listener> on flow '{}' acquired activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName() );
						connection.setResponseCallback( activity );

						String correlationId = activity.getCorrelationId().orElse( null );

						Result.Builder resultBuilder = Result.<Serializable, BPMActivityAttributes >builder();
						resultBuilder.output( activity.getValue() );
						resultBuilder.attributes( new BPMActivityAttributes( queueDescriptor.getQueueName(), correlationId ) );

						Result< Serializable, BPMActivityAttributes > result = resultBuilder.build();

						ctx.setCorrelationId( correlationId );

						if( isAlive() ) {
							sourceCallback.handle( result, ctx );
						} else {
							cancel( ctx );
						}

					}

				} catch( ConnectionException e ) {

					stop();
					cancel( ctx );
					LOGGER.warn( "Consumer for <bpm:listener> on flow '{}' is unable to connect. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName(), e );

				} catch( InterruptedException e ) {

					stop();
					cancel( ctx );
					LOGGER.warn( "Consumer for <bpm:listener> on flow '{}' was interrupted. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName(), e );

				} catch( Exception e ) {

					cancel( ctx );
					LOGGER.error( "Consumer for <bpm:listener> on flow '{}' found unexpected exception. Consuming will continue for thread '{}'", location.getRootContainerName(), currentThread().getName(), e );
				}

			}

		}

		private void cancel( SourceCallbackContext ctx ) {
			try {
				ctx.getTransactionHandle().rollback();
			} catch( TransactionException e ) {
				if( LOGGER.isWarnEnabled() ) {
					LOGGER.warn( "Failed to rollback transaction: " + e.getMessage(), e );
				}
			}
			
			try {
				connectionProvider.disconnect( ctx.getConnection() );
			} catch( IllegalStateException e) {
				// Not connected
			} catch( Exception e ) {
				if( LOGGER.isWarnEnabled() ) {
					LOGGER.warn( "Failed to disconnect connection: " + e.getMessage(), e );
				}
			}
		}

		private BPMConnection connect( SourceCallbackContext ctx ) throws ConnectionException, TransactionException {
			BPMConnection connection = connectionProvider.connect();
			ctx.bindConnection( connection );
			return connection;
		}

		private boolean isAlive() {
			return !stop.get() && !currentThread().isInterrupted();
		}

		public void stop() {
			stop.set( true );
		}

	}

}
