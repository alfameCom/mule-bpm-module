package org.flowable.mule;

import com.alfame.esb.bpm.api.BPMVariableInstance;
import org.flowable.variable.api.persistence.entity.VariableInstance;

public class MuleDelegateVariableInstanceProxy implements BPMVariableInstance {

    private final VariableInstance variableInstance;

    public MuleDelegateVariableInstanceProxy(VariableInstance variableInstance) {
        this.variableInstance = variableInstance;
    }

    @Override
    public String getExecutionId() {
        return this.variableInstance.getExecutionId();
    }

    @Override
    public String getId() {
        return this.variableInstance.getId();
    }

    @Override
    public String getName() {
        return this.variableInstance.getName();
    }

    @Override
    public String getProcessDefinitionId() {
        return this.variableInstance.getProcessDefinitionId();
    }

    @Override
    public String getProcessInstanceId() {
        return this.variableInstance.getProcessInstanceId();
    }

    @Override
    public String getScopeId() {
        return this.variableInstance.getScopeId();
    }

    @Override
    public String getScopeType() {
        return this.variableInstance.getScopeType();
    }

    @Override
    public String getSubScopeId() {
        return this.variableInstance.getSubScopeId();
    }

    @Override
    public String getTaskId() {
        return this.variableInstance.getTaskId();
    }

    @Override
    public String getTypeName() {
        return this.variableInstance.getTypeName();
    }

    @Override
    public Object getValue() {
        return this.variableInstance.getValue();
    }

}
