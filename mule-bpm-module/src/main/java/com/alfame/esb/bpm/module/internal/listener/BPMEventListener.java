package com.alfame.esb.bpm.module.internal.listener;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventSubscription;
import com.alfame.esb.bpm.api.BPMEngineEventSubscriptionBuilder;
import com.alfame.esb.bpm.api.BPMEngineEventType;
import com.alfame.esb.bpm.module.api.config.BPMEventSubscriptionEventTypeFilter;
import com.alfame.esb.bpm.module.api.config.BPMEventSubscriptionFilter;
import com.alfame.esb.bpm.module.api.param.BPMEventType;
import com.alfame.esb.bpm.module.internal.BPMExtension;
import com.alfame.esb.bpm.module.internal.connection.BPMConnection;
import com.alfame.esb.bpm.module.internal.impl.BPMEventSubscriptionBuilderImpl;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.api.scheduler.SchedulerConfig;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.api.tx.TransactionException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.MetadataScope;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.currentThread;
import static org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.slf4j.LoggerFactory.getLogger;

@Alias("event-listener")
@MetadataScope(outputResolver = BPMEventListenerOutputMetadataResolver.class, attributesResolver = BPMEventListenerAttributesMetadataResolver.class)
@EmitsResponse
@MediaType(value = ANY, strict = false)
public class BPMEventListener extends Source<Object, BPMEngineEvent> {

    private static final Logger LOGGER = getLogger(BPMEventListener.class);

    @ParameterGroup(name = "Endpoint")
    private BPMEventListenerEndpointDescriptor endpointDescriptor;

    @Parameter
    @Optional(defaultValue = "4")
    private int numberOfConsumers;

    @Config
    private BPMExtension config;

    @Connection
    private ConnectionProvider<BPMConnection> connectionProvider;
/*
    @Parameter
    private SourceTransactionalAction action;

    private Semaphore semaphore;

*/
    private Scheduler scheduler;

    @Inject
    private SchedulerConfig schedulerConfig;

    @Inject
    private SchedulerService schedulerService;

    private ComponentLocation location;

    private List<Consumer> consumers;

    @Override
    public void onStart(SourceCallback<Object, BPMEngineEvent> sourceCallback) throws MuleException {

        // TODO: Subscribe for all configured events
        BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder = this.config.eventSubscriptionBuilder();

        List<BPMEventSubscriptionFilter> eventSubscriptionFilters = this.endpointDescriptor.getEventSubscriptionFilters();
        if (eventSubscriptionFilters != null) {
            for (BPMEventSubscriptionFilter eventSubscriptionFilter : eventSubscriptionFilters) {
                if (eventSubscriptionFilter instanceof BPMEventSubscriptionEventTypeFilter) {
                    BPMEventSubscriptionEventTypeFilter eventTypeFilter = (BPMEventSubscriptionEventTypeFilter) eventSubscriptionFilter;
                    BPMEventType eventType = eventTypeFilter.getEventType();
                    if (eventType == BPMEventType.PROCESS_INSTANCE_CREATED) {
                        eventSubscriptionBuilder.eventType(BPMEngineEventType.PROCESS_INSTANCE_CREATED);
                    } else if (eventType == BPMEventType.PROCESS_INSTANCE_ENDED) {
                        eventSubscriptionBuilder.eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED);
                    }
                }
            }
        }
        BPMEngineEventSubscription eventSubscription = eventSubscriptionBuilder.subscribeForEvents();

        startConsumers(eventSubscription, sourceCallback);

    }

    @Override
    public void onStop() {

        if (consumers != null) {
            consumers.forEach(Consumer::stop);
        }

        if (scheduler != null) {
            scheduler.shutdownNow();
        }

    }

    /*@OnSuccess
    public void onSuccess(@ParameterGroup(name = "Response", showInDsl = true) BPMTaskListenerSuccessResponseBuilder responseBuilder, CorrelationInfo correlationInfo, SourceCallbackContext ctx) {

        LOGGER.debug("Submitting response after successful execution: " + responseValueAsString(responseBuilder.getValue()));
        BPMTaskResponse response = new BPMTaskResponse(responseValue(responseBuilder.getValue()));

        BPMConnection connection = ctx.getConnection();

        response.setVariablesToUpdate(connection.getVariablesToUpdate());
        response.setVariablesToRemove(connection.getVariablesToRemove());

        connection.getResponseCallback().submitResponse(response);

    }

    @OnError
    public void onError(@ParameterGroup(name = "Error Response", showInDsl = true) BPMTaskListenerErrorResponseBuilder errorResponseBuilder, Error error, CorrelationInfo correlationInfo, SourceCallbackContext ctx) {

        final ErrorType errorType = error != null ? error.getErrorType() : null;
        final String namespace = errorType != null ? (errorType.getNamespace() != null ? errorType.getNamespace() : "null") : "null";
        final String identifier = errorType != null ? (errorType.getIdentifier() != null ? errorType.getIdentifier() : "null") : "null";
        final String description = error != null ? (error.getDescription() != null ? error.getDescription() : "null") : "null";
        String msg = "" + namespace + ": " + identifier + ": " + description;
        LOGGER.error(msg);

        LOGGER.debug("Submitting response after erroneous execution: " + responseValueAsString(errorResponseBuilder.getValue()));
        BPMTaskResponse response = new BPMTaskResponse(responseValue(errorResponseBuilder.getValue()),
                error != null ? error.getCause() : null);

        BPMConnection connection = ctx.getConnection();
        connection.getResponseCallback().submitResponse(response);

    }

    @OnTerminate
    public void onTerminate() {
        if (semaphore != null) {
            semaphore.release();
        }
    }*/

    private void startConsumers(BPMEngineEventSubscription eventSubscription, SourceCallback<Object, BPMEngineEvent> sourceCallback) {
        createScheduler();
        consumers = new ArrayList<>(numberOfConsumers);
        //semaphore = new Semaphore(1, false);
        for (int i = 0; i < numberOfConsumers; i++) {
            final Consumer consumer = new Consumer(config, endpointDescriptor, eventSubscription, sourceCallback);
            consumers.add(consumer);
            scheduler.submit(consumer::start);
        }

    }

    private void createScheduler() {
        scheduler = schedulerService.customScheduler(schedulerConfig
                .withMaxConcurrentTasks(numberOfConsumers)
                .withName("bpm-event-flow" + location.getRootContainerName())
                .withWaitAllowed(true)
                .withShutdownTimeout(endpointDescriptor.getTimeout(), endpointDescriptor.getTimeoutUnit()));
    }

    /*private Serializable responseValue(TypedValue<Serializable> responseValue) {
        return responseValue != null ? responseValue.getValue() : null;
    }
    private String responseValueAsString(TypedValue<Serializable> responseValue) {
        Serializable value = responseValue(responseValue);
        return value != null ? value.toString() : null;
    }*/

    private class Consumer {

        private final BPMExtension config;
        private final BPMEventListenerEndpointDescriptor endpointDescription;
        private final BPMEngineEventSubscription eventSubscription;
        private final SourceCallback<Object, BPMEngineEvent> sourceCallback;
        private final AtomicBoolean stop = new AtomicBoolean(false);

        public Consumer(BPMExtension config, BPMEventListenerEndpointDescriptor endpointDescription, BPMEngineEventSubscription eventSubscription, SourceCallback<Object, BPMEngineEvent> sourceCallback) {
            this.config = config;
            this.endpointDescription = endpointDescription;
            this.eventSubscription = eventSubscription;
            this.sourceCallback = sourceCallback;
        }

        public void start() {
            LOGGER.debug("Consumer for <bpm:event-listener> on flow '{}' acquiring events. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());

            while (isAlive()) {

                SourceCallbackContext ctx = null;

                try {
                    // TODO: handle all events in cache!
                    BPMEngineEvent event = this.eventSubscription.
                            waitAndPopEvent(endpointDescription.getTimeout(), endpointDescriptor.getTimeoutUnit());

                    if (event == null) {
                        LOGGER.trace("Consumer for <bpm:event-listener> on flow '{}' acquired no activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                        continue;
                    } else {

                        //semaphore.acquire();
                        ctx = sourceCallback.createContext();
                        if (ctx == null) {
                            LOGGER.warn("Consumer for <bpm:event-listener> on flow '{}' no callback context. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                            stop();
                            cancel(null);
                            continue;
                        }

                        /*long taskTimeoutMillis = task.getRequestTimeoutMillis();
                        LOGGER.trace("Consumer for <bpm:event-listener> on flow '{}' uses activity timeout {} ms. Consuming for thread '{}'", location.getRootContainerName(), taskTimeoutMillis, currentThread().getName());
                        //LOGGER.trace("Consumer for <bpm:event-listener> on flow '{}' uses async executor timeout {} ms. Consuming for thread '{}'", location.getRootContainerName(), config.getAsyncExecutor(task.getTenantId()).getAsyncJobLockTimeInMillis(), currentThread().getName());
                        LOGGER.trace("Consumer for <bpm:event-listener> on flow '{}' uses endpoint timeout {} ms. Consuming for thread '{}'", location.getRootContainerName(), TimeUnit.MILLISECONDS.convert(endpointDescription.getTimeout(), endpointDescription.getTimeoutUnit()), currentThread().getName());
                        if (taskTimeoutMillis > config.getAsyncExecutor(task.getTenantId()).getAsyncJobLockTimeInMillis()) {
                            LOGGER.error("Consumer for <bpm:event-listener> on flow '{}' uses longer timeout than async executor supports. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                            cancel(ctx);
                            continue;
                        }

                        String correlationId = task.getCorrelationId().orElse(null);
                        if (correlationId == null || correlationId.isEmpty()) {
                            LOGGER.error("Consumer for <bpm:event-listener> on flow '{}' no correlation id available. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                            cancel(ctx);
                            continue;
                        } else {
                            LOGGER.trace("Consumer for <bpm:event-listener> on flow '{}' found correlation id '{}'. Consuming for thread '{}'", location.getRootContainerName(), correlationId, currentThread().getName());
                            ctx.setCorrelationId(correlationId);
                        }*/

                        LOGGER.trace("Consumer for <bpm:event-listener> on flow '{}' acquiring activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                        final BPMConnection connection = connect(ctx, event);
                        if (connection == null) {
                            LOGGER.warn("Consumer for <bpm:event-listener> on flow '{}' no connection provider available. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                            stop();
                            cancel(ctx);
                            continue;
                        }

                        LOGGER.trace("Consumer for <bpm:event-listener> on flow '{}' acquired activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                        //connection.setResponseCallback(task);

                        Result.Builder<Object, BPMEngineEvent> resultBuilder = Result.<Object, BPMEngineEvent>builder();
                        resultBuilder.output(event);
                        resultBuilder.mediaType(APPLICATION_JAVA);
                        resultBuilder.attributes(event);
                        resultBuilder.mediaType(APPLICATION_JAVA);

                        //task.applyCommandContext();
                        //semaphore.release();

                        Result<Object, BPMEngineEvent> result = resultBuilder.build();

                        //semaphore.acquire();
                        if (isAlive()) {
                            sourceCallback.handle(result, ctx);
                        } else {
                            cancel(ctx);
                        }
                        //semaphore.release();
                    }

                } catch (ConnectionException e) {

                    LOGGER.warn("Consumer for <bpm:event-listener> on flow '{}' is unable to connect. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName(), e);
                    stop();
                    cancel(ctx);
                } catch (InterruptedException e) {

                    LOGGER.debug("Consumer for <bpm:event-listener> on flow '{}' was interrupted. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                    stop();
                    cancel(ctx);

                } catch (Exception e) {

                    LOGGER.error("Consumer for <bpm:task-listener> on flow '{}' found unexpected exception. Consuming will continue for thread '{}'", location.getRootContainerName(), currentThread().getName(), e);
                    cancel(ctx);
                }

            }

        }

        private void cancel(SourceCallbackContext ctx) {
            try {
                if (ctx != null) {
                    ctx.getTransactionHandle().rollback();
                }
            } catch (TransactionException e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Failed to rollback transaction: " + e.getMessage(), e);
                }
            }

            try {
                if (ctx != null) {
                    connectionProvider.disconnect(ctx.getConnection());
                }
            } catch (IllegalStateException e) {
                // Not connected
            } catch (IllegalArgumentException e) {
                // Not connected
            } catch (Exception e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Failed to disconnect connection: " + e.getMessage(), e);
                }
            }

            //semaphore.release();
        }

        private BPMConnection connect(SourceCallbackContext ctx, BPMEngineEvent event) throws ConnectionException, TransactionException {
            BPMConnection connection = connectionProvider.connect();
            connection.setEvent(event);
            ctx.bindConnection(connection);
            return connection;
        }

        private boolean isAlive() {
            return !stop.get() && !currentThread().isInterrupted();
        }

        public void stop() {
            stop.set(true);
        }

    }

    /*public class TaskRollback implements BPMTaskRollbackCallback {

        final private TransactionHandle transactionHandle;

        public TaskRollback(TransactionHandle transactionHandle) {
            this.transactionHandle = transactionHandle;
        }

        @Override
        public void rollback() {
            try {
                transactionHandle.rollback();
            } catch (TransactionException e) {
                LOGGER.error("Error while rolling back task transaction", e);
            }
        }
    }*/

}
