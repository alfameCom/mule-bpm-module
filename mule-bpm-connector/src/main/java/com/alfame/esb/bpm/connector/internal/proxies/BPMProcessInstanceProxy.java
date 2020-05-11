package com.alfame.esb.bpm.connector.internal.proxies;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.Date;

public class BPMProcessInstanceProxy implements BPMProcessInstance {

    private final ProcessInstance processInstance;

    public BPMProcessInstanceProxy(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    @Override
    public String getProcessDefinitionId() {
        return this.processInstance.getProcessDefinitionId();
    }

    @Override
    public String getProcessDefinitionName() {
        return this.processInstance.getProcessDefinitionName();
    }

    @Override
    public String getProcessDefinitionKey() {
        return this.processInstance.getProcessDefinitionKey();
    }

    @Override
    public Integer getProcessDefinitionVersion() {
        return this.processInstance.getProcessDefinitionVersion();
    }

    @Override
    public String getDeploymentId() {
        return this.processInstance.getDeploymentId();
    }

    @Override
    public String getCurrentActivityId() {
        return this.processInstance.getActivityId();
    }

    @Override
    public String getProcessInstanceId() {
        return this.processInstance.getProcessInstanceId();
    }

    @Override
    public String getParentId() {
        return this.processInstance.getParentId();
    }

    @Override
    public String getSuperExecutionId() {
        return this.processInstance.getSuperExecutionId();
    }

    @Override
    public String getTenantId() {
        return this.processInstance.getTenantId();
    }

    @Override
    public String getBusinessKey() {
        return this.processInstance.getBusinessKey();
    }

    @Override
    public String getName() {
        return this.processInstance.getName();
    }

    @Override
    public Date getStartTime() {
        return this.processInstance.getStartTime();
    }

    @Override
    public Date getEndTime() {
        return null;
    }

}
