package com.alfame.esb.bpm.connector.internal.impl;

import com.alfame.esb.bpm.api.BPMVariableInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;

public class BPMProcessHistoricVariableInstanceProxy implements BPMVariableInstance {

    private final HistoricVariableInstance historicVariableInstance;

    public BPMProcessHistoricVariableInstanceProxy(HistoricVariableInstance historicVariableInstance) {
        this.historicVariableInstance = historicVariableInstance;
    }

    @Override
    public String getExecutionId() {
        return this.historicVariableInstance.getProcessInstanceId();
    }

    @Override
    public String getId() {
        return this.historicVariableInstance.getId();
    }

    @Override
    public String getName() {
        return this.historicVariableInstance.getVariableName();
    }

    @Override
    public String getProcessDefinitionId() {
        return null; // Not available
    }

    @Override
    public String getProcessInstanceId() {
        return this.historicVariableInstance.getProcessInstanceId();
    }

    @Override
    public String getScopeId() {
        return this.historicVariableInstance.getScopeId();
    }

    @Override
    public String getScopeType() {
        return this.historicVariableInstance.getScopeType();
    }

    @Override
    public String getSubScopeId() {
        return this.historicVariableInstance.getSubScopeId();
    }

    @Override
    public String getTaskId() {
        return this.historicVariableInstance.getTaskId();
    }

    @Override
    public String getTypeName() {
        return this.historicVariableInstance.getVariableTypeName();
    }

    @Override
    public Object getValue() {
        return this.historicVariableInstance.getValue();
    }

}
