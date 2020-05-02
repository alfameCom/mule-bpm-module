package com.alfame.esb.bpm.connector.internal.listener;

import com.alfame.esb.bpm.api.BPMTaskInstance;
import com.alfame.esb.bpm.connector.internal.BPMExtension;
import com.alfame.esb.bpm.connector.internal.connection.BPMConnection;
import com.alfame.esb.bpm.taskqueue.BPMBaseTask;
import com.alfame.esb.bpm.taskqueue.BPMTaskQueue;
import com.alfame.esb.bpm.taskqueue.BPMTaskQueueFactory;
import com.alfame.esb.bpm.taskqueue.BPMTaskResponse;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.message.ErrorType;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.api.scheduler.SchedulerConfig;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.api.tx.TransactionException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.execution.OnError;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.execution.OnTerminate;
import org.mule.runtime.extension.api.annotation.metadata.MetadataScope;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.extension.api.tx.SourceTransactionalAction;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.currentThread;
import static org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.slf4j.LoggerFactory.getLogger;

@Alias("task-listener")
@MetadataScope(outputResolver = BPMTaskListenerOutputMetadataResolver.class, attributesResolver = BPMTaskListenerAttributesMetadataResolver.class)
@EmitsResponse
@MediaType(value = ANY, strict = false)
public class BPMTaskListener extends Source<Object, BPMTaskInstance> {

    private static final Logger LOGGER = getLogger(BPMTaskListener.class);

    @ParameterGroup(name = "Endpoint")
    private BPMTaskListenerEndpointDescriptor endpointDescriptor;

    @Parameter
    @Optional(defaultValue = "4")
    private int numberOfConsumers;

    @Config
    private BPMExtension config;

    @Connection
    private ConnectionProvider<BPMConnection> connectionProvider;

    @Parameter
    private SourceTransactionalAction action;

    private Scheduler scheduler;

    @Inject
    private SchedulerConfig schedulerConfig;

    @Inject
    private SchedulerService schedulerService;

    private ComponentLocation location;

    private List<Consumer> consumers;

