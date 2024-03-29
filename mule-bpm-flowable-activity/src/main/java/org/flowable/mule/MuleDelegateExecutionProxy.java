package org.flowable.mule;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.api.BPMVariableInstance;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.slf4j.Logger;

import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

public class MuleDelegateExecutionProxy extends BPMProcessInstance {

    private static final Logger LOGGER = getLogger(MuleDelegateExecutionProxy.class);

    private final DelegateExecution delegateExecution;

    public MuleDelegateExecutionProxy(DelegateExecution delegateExecution) {
        this.delegateExecution = delegateExecution;
    }

    @Override
    public String getProcessDefinitionId() {
        return this.delegateExecution.getProcessDefinitionId();
    }

    @Override
    public String getProcessDefinitionName() {
        return null;
    }

    @Override
    public String getProcessDefinitionKey() {
        return this.delegateExecution.getProcessDefinitionId().replaceFirst(":.*", "");
    }

    @Override
    public Integer getProcessDefinitionVersion() {
        return null;
    }

    @Override
    public String getDeploymentId() {
        return null;
    }

    @Override
    public String getCurrentActivityId() {
        return this.delegateExecution.getCurrentActivityId();
    }

    @Override
    public String getProcessInstanceId() {
        return this.delegateExecution.getProcessInstanceId();
    }

    @Override
    public String getParentId() {
        return this.delegateExecution.getParentId();
    }

    @Override
    public String getSuperExecutionId() {
        return this.delegateExecution.getSuperExecutionId();
    }

    @Override
    public String getTenantId() {
        return this.delegateExecution.getTenantId();
    }

    @Override
    public String getBusinessKey() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Date getStartTime() {
        return null;
    }

    @Override
    public Date getEndTime() {
        return null;
    }

    @Override
    public BPMVariableInstance getVariableInstance(String variableName) {
        BPMVariableInstance variableInstance = null;

        VariableInstance contextVariableInstance = null;
        try {
            contextVariableInstance = this.delegateExecution.getVariableInstance(variableName, false);
            if (contextVariableInstance != null) {
                LOGGER.debug("Found variable {} from execution context", variableName);
                variableInstance = new MuleDelegateVariableInstanceProxy(contextVariableInstance);
            }
        } catch (FlowableException e) {
            if(e.getMessage().equals("lazy loading outside command context")) {
                LOGGER.debug("Retrieval of variable {} was interrupted by command context switch", variableName);
                variableInstance = null;
            } else {
                throw e;
            }
        }

        return variableInstance;
    }

}
