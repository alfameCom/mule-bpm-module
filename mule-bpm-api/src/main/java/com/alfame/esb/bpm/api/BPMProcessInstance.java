package com.alfame.esb.bpm.api;

import java.text.MessageFormat;
import java.util.Date;

public abstract class BPMProcessInstance {

    /**
     * The id of the process definition of the process instance.
     */
    public abstract String getProcessDefinitionId();

    /**
     * The name of the process definition of the process instance.
     */
    public abstract String getProcessDefinitionName();

    /**
     * The key of the process definition of the process instance.
     */
    public abstract String getProcessDefinitionKey();

    /**
     * The version of the process definition of the process instance.
     */
    public abstract Integer getProcessDefinitionVersion();

    /**
     * The deployment id of the process definition of the process instance.
     */
    public abstract String getDeploymentId();

    /**
     * Returns the id of the activity where the execution currently is at. Returns
     * null if the execution is not a 'leaf' execution (eg concurrent parent).
     */
    public abstract String getCurrentActivityId();

    /**
     * Id of the root of the execution tree representing the process instance.
     */
    public abstract String getProcessInstanceId();

    /**
     * Gets the id of the parent of this execution. If null, the execution
     * represents a process-instance.
     */
    public abstract String getParentId();

    /**
     * Gets the id of the super execution of this execution.
     */
    public abstract String getSuperExecutionId();

    /**
     * The tenant identifier of this process instance
     */
    public abstract String getTenantId();

    /**
     * The business key of this process instance.
     */
    public abstract String getBusinessKey();

    /**
     * Returns the name of this process instance.
     */
    public abstract String getName();

    /**
     * Returns the start time of this process instance.
     */
    public abstract Date getStartTime();

    /**
     * Returns the end time of this process instance.
     */
    public abstract Date getEndTime();

    public abstract BPMVariableInstance getVariableInstance(String variableName);

    @Override
    public String toString() {
        return MessageFormat.format(
                "[processDefinitionKey: {0}, processInstanceId: {1}, businessKey: {2}]",
                getProcessDefinitionKey(), getProcessInstanceId(), getBusinessKey());
    }

}
