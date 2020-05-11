package com.alfame.esb.bpm.api;

public abstract class BPMEngine {

    public abstract String getName();
    public abstract String getDefaultTenantId();

    public abstract BPMProcessInstanceBuilder processInstanceBuilder();

    public abstract BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder();

    public abstract BPMVariableInstance getVariableInstance(String executionId, String variableName);
    public abstract BPMVariableInstance getHistoricVariableInstance(String executionId, String variableName);
    public abstract void setVariable(String executionId, String variableName, Object content);

    public abstract BPMAttachmentBuilder attachmentBuilder();

    public abstract void triggerSignal(String executionId, String signalName);

}
