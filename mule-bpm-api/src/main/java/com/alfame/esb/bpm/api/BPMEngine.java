package com.alfame.esb.bpm.api;

import java.io.InputStream;
import java.util.Map;

public interface BPMEngine {

    String getName();
    String getTenantId();

    BPMProcessInstanceBuilder processInstanceBuilder();

    BPMProcessInstanceQueryBuilder processInstanceQueryBuilder();

    BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder();

    BPMVariableInstance getVariableInstance(String executionId, String variableName);
    BPMVariableInstance getHistoricVariableInstance(String executionId, String variableName);
    void setVariable(String executionId, String variableName, Object content);

    BPMAttachmentBuilder attachmentBuilder();
    InputStream getAttachmentContent(String attachmentId);
    void removeAttachment(String attachmentId);

    void triggerSignal(String executionId, String signalName);
    void deleteProcessInstance(String processInstanceId, String deleteReason);

    void completeTask(String taskId, String formDefinitionId, String outcome, Map<String, Object> variables);
}
