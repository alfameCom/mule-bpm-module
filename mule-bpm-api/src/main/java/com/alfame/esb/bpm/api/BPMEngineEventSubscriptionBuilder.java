package com.alfame.esb.bpm.api;

public abstract class BPMEngineEventSubscriptionBuilder extends BPMEngineEventFilter<BPMEngineEventSubscriptionBuilder> {

    public abstract BPMEngineEventSubscription subscribeForEvents();

    public abstract BPMEngineEventSubscription subscribeForEvents(BPMEngineEventListener engineEventListener);

}
