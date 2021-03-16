package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.taskqueue.BPMTask;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class BPMDummyMuleTask extends BPMTask {

    private final Object payload;
    private final String correlationId;

    BPMDummyMuleTask(Object payload, String correlationId) {
        this.payload = payload;
        this.correlationId = correlationId;
    }

    @Override
    public String getActivityId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<String> getCorrelationId() {
        return ofNullable(correlationId);
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getParentId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public String getProcessInstanceId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProcessDefinitionId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProcessDefinitionKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSuperExecutionId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTenantId() {
        // TODO Auto-generated method stub
        return null;
    }

}
