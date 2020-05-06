package com.alfame.esb.bpm.connector.internal.operations;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventSubscription;
import com.alfame.esb.bpm.api.BPMEngineEventSubscriptionBuilder;
import com.alfame.esb.bpm.api.BPMEngineEventType;
import com.alfame.esb.bpm.connector.api.config.*;
import com.alfame.esb.bpm.connector.api.param.BPMEventType;
import com.alfame.esb.bpm.connector.internal.BPMExtension;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMEventSubscriptionOperations {

    private static final Logger LOGGER = getLogger(BPMEventSubscriptionOperations.class);

    @Alias("event-subscription-builder")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMEventSubscriptionOutputMetadataResolver.class)
    public BPMEngineEventSubscription eventSubscriptionBuilder(
            @Config BPMExtension engine,
            @Optional @Alias("event-subscription-filters") List<BPMEventSubscriptionFilter> eventSubscriptionFilters) {

        BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder = engine.eventSubscriptionBuilder();

        for (BPMEventSubscriptionFilter eventSubscriptionFilter : eventSubscriptionFilters) {
            if (eventSubscriptionFilter instanceof BPMEventSubscriptionProcessDefinitionFilter) {
                BPMEventSubscriptionProcessDefinitionFilter processDefinitionFilter =
                        (BPMEventSubscriptionProcessDefinitionFilter) eventSubscriptionFilter;
                LOGGER.debug("Filtering events without process definition key {}", processDefinitionFilter.getKey());
                eventSubscriptionBuilder.processDefinitionKey(processDefinitionFilter.getKey());
            } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionProcessInstanceFilter) {
                BPMEventSubscriptionProcessInstanceFilter processInstanceFilter =
                        (BPMEventSubscriptionProcessInstanceFilter) eventSubscriptionFilter;
                LOGGER.debug("Filtering events without process instance id {}", processInstanceFilter.getProcessInstanceId());
                eventSubscriptionBuilder.processDefinitionKey(processInstanceFilter.getProcessInstanceId());
            } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionEventTypeFilter) {
                BPMEventSubscriptionEventTypeFilter eventTypeFilter =
                        (BPMEventSubscriptionEventTypeFilter) eventSubscriptionFilter;
                BPMEngineEventType engineEventType;
                if (eventTypeFilter.getEventType() == BPMEventType.processInstanceCreated) {
                    engineEventType = BPMEngineEventType.PROCESS_INSTANCE_CREATED;
                } else if (eventTypeFilter.getEventType() == BPMEventType.processInstanceEnded) {
                    engineEventType = BPMEngineEventType.PROCESS_INSTANCE_ENDED;
                } else if (eventTypeFilter.getEventType() == BPMEventType.variableCreated) {
                    engineEventType = BPMEngineEventType.VARIABLE_CREATED;
                } else if (eventTypeFilter.getEventType() == BPMEventType.variableUpdated) {
                    engineEventType = BPMEngineEventType.VARIABLE_UPDATED;
                } else if (eventTypeFilter.getEventType() == BPMEventType.variableRemoved) {
                    engineEventType = BPMEngineEventType.VARIABLE_REMOVED;
                } else {
                    engineEventType = BPMEngineEventType.UNKNOWN;
                }
                LOGGER.debug("Filtering events without event type {}", engineEventType);
                eventSubscriptionBuilder.eventType(engineEventType);
            } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionVariableFilter) {
                BPMEventSubscriptionVariableFilter variableFilter =
                        (BPMEventSubscriptionVariableFilter) eventSubscriptionFilter;
                if (variableFilter.getValue() != null) {
                    LOGGER.debug("Filtering events without variables {} with value {}", variableFilter.getVariableName(), variableFilter.getValue());
                    eventSubscriptionBuilder.variableWithValue(variableFilter.getVariableName(), variableFilter.getValue());
                } else {
                    LOGGER.debug("Filtering events without variables {}", variableFilter.getVariableName());
                    eventSubscriptionBuilder.variable(variableFilter.getVariableName());
                }
            } else {
                throw new IllegalArgumentException("Unsupported filter");
            }
        }

        BPMEngineEventSubscription engineEventSubscription = eventSubscriptionBuilder.subscribeForEvents();

        LOGGER.debug("Subscribed for events");

        return engineEventSubscription;
    }

    @Alias("wait-events-and-unsubscribe")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMEventSubscriptionEventsOutputMetadataResolver.class)
    public List<BPMEngineEvent> waitAndUnsubscribeForEvents(
            @Config BPMExtension engine,
            @Alias("subscription") BPMEngineEventSubscription eventSubscription,
            int numberOfEvents,
            @Optional(defaultValue = "5") @Placement(tab = "Advanced", order = 1) Long timeout,
            @Optional(defaultValue = "SECONDS") @Placement(tab = "Advanced", order = 2) TimeUnit timeoutUnit) throws InterruptedException {

        LOGGER.debug("Waiting for {} events", numberOfEvents);

        return eventSubscription.waitAndUnsubscribeForEvents(numberOfEvents, timeout, timeoutUnit);
    }

    @Alias("get-unique-event-from-subscription")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMEventSubscriptionEventsOutputMetadataResolver.class)
    public BPMEngineEvent fetchUniqueSubscriptionEvent(
            @Config BPMExtension engine,
            @Alias("subscription") BPMEngineEventSubscription eventSubscription,
            @Optional @Alias("event-subscription-filters") List<BPMEventSubscriptionFilter> eventSubscriptionFilters) throws InterruptedException {

        LOGGER.debug("Fetching unique event");

        return eventSubscription.uniqueEventByEventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED);
    }

}
