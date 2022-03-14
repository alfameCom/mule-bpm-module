package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.api.BPMVariableInstance;

public class BPMProcessInstanceVariableProxy implements BPMVariableInstance {

    private final BPMProcessInstance processInstance;
    private final String variableName;
    private final Object value;

    public BPMProcessInstanceVariableProxy(BPMProcessInstance processInstance, String variableName, Object value) {
        this.processInstance = processInstance;
        this.variableName = variableName;
        this.value = value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getProcessDefinitionId() {
        return this.processInstance.getProcessDefinitionId();
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public String getName() {
        return this.variableName;
    }

    @Override
    public String getProcessInstanceId() {
        return this.processInstance.getProcessInstanceId();
    }

    @Override
    public String getExecutionId() {
        return null;
    }

    @Override
    public String getScopeId() {
        return null;
    }

    @Override
    public String getSubScopeId() {
        return null;
    }

    @Override
    public String getScopeType() {
        return null;
    }

    @Override
    public String getTaskId() {
        return null;
    }
}
