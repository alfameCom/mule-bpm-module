package com.alfame.esb.bpm.api;

public interface BPMVariableInstance {

	/**
	 * The unique identifier of the variable.
	 */
	String getId();

	/**
	 * The id of the process definition of the process instance.
	 */
	String getProcessDefinitionId();

	/**
	 * The type of the variable
	 */
	String getTypeName();

	/**
	 * @return the name of the variable
	 */
	String getName();

	/**
	 * @return the process instance id of the variable
	 */
	String getProcessInstanceId();

	/**
	 * @return the execution id of the variable
	 */
	String getExecutionId();

	/**
	 * @return the scope id of the variable
	 */
	String getScopeId();

	/**
	 * @return the sub scope id of the variable
	 */
	String getSubScopeId();

	/**
	 * @return the scope type of the variable
	 */
	String getScopeType();

	/**
	 * @return the task id of the variable
	 */
	String getTaskId();

}
