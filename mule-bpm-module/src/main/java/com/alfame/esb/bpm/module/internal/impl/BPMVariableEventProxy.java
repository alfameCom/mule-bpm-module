package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.variable.api.event.FlowableVariableEvent;

public class BPMVariableEventProxy extends BPMEngineEvent {

    private final FlowableVariableEvent variableEvent;

    public BPMVariableEventProxy(FlowableVariableEvent variableEvent) {
        this.variableEvent = variableEvent;
    }

    @Override
    public BPMEngineEventType getEventType() {
        BPMEngineEventType type = null;

        if (this.variableEvent.getType().equals(FlowableEngineEventType.VARIABLE_CREATED)) {
            type = BPMEngineEventType.VARIABLE_CREATED;
        } else if (this.variableEvent.getType().equals(FlowableEngineEventType.VARIABLE_UPDATED)) {
            type = BPMEngineEventType.VARIABLE_UPDATED;
        } else if (this.variableEvent.getType().equals(FlowableEngineEventType.VARIABLE_DELETED)) {
            type = BPMEngineEventType.VARIABLE_REMOVED;
        } else {
            type = BPMEngineEventType.UNKNOWN;
        }

        return type;
    }

    @Override
    public String getProcessDefinitionKey() {
        return this.variableEvent.getProcessDefinitionId().replaceFirst(":.*", "");
    }

    @Override
    public String getProcessInstanceId() {
        return this.variableEvent.getProcessInstanceId();
    }

    @Override
    public String getVariableName() {
        return this.variableEvent.getVariableName();
    }

    @Override
    public Object getVariableValue() {
        return this.variableEvent.getVariableValue();
    }

}
