package com.alfame.esb.bpm.connector.internal.operations;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class BPMProcessFactoryProperties {

    @Parameter
    private String processDefinitionKey;

    @Parameter
    @Optional
    private String uniqueBusinessKey;

    @Parameter
    @Optional
    private String tenantId;

    @Parameter
    @Optional
    private String processName;

    @Parameter
    @Optional(defaultValue = "false")
    private boolean returnCollidedInstance;

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getUniqueBusinessKey() {
        return uniqueBusinessKey;
    }

    public void setUniqueBusinessKey(String uniqueBusinessKey) {
        this.uniqueBusinessKey = uniqueBusinessKey;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getProcessName() {
        return processName;
    }

    public boolean getReturnCollidedInstance() {
        return returnCollidedInstance;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

}
