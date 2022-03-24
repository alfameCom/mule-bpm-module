package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.engine.delegate.event.FlowableProcessEngineEvent;

public class BPMEventProxy extends BPMEngineEvent {

    private final FlowableProcessEngineEvent processEngineEvent;
    private final FlowableEntityEvent entityEvent;

    public BPMEventProxy(FlowableProcessEngineEvent processEngineEvent) {
        this.processEngineEvent = processEngineEvent;
        if (processEngineEvent instanceof FlowableEntityEvent) {
            this.entityEvent = (FlowableEntityEvent) processEngineEvent;
        } else {
            this.entityEvent = null;
        }
    }

    @Override
    public BPMEngineEventType getEventType() {
        BPMEngineEventType type = null;

        if (this.processEngineEvent.getType().equals(FlowableEngineEventType.PROCESS_CREATED)) {
            type = BPMEngineEventType.PROCESS_INSTANCE_CREATED;
        } else if (this.processEngineEvent.getType().equals(FlowableEngineEventType.PROCESS_COMPLETED)) {
            type = BPMEngineEventType.PROCESS_INSTANCE_ENDED;
        } else if (this.processEngineEvent.getType().equals(FlowableEngineEventType.ENGINE_CREATED)) {
            type = BPMEngineEventType.ENGINE_STARTED;
        } else if (this.processEngineEvent.getType().equals(FlowableEngineEventType.ENGINE_CLOSED)) {
            type = BPMEngineEventType.ENGINE_STOPPED;
        } else {
            type = BPMEngineEventType.UNKNOWN;
        }

        return type;
    }

    @Override
    public String getProcessDefinitionKey() {
        String processDefinitionKey = processEngineEvent.getProcessDefinitionId();
        if (processDefinitionKey != null) {
            processDefinitionKey = processDefinitionKey.replaceFirst(":.*", "");
        }
        return processDefinitionKey;
    }

    @Override
    public String getProcessInstanceId() {
        return this.processEngineEvent.getExecutionId();
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
        return null;
    }

    @Override
    public String getExceptionMessage() {
        return null;
    }

    @Override
    public Object getEntity() {
        Object entity = null;

        if (this.entityEvent != null) {
            entity = this.entityEvent.getEntity();
        }

        return entity;
    }

}
