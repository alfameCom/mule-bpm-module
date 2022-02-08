package com.alfame.esb.bpm.api;

import java.util.HashMap;
import java.util.Map;

public abstract class BPMProcessInstanceQueryBuilder {

    protected String processInstanceId;
    protected String processDefinitionKey;
    protected String tenantId;
    protected String uniqueBusinessKey;
    protected String processInstanceName;
    protected Map<String, Object> variables = new HashMap<>();
    protected boolean returnCollidedInstance;

    public BPMProcessInstanceQueryBuilder tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public BPMProcessInstanceQueryBuilder processInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public BPMProcessInstanceQueryBuilder processDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
        return this;
    }

    public BPMProcessInstanceQueryBuilder uniqueBusinessKey(String uniqueBusinessKey) {
        this.uniqueBusinessKey = uniqueBusinessKey;
        return this;
    }

    public BPMProcessInstanceQueryBuilder processInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
        return this;
    }

    public BPMProcessInstanceQueryBuilder variable(String variableName) {
        variableWithValue(variableName, null);
        return this;
    }

    public BPMProcessInstanceQueryBuilder variableWithValue(String variableName, Object value) {
        this.variables.put(variableName, value);
        return this;
    }

    public BPMProcessInstanceQueryBuilder returnCollidedInstance(boolean returnCollidedInstance) {
        this.returnCollidedInstance = returnCollidedInstance;
        return this;
    }

    public abstract BPMProcessInstanceQuery buildProcessInstanceQuery();

}
