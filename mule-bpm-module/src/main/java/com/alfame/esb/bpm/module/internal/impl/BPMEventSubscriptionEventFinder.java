package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventFinder;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMEventSubscriptionEventFinder extends BPMEngineEventFinder {

    private final List<BPMEngineEvent> cachedEvents;

    public BPMEventSubscriptionEventFinder(List<BPMEngineEvent> cachedEvents) {
        this.cachedEvents = cachedEvents;
    }

    @Override
    public BPMEngineEvent uniqueEvent() {
        BPMEngineEvent event = null;
        List<BPMEngineEvent> events = events();

        if (events.size() == 1) {
            event = events.get(0);
        } else if (events.size() > 1) {
            throw new IndexOutOfBoundsException("Multiple events found");
        }

        return event;
    }

    @Override
    public List<BPMEngineEvent> events() {
        List<BPMEngineEvent> events = new ArrayList<>();

        for (BPMEngineEvent event : this.cachedEvents) {
            if (isUnfilteredEvent(event)) {
                events.add(event);
            }
        }

        return events;
    }


}
