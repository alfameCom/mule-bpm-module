package com.alfame.esb.bpm.api;

public interface BPMTask {

	/**
	 * The unique identifier of the execution.
	 */
	String getId();

	/**
	 * Returns the id of the activity where the execution currently is at. Returns
	 * null if the execution is not a 'leaf' execution (eg concurrent parent).
	 */
	String getActivityId();

	/**
	 * Id of the root of the execution tree representing the process instance. It is
	 * the same as {@link #getId()} if this execution is the process instance.
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

}