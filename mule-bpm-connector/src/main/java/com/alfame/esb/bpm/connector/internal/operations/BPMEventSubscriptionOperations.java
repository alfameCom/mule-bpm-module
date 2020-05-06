package com.alfame.esb.bpm.connector.internal.operations;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventSubscription;
import com.alfame.esb.bpm.api.BPMEngineEventSubscriptionBuilder;
import com.alfame.esb.bpm.api.BPMEngineEventType;
import com.alfame.esb.bpm.connector.api.config.BPMEventSubscriptionFilter;
import com.alfame.esb.bpm.connector.internal.BPMExtension;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
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
            @Optional @Alias("event-subscription-filters") List<BPMEventSubscriptionFilter> eventSubscriptionFilter) {

        BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder = engine.eventSubscriptionBuilder();

        BPMEngineEventSubscription engineEventSubscription = eventSubscriptionBuilder.subscribeForEvents();

        LOGGER.debug("Subscribed for events");

        return engineEventSubscription;
    }

    @Alias("wait-events-and-unsubscribe")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMEventSubscriptionEventsOutputMetadataResolver.class)
    public List<BPMEngineEvent> waitAndUnsubscribeForEvents(
            @Config BPMExtension engine,
            @Alias("subscription") BPMEngineEventSubscription eventSubscription) throws InterruptedException {

        LOGGER.debug("Unsubscribing for events");

        return eventSubscription.waitAndUnsubscribeForEvents(0, 1, TimeUnit.SECONDS);
    }

    @Alias("get-unique-event-from-subscription")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMEventSubscriptionEventsOutputMetadataResolver.class)
    public BPMEngineEvent fetchUniqueSubscriptionEvent(
            @Config BPMExtension engine,
            @Alias("subscription") BPMEngineEventSubscription eventSubscription,
            @Optional @Alias("event-subscription-filters") List<BPMEventSubscriptionFilter> eventSubscriptionFilter) throws InterruptedException {

        LOGGER.debug("Fetching unique event");

        return eventSubscription.uniqueEventByEventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED);
    }

}
