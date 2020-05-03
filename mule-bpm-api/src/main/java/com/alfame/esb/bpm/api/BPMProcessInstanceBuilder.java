package com.alfame.esb.bpm.api;

import java.util.Map;

public abstract class BPMProcessInstanceBuilder {

    protected String processDefinitionKey;
    protected String tenantId;
    protected String uniqueBusinessKey;
    protected String processInstanceName;
    protected Map<String, Object> variables;

    public BPMProcessInstanceBuilder tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public BPMProcessInstanceBuilder processDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
        return this;
    }

    public BPMProcessInstanceBuilder uniqueBusinessKey(String uniqueBusinessKey) {
        this.uniqueBusinessKey = uniqueBusinessKey;
        return this;
    }

    public BPMProcessInstanceBuilder processInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
        return this;
    }

    public BPMProcessInstanceBuilder variables(Map<String, Object> variables) {
        this.variables = variables;
        return this;
    }

    public abstract BPMProcessInstance startProcessInstance();

}
