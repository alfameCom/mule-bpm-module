package com.alfame.esb.bpm.api;

import java.util.Date;

public interface BPMProcessInstance {

    /**
     * The id of the process definition of the process instance.
     */
    String getProcessDefinitionId();

    /**
     * The name of the process definition of the process instance.
     */
    String getProcessDefinitionName();

    /**
     * The key of the process definition of the process instance.
     */
    String getProcessDefinitionKey();

    /**
     * The version of the process definition of the process instance.
     */
    Integer getProcessDefinitionVersion();

    /**
     * The deployment id of the process definition of the process instance.
     */
    String getDeploymentId();

    /**
     * Returns the id of the activity where the execution currently is at. Returns
     * null if the execution is not a 'leaf' execution (eg concurrent parent).
     */
    String getCurrentActivityId();

    /**
     * Id of the root of the execution tree representing the process instance.
     */
    String getProcessInstanceId();

    /**
     * Gets the id of the parent of this execution. If null, the execution
     * represents a process-instance.
     */
    String getParentId();

    /**
     * Gets the id of the super execution of this execution.
     */
    String getSuperExecutionId();

    /**
     * The tenant identifier of this process instance
     */
    String getTenantId();

    /**
     * The business key of this process instance.
     */
    String getBusinessKey();

    /**
     * Returns the name of this process instance.
     */
    String getName();

    /**
     * Returns the start time of this process instance.
     */
    Date getStartTime();

    /**
     * Returns the end time of this process instance.
     */
    Date getEndTime();

}
