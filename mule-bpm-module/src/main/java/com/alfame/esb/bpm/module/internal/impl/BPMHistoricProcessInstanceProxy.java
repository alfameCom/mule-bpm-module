package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import org.flowable.engine.history.HistoricProcessInstance;

import java.util.Date;

public class BPMHistoricProcessInstanceProxy implements BPMProcessInstance {

    private final HistoricProcessInstance historicProcessInstance;

    public BPMHistoricProcessInstanceProxy(HistoricProcessInstance historicProcessInstance) {
        this.historicProcessInstance = historicProcessInstance;
    }

    @Override
    public String getProcessDefinitionId() {
        return this.historicProcessInstance.getProcessDefinitionId();
    }

    @Override
    public String getProcessDefinitionName() {
        return this.historicProcessInstance.getProcessDefinitionName();
    }

    @Override
    public String getProcessDefinitionKey() {
        return this.historicProcessInstance.getProcessDefinitionKey();
    }

    @Override
    public Integer getProcessDefinitionVersion() {
        return this.historicProcessInstance.getProcessDefinitionVersion();
    }

    @Override
    public String getDeploymentId() {
        return this.historicProcessInstance.getDeploymentId();
    }

    @Override
    public String getCurrentActivityId() {
        return this.historicProcessInstance.getEndActivityId();
    }

    @Override
    public String getProcessInstanceId() {
        return this.historicProcessInstance.getId();
    }

    @Override
    public String getParentId() {
        return null;
    }

    @Override
    public String getSuperExecutionId() {
        return this.historicProcessInstance.getSuperProcessInstanceId();
    }

    @Override
    public String getTenantId() {
        return this.historicProcessInstance.getTenantId();
    }

    @Override
    public String getBusinessKey() {
        return this.historicProcessInstance.getBusinessKey();
    }

    @Override
    public String getName() {
        return this.historicProcessInstance.getName();
    }

    @Override
    public Date getStartTime() {
        return this.historicProcessInstance.getStartTime();
    }

    @Override
    public Date getEndTime() {
        return this.historicProcessInstance.getEndTime();
    }

}
