package com.alfame.esb.bpm.api;

import java.text.MessageFormat;

public abstract class BPMEngineEvent {

    /**
     * The type of this event.
     */
    public abstract BPMEngineEventType getEventType();

    /**
     * The key of the process definition of the process instance being target of this event.
     */
    public abstract String getProcessDefinitionKey();

    /**
     * Id of the root of the execution tree representing the process instance being target of this event.
     */
    public abstract String getProcessInstanceId();

    /**
     * The name of target variable, if the variable was target of this event.
     */
    public abstract String getVariableName();

    /**
     * The value of target variable, if variable was target of this event.
     */
    public abstract Object getVariableValue();

    @Override
    public String toString() {
        return MessageFormat.format(
                "[eventType: {0}, processDefinitionKey: {1}, processInstanceId: {2}, variable: {3} = {4}]",
                getEventType(), getProcessDefinitionKey(), getProcessInstanceId(), getVariableName(), getVariableValue());
    }

}