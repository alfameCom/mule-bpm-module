package com.alfame.esb.bpm.api;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface BPMEngineEventSubscription {

    public void unsubscribeForEvents();

    public List<BPMEngineEvent> waitForEvents(int numberOfEvents, long timeout, TimeUnit timeUnit) throws InterruptedException;

    public BPMEngineEventFinder eventFinder();

}