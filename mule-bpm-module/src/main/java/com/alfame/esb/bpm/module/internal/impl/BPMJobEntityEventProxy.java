package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.impl.event.FlowableEntityExceptionEventImpl;
import org.flowable.job.service.impl.persistence.entity.JobEntity;

public class BPMJobEntityEventProxy extends BPMEngineEvent {

    private final FlowableEntityEvent entityEvent;
    private final JobEntity jobEntity;

    public BPMJobEntityEventProxy(FlowableEntityEvent entityEvent) {
        this.entityEvent = entityEvent;
        this.jobEntity = (JobEntity) entityEvent.getEntity();
        // FlowableEntityExceptionEventImpl.getEntity().getExceptionMessage() is null after first failure, need to set it manually
        if (entityEvent instanceof FlowableEntityExceptionEventImpl) {
            this.jobEntity.setExceptionMessage(((FlowableEntityExceptionEventImpl) entityEvent).getCause().getMessage());
        }
    }

    @Override
    public BPMEngineEventType getEventType() {
        BPMEngineEventType type = null;

        if (this.entityEvent.getType().equals(FlowableEngineEventType.JOB_EXECUTION_FAILURE)) {
            type = BPMEngineEventType.ACTIVITY_FAILURE;
        } else {
            type = BPMEngineEventType.UNKNOWN;
        }

        return type;
    }

    @Override
    public String getProcessDefinitionKey() {
        return this.jobEntity.getProcessDefinitionId().replaceFirst(":.*", "");
    }

    @Override
    public String getProcessInstanceId() {
        return this.jobEntity.getProcessInstanceId();
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
        return this.jobEntity.getElementId();
    }

    @Override
    public String getExceptionMessage() {
        return this.jobEntity.getExceptionMessage();
    }

}
