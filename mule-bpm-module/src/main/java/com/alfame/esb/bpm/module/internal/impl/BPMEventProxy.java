package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.delegate.event.FlowableProcessEngineEvent;

public class BPMEventProxy extends BPMEngineEvent {

    private final FlowableProcessEngineEvent processEngineEvent;

    public BPMEventProxy(FlowableProcessEngineEvent processEngineEvent) {
        this.processEngineEvent = processEngineEvent;
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
        } else if (this.processEngineEvent.getType().equals(FlowableEngineEventType.TASK_CREATED)) {
            type = BPMEngineEventType.TASK_CREATED;
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

}
