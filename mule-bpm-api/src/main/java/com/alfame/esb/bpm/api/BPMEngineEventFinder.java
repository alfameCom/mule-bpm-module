package com.alfame.esb.bpm.api;

import java.util.List;

public abstract class BPMEngineEventFinder extends BPMEngineEventFilter<BPMEngineEventFinder> {

    public abstract BPMEngineEvent uniqueEvent();

    public abstract List<BPMEngineEvent> events();

}