package com.alfame.esb.bpm.module.internal.operations;

import com.alfame.esb.bpm.api.*;
import com.alfame.esb.bpm.module.api.config.*;
import com.alfame.esb.bpm.module.api.param.BPMEventType;
import com.alfame.esb.bpm.module.internal.BPMExtension;
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
            @Optional @Alias("event-filters") List<BPMEventSubscriptionFilter> eventSubscriptionFilters) {

        BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder =
                createEventSubscriptionBuilder(engine, eventSubscriptionFilters);

        BPMEngineEventSubscription engineEventSubscription = eventSubscriptionBuilder.subscribeForEvents();

        LOGGER.debug("Subscribed for events");

        return engineEventSubscription;
    }

    @Alias("wait-for-events")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMEventSubscriptionEventsOutputMetadataResolver.class)
    public List<BPMEngineEvent> waitAndUnsubscribeForEvents(
            @Config BPMExtension engine,
            @Alias("subscription") BPMEngineEventSubscription eventSubscription,
            int numberOfEvents,
            @Optional(defaultValue = "true") boolean unsubscribeWhenDone,
            @Optional(defaultValue = "5") @Placement(tab = "Advanced", order = 1) Long timeout,
            @Optional(defaultValue = "SECONDS") @Placement(tab = "Advanced", order = 2) TimeUnit timeoutUnit) throws InterruptedException {
        List<BPMEngineEvent> events = null;

        LOGGER.debug("Waiting for {} events", numberOfEvents);

        events = eventSubscription.waitForEvents(numberOfEvents, timeout, timeoutUnit);

        if (unsubscribeWhenDone) {
            eventSubscription.unsubscribeForEvents();
        }

        return events;
    }

    @Alias("get-unique-event")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMEventSubscriptionEventsOutputMetadataResolver.class)
    public BPMEngineEvent fetchUniqueSubscriptionEvent(
            @Config BPMExtension engine,
            @Alias("subscription") BPMEngineEventSubscription eventSubscription,
            @Optional @Alias("event-filters") List<BPMEventSubscriptionFilter> eventSubscriptionFilters) {

        BPMEngineEventFinder eventFinder = createEventFinder(eventSubscription, eventSubscriptionFilters);

        LOGGER.debug("Fetching unique event");

        return eventFinder.uniqueEvent();
    }

    @Alias("get-events")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMEventSubscriptionEventsOutputMetadataResolver.class)
    public List<BPMEngineEvent> fetchSubscriptionEvents(
            @Config BPMExtension engine,
            @Alias("subscription") BPMEngineEventSubscription eventSubscription,
            @Optional @Alias("event-filters") List<BPMEventSubscriptionFilter> eventSubscriptionFilters) {

        BPMEngineEventFinder eventFinder = createEventFinder(eventSubscription, eventSubscriptionFilters);

        LOGGER.debug("Fetching events");

        return eventFinder.events();
    }

    protected BPMEngineEventSubscriptionBuilder createEventSubscriptionBuilder(
            BPMExtension engine,
            List<BPMEventSubscriptionFilter> eventSubscriptionFilters) {

        BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder = engine.eventSubscriptionBuilder();

        if (eventSubscriptionFilters != null && !eventSubscriptionFilters.isEmpty()) {
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
                } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionActivityNameFilter) {
                    BPMEventSubscriptionActivityNameFilter activityNameFilter =
                            (BPMEventSubscriptionActivityNameFilter) eventSubscriptionFilter;
                    LOGGER.debug("Filtering events without activity name {}", activityNameFilter.getActivityName());
                    eventSubscriptionBuilder.activityName(activityNameFilter.getActivityName());
                } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionEventTypeFilter) {
                    BPMEventSubscriptionEventTypeFilter eventTypeFilter =
                            (BPMEventSubscriptionEventTypeFilter) eventSubscriptionFilter;
                    BPMEngineEventType engineEventType = mapEventType(eventTypeFilter.getEventType());
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
        }

        return eventSubscriptionBuilder;
    }

    protected BPMEngineEventFinder createEventFinder(
            BPMEngineEventSubscription eventSubscription,
            List<BPMEventSubscriptionFilter> eventSubscriptionFilters) {

        BPMEngineEventFinder eventFinder = eventSubscription.eventFinder();

        if (eventSubscriptionFilters != null && !eventSubscriptionFilters.isEmpty()) {
            for (BPMEventSubscriptionFilter eventSubscriptionFilter : eventSubscriptionFilters) {
                if (eventSubscriptionFilter instanceof BPMEventSubscriptionProcessDefinitionFilter) {
                    BPMEventSubscriptionProcessDefinitionFilter processDefinitionFilter =
                            (BPMEventSubscriptionProcessDefinitionFilter) eventSubscriptionFilter;
                    LOGGER.debug("Filtering events without process definition key {}", processDefinitionFilter.getKey());
                    eventFinder.processDefinitionKey(processDefinitionFilter.getKey());
                } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionProcessInstanceFilter) {
                    BPMEventSubscriptionProcessInstanceFilter processInstanceFilter =
                            (BPMEventSubscriptionProcessInstanceFilter) eventSubscriptionFilter;
                    LOGGER.debug("Filtering events without process instance id {}", processInstanceFilter.getProcessInstanceId());
                    eventFinder.processDefinitionKey(processInstanceFilter.getProcessInstanceId());
                } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionActivityNameFilter) {
                    BPMEventSubscriptionActivityNameFilter activityNameFilter =
                            (BPMEventSubscriptionActivityNameFilter) eventSubscriptionFilter;
                    LOGGER.debug("Filtering events without activity name {}", activityNameFilter.getActivityName());
                    eventFinder.activityName(activityNameFilter.getActivityName());
                } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionEventTypeFilter) {
                    BPMEventSubscriptionEventTypeFilter eventTypeFilter =
                            (BPMEventSubscriptionEventTypeFilter) eventSubscriptionFilter;
                    BPMEngineEventType engineEventType = mapEventType(eventTypeFilter.getEventType());
                    LOGGER.debug("Filtering events without event type {}", engineEventType);
                    eventFinder.eventType(engineEventType);
                } else if (eventSubscriptionFilter instanceof BPMEventSubscriptionVariableFilter) {
                    BPMEventSubscriptionVariableFilter variableFilter =
                            (BPMEventSubscriptionVariableFilter) eventSubscriptionFilter;
                    if (variableFilter.getValue() != null) {
                        LOGGER.debug("Filtering events without variables {} with value {}", variableFilter.getVariableName(), variableFilter.getValue());
                        eventFinder.variableWithValue(variableFilter.getVariableName(), variableFilter.getValue());
                    } else {
                        LOGGER.debug("Filtering events without variables {}", variableFilter.getVariableName());
                        eventFinder.variable(variableFilter.getVariableName());
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported filter");
                }
            }
        }

        return eventFinder;
    }

    protected BPMEngineEventType mapEventType(BPMEventType eventType) {
        BPMEngineEventType engineEventType;

        if (eventType == BPMEventType.PROCESS_INSTANCE_CREATED) {
            engineEventType = BPMEngineEventType.PROCESS_INSTANCE_CREATED;
        } else if (eventType == BPMEventType.PROCESS_INSTANCE_ENDED) {
            engineEventType = BPMEngineEventType.PROCESS_INSTANCE_ENDED;
        } else if (eventType == BPMEventType.VARIABLE_CREATED) {
            engineEventType = BPMEngineEventType.VARIABLE_CREATED;
        } else if (eventType == BPMEventType.VARIABLE_UPDATED) {
            engineEventType = BPMEngineEventType.VARIABLE_UPDATED;
        } else if (eventType == BPMEventType.VARIABLE_REMOVED) {
            engineEventType = BPMEngineEventType.VARIABLE_REMOVED;
        } else {
            engineEventType = BPMEngineEventType.UNKNOWN;
        }

        return engineEventType;
    }
}
