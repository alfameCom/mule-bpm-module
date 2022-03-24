package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.delegate.event.FlowableProcessEngineEvent;

public class BPMActivityEventProxy extends BPMEngineEvent {

    private final FlowableProcessEngineEvent engineEvent;

    public BPMActivityEventProxy(FlowableProcessEngineEvent engineEvent) {
        this.engineEvent = engineEvent;
    }

    @Override
    public BPMEngineEventType getEventType() {
        BPMEngineEventType type = null;

        if (this.engineEvent.getType().equals(FlowableEngineEventType.ACTIVITY_STARTED)) {
            type = BPMEngineEventType.ACTIVITY_STARTED;
        } else if (this.engineEvent.getType().equals(FlowableEngineEventType.ACTIVITY_COMPLETED)) {
            type = BPMEngineEventType.ACTIVITY_COMPLETED;
        } else {
            type = BPMEngineEventType.UNKNOWN;
        }

        return type;
    }

    @Override
    public String getProcessDefinitionKey() {
        return this.engineEvent.getProcessDefinitionId().replaceFirst(":.*", "");
    }

    @Override
    public String getProcessInstanceId() {
        return this.engineEvent.getProcessInstanceId();
    }

    @Override
    public String getVariableName() {
        return null;
    }

    @Override
    public Object getVariableValue() {
        return null;
    }

    @Override
    public String getActivityName() {
        return engineEvent.getExecution() != null ? engineEvent.getExecution().getCurrentActivityId() : null;
    }

    @Override
    public String getExceptionMessage() {
        return null;
    }

    @Override
    public Object getEntity() {
        return this.engineEvent.getExecution();
    }

}
