package com.alfame.esb.bpm.module.internal.listener;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventSubscription;
import com.alfame.esb.bpm.api.BPMEngineEventSubscriptionBuilder;
import com.alfame.esb.bpm.api.BPMEngineEventType;
import com.alfame.esb.bpm.module.api.config.*;
import com.alfame.esb.bpm.module.api.param.BPMEventType;
import com.alfame.esb.bpm.module.internal.BPMExtension;
import com.alfame.esb.bpm.module.internal.connection.BPMConnection;
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

    private Scheduler scheduler;

    @Inject
    private SchedulerConfig schedulerConfig;

    @Inject
    private SchedulerService schedulerService;

    private ComponentLocation location;

    private List<Consumer> consumers;

    @Override
    public void onStart(SourceCallback<Object, BPMEngineEvent> sourceCallback) throws MuleException {

        BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder = this.config.eventSubscriptionBuilder();

        List<BPMEventSubscriptionFilter> eventSubscriptionFilters = this.endpointDescriptor.getEventSubscriptionFilters();

        if (eventSubscriptionFilters != null) {
            handleEventSubscriptionFilters(eventSubscriptionBuilder, eventSubscriptionFilters);
        }


        BPMEngineEventSubscription eventSubscription = eventSubscriptionBuilder.subscribeForEvents();

        startConsumers(eventSubscription, sourceCallback);

    }

    private void handleEventSubscriptionFilters(BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder, List<BPMEventSubscriptionFilter> eventSubscriptionFilters ) {
        for (BPMEventSubscriptionFilter eventSubscriptionFilter : eventSubscriptionFilters) {
            if (eventSubscriptionFilter instanceof BPMEventSubscriptionEventTypeFilter) {
                BPMEventSubscriptionEventTypeFilter eventTypeFilter = (BPMEventSubscriptionEventTypeFilter) eventSubscriptionFilter;
                BPMEventType eventType = eventTypeFilter.getEventType();
                switch (eventType) {
                    case PROCESS_INSTANCE_CREATED: {
                        LOGGER.debug("Filtering events without event type {}", BPMEngineEventType.PROCESS_INSTANCE_CREATED);
                        eventSubscriptionBuilder.eventType(BPMEngineEventType.PROCESS_INSTANCE_CREATED);
                        break;
                    }
                    case PROCESS_INSTANCE_ENDED: {
                        LOGGER.debug("Filtering events without event type {}", BPMEngineEventType.PROCESS_INSTANCE_ENDED);
                        eventSubscriptionBuilder.eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED);
                        break;
                    }
                    case TASK_CREATED: {
                        LOGGER.debug("Filtering events without event type {}", BPMEngineEventType.TASK_CREATED);
                        eventSubscriptionBuilder.eventType(BPMEngineEventType.TASK_CREATED);
                        break;
                    }
                    case TASK_COMPLETED: {
                        LOGGER.debug("Filtering events without event type {}", BPMEngineEventType.TASK_COMPLETED);
                        eventSubscriptionBuilder.eventType(BPMEngineEventType.TASK_COMPLETED);
                        break;
                    }
                    default:
                        LOGGER.warn("Unsupported event type {}", eventType);
                }
            } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionProcessDefinitionFilter) {
                BPMEventSubscriptionProcessDefinitionFilter processDefinitionFilter =
                        (BPMEventSubscriptionProcessDefinitionFilter) eventSubscriptionFilter;
                LOGGER.debug("Filtering events without process definition key {}", processDefinitionFilter.getKey());
                eventSubscriptionBuilder.processDefinitionKey(processDefinitionFilter.getKey());
            } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionActivityNameFilter) {
                BPMEventSubscriptionActivityNameFilter activityNameFilter =
                        (BPMEventSubscriptionActivityNameFilter) eventSubscriptionFilter;
                LOGGER.info("Filtering events without activity name {}", activityNameFilter.getActivityName());
                eventSubscriptionBuilder.activityName(activityNameFilter.getActivityName());
            }
        }
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

    private void startConsumers(BPMEngineEventSubscription eventSubscription, SourceCallback<Object, BPMEngineEvent> sourceCallback) {
        createScheduler();
        consumers = new ArrayList<>(numberOfConsumers);
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
                    BPMEngineEvent event = this.eventSubscription.
                            waitAndPopEvent(endpointDescription.getTimeout(), endpointDescriptor.getTimeoutUnit());

                    if (event == null) {
                        LOGGER.trace("Consumer for <bpm:event-listener> on flow '{}' acquired no activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                    } else {

                        ctx = sourceCallback.createContext();
                        if (ctx == null) {
                            LOGGER.warn("Consumer for <bpm:event-listener> on flow '{}' no callback context. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                            stop();
                            cancel(null);
                            continue;
                        }

                        LOGGER.trace("Consumer for <bpm:event-listener> on flow '{}' acquiring activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                        final BPMConnection connection = connect(ctx, event);
                        if (connection == null) {
                            LOGGER.warn("Consumer for <bpm:event-listener> on flow '{}' no connection provider available. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
                            stop();
                            cancel(ctx);
                            continue;
                        }

                        LOGGER.trace("Consumer for <bpm:event-listener> on flow '{}' acquired activities. Consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());

                        Result<Object, BPMEngineEvent> result = buildResult(event);

                        if (isAlive()) {
                            sourceCallback.handle(result, ctx);
                        } else {
                            cancel(ctx);
                        }
                    }

                } catch (ConnectionException e) {

                    LOGGER.warn("Consumer for <bpm:event-listener> on flow '{}' is unable to connect. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName(), e);
                    stop();
                    cancel(ctx);
                } catch (InterruptedException e) {

                    LOGGER.debug("Consumer for <bpm:event-listener> on flow '{}' was interrupted. No more consuming for thread '{}'", location.getRootContainerName(), currentThread().getName());
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
        }

        private BPMConnection connect(SourceCallbackContext ctx, BPMEngineEvent event) throws ConnectionException, TransactionException {
            BPMConnection connection = connectionProvider.connect();
            connection.setEvent(event);
            ctx.bindConnection(connection);
            return connection;
        }

        private boolean isAlive() {
            return this.eventSubscription.hasEvents() || (!stop.get() && !currentThread().isInterrupted());
        }

        public void stop() {
            stop.set(true);
        }

        private Result<Object, BPMEngineEvent> buildResult(BPMEngineEvent event) {

            Object entity = event.getEntity();
            Result.Builder<Object, BPMEngineEvent> resultBuilder = Result.<Object, BPMEngineEvent>builder();

            if (entity != null) {
                resultBuilder.mediaType(APPLICATION_JAVA);
                resultBuilder.output(entity);
            }

            resultBuilder.attributesMediaType(APPLICATION_JAVA);
            resultBuilder.attributes(event);

            return resultBuilder.build();
        }

        
    }

}
