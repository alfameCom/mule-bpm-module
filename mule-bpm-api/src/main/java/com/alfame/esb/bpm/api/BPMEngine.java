package com.alfame.esb.bpm.api;

import java.io.InputStream;

public interface BPMEngine {

    String getName();
    String getDefaultTenantId();

    BPMProcessInstanceBuilder processInstanceBuilder();

    BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder();

    BPMVariableInstance getVariableInstance(String executionId, String variableName);
    BPMVariableInstance getHistoricVariableInstance(String executionId, String variableName);
    void setVariable(String executionId, String variableName, Object content);

    BPMAttachmentBuilder attachmentBuilder();
    InputStream getAttachmentContent(String attachmentId);

    void triggerSignal(String executionId, String signalName);

}