    @Override
    public void onStart(SourceCallback<Object, BPMTaskInstance> sourceCallback) throws MuleException {

        startConsumers(sourceCallback);

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

    @OnSuccess
    public void onSuccess(@ParameterGroup(name = "Response", showInDsl = true) BPMTaskListenerSuccessResponseBuilder responseBuilder, CorrelationInfo correlationInfo, SourceCallbackContext ctx) {

        LOGGER.debug(responseBuilder.getValue() != null ?
                responseBuilder.getValue().getValue() != null ?
                        responseBuilder.getValue().getValue().toString() : null : null);
        BPMTaskResponse response = new BPMTaskResponse(
                responseBuilder.getValue() != null ? responseBuilder.getValue().getValue() : null);

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

        LOGGER.debug(errorResponseBuilder.getValue() != null ?
                errorResponseBuilder.getValue().getValue() != null ?
                        errorResponseBuilder.getValue().getValue().toString() : null : null);
        BPMTaskResponse response = new BPMTaskResponse(
                errorResponseBuilder.getValue() != null ? errorResponseBuilder.getValue().getValue() : null,
                error != null ? error.getCause() : null);

        BPMConnection connection = ctx.getConnection();
        connection.getResponseCallback().submitResponse(response);

    }

    @OnTerminate
    public void onTerminate() {
    }

    private void startConsumers(SourceCallback<Object, BPMTaskInstance> sourceCallback) {
        createScheduler();
        consumers = new ArrayList<>(numberOfConsumers);
        for (int i = 0; i < numberOfConsumers; i++) {
            final Consumer consumer = new Consumer(config, endpointDescriptor, sourceCallback);
            consumers.add(consumer);
            scheduler.submit(consumer::start);
        }

    }

    private void createScheduler() {
        scheduler = schedulerService.customScheduler(schedulerConfig
                .withMaxConcurrentTasks(numberOfConsumers)
                .withName("bpm-listener-flow" + location.getRootContainerName())
                .withWaitAllowed(true)
                .withShutdownTimeout(endpointDescriptor.getTimeout(), endpointDescriptor.getTimeoutUnit()));
    }

    private class Consumer {

        private final BPMExtension config;
        private final BPMTaskListenerEndpointDescriptor endpointDescription;
        private final SourceCallback<Object, BPMTaskInstance> sourceCallback;
        private final AtomicBoolean stop = new AtomicBoolean(false);

        public Consumer(BPMExtension config, BPMTaskListenerEndpointDescriptor endpointDescription, SourceCallback<Object, BPMTaskInstance> sourceCallback) {
            this.config = config;
            this.endpointDescription = endpointDescription;
            this.sourceCallback = sourceCallback;
        }

        public void start() {
            LOGGER.debug("Consumer for <bpm:task-listener> on flow '{}' acquiring activities for endpoint {}. Consuming for thread '{}'", location.getRootContainerName(), endpointDescriptor.getEndpointUrl(), currentThread().getName());

            while (isAlive()) {

                final SourceCallbackContext ctx = sourceCallback.createContext();
                if (ctx == null) {
                    LOGGER.warn("Consumer for <bpm:task-listener> on flow '{}' no callback context. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                    stop();
                    continue;
                }

                try {

                    final BPMTaskQueue queue = BPMTaskQueueFactory.getInstance(endpointDescriptor.getEndpointUrl());
                    BPMBaseTask task = queue.pop(endpointDescriptor.getTimeout(), endpointDescriptor.getTimeoutUnit());

                    if (task == null) {
                        LOGGER.trace("Consumer for <bpm:task-listener> on flow '{}' acquired no activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                        cancel(ctx);
                        continue;
                    } else {

                        long taskTimeoutMillis = task.getRequestTimeoutMillis();
                        LOGGER.trace("Consumer for <bpm:task-listener> on flow '{}' uses activity timeout {} ms. Consuming for thread '{}'", location.getRootContainerName(), taskTimeoutMillis, currentThread().getName());
                        LOGGER.trace("Consumer for <bpm:task-listener> on flow '{}' uses async executor timeout {} ms. Consuming for thread '{}'", location.getRootContainerName(), config.getAsyncExecutor(task.getTenantId()).getAsyncJobLockTimeInMillis(), currentThread().getName());
                        LOGGER.trace("Consumer for <bpm:task-listener> on flow '{}' uses endpoint timeout {} ms. Consuming for thread '{}'", location.getRootContainerName(), TimeUnit.MILLISECONDS.convert(endpointDescription.getTimeout(), endpointDescription.getTimeoutUnit()), currentThread().getName());
                        if (taskTimeoutMillis > config.getAsyncExecutor(task.getTenantId()).getAsyncJobLockTimeInMillis()) {
                            LOGGER.error("Consumer for <bpm:task-listener> on flow '{}' uses longer timeout than async executor supports. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                            cancel(ctx);
                            continue;
                        } else if (taskTimeoutMillis > TimeUnit.MILLISECONDS.convert(endpointDescription.getTimeout(), endpointDescription.getTimeoutUnit())) {
                            LOGGER.error("Consumer for <bpm:task-listener> on flow '{}' uses longer timeout than endpoint supports. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                            cancel(ctx);
                            continue;
                        }

                        String correlationId = task.getCorrelationId().orElse(null);
                        if (correlationId == null || correlationId.isEmpty()) {
                            LOGGER.error("Consumer for <bpm:task-listener> on flow '{}' no correlation id available. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                            cancel(ctx);
                            continue;
                        } else {
                            LOGGER.trace("Consumer for <bpm:task-listener> on flow '{}' found correlation id '{}'. Consuming for thread '{}'", location.getRootContainerName(), correlationId, currentThread().getName());
                            ctx.setCorrelationId(correlationId);
                        }

                        LOGGER.trace("Consumer for <bpm:task-listener> on flow '{}' acquiring activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                        final BPMConnection connection = connect(ctx, task);
                        if (connection == null) {
                            LOGGER.warn("Consumer for <bpm:task-listener> on flow '{}' no connection provider available. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                            stop();
                            cancel(ctx);
                            continue;
                        }

                        LOGGER.trace("Consumer for <bpm:task-listener> on flow '{}' acquired activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                        connection.setResponseCallback(task);

                        Result.Builder<Object, BPMTaskInstance> resultBuilder = Result.<Object, BPMTaskInstance>builder();
                        resultBuilder.output(task.getPayload());
                        resultBuilder.mediaType(APPLICATION_JAVA);
                        resultBuilder.attributes(task);
                        resultBuilder.mediaType(APPLICATION_JAVA);

                        Result<Object, BPMTaskInstance> result = resultBuilder.build();

                        if (isAlive()) {
                            sourceCallback.handle(result, ctx);
                        } else {
                            cancel(ctx);
                        }

                    }

                } catch (ConnectionException e) {

                    LOGGER.warn("Consumer for <bpm:task-listener> on flow '{}' is unable to connect. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName(), e);
                    stop();
                    cancel(ctx);
                } catch (InterruptedException e) {

                    LOGGER.debug("Consumer for <bpm:task-listener> on flow '{}' was interrupted. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
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
                connectionProvider.disconnect(ctx.getConnection());
            } catch (IllegalStateException e) {
                // Not connected
            } catch (IllegalArgumentException e) {
                // Not connected
            } catch (Exception e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Failed to disconnect connection: " + e.getMessage(), e);
                }
            }

            try {
                ctx.getTransactionHandle().rollback();
            } catch (TransactionException e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Failed to rollback transaction: " + e.getMessage(), e);
                }
            }
        }

        private BPMConnection connect(SourceCallbackContext ctx, BPMBaseTask task) throws ConnectionException, TransactionException {
            BPMConnection connection = connectionProvider.connect();
            connection.setTask(task);
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

}
