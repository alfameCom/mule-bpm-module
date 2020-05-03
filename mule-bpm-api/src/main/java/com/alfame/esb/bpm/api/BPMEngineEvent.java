package com.alfame.esb.bpm.api;

public interface BPMEngineEvent {

    /**
     * The type of this event.
     */
    BPMEngineEventType getType();

    /**
     * The key of the process definition of the process instance being target of this event.
     */
    String getProcessDefinitionKey();

    /**
     * Id of the root of the execution tree representing the process instance being target of this event.
     */
    String getProcessInstanceId();

    /**
     * The name of target variable, if the variable was target of this event.
     */
    String getVariableName();

    /**
     * The value of target variable, if variable was target of this event.
     */
    Object getVariableValue();

}