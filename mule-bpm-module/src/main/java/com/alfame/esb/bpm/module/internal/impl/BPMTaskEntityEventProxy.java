package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

public class BPMTaskEntityEventProxy extends BPMEngineEvent {

    private final FlowableEntityEvent entityEvent;
    private final TaskEntity taskEntity;

    public BPMTaskEntityEventProxy(FlowableEntityEvent entityEvent) {
        this.entityEvent = entityEvent;
        this.taskEntity = (TaskEntity) entityEvent.getEntity();
    }

    @Override
    public BPMEngineEventType getEventType() {
        BPMEngineEventType type = null;

        if (this.entityEvent.getType().equals(FlowableEngineEventType.TASK_CREATED)) {
            type = BPMEngineEventType.TASK_CREATED;
        } else if (this.entityEvent.getType().equals(FlowableEngineEventType.TASK_COMPLETED)) {
            type = BPMEngineEventType.TASK_COMPLETED;
        } else {
            type = BPMEngineEventType.UNKNOWN;
        }

        return type;
    }

    @Override
    public String getProcessDefinitionKey() {
        return this.taskEntity.getProcessDefinitionId().replaceFirst(":.*", "");
    }

    @Override
    public String getProcessInstanceId() {
        return this.taskEntity.getProcessInstanceId();
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
        return this.entityEvent.getEntity();
    }

}
