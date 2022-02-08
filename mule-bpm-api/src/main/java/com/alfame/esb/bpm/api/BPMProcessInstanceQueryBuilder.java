package com.alfame.esb.bpm.api;

import java.util.HashMap;
import java.util.Map;

public abstract class BPMProcessInstanceQueryBuilder {

    protected String processInstanceId;
    protected String processDefinitionKey;
    protected String tenantId;
    protected String uniqueBusinessKeyLike;
    protected String processInstanceName;
    protected Map<String, String> variablesLike = new HashMap<>();

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

    public BPMProcessInstanceQueryBuilder uniqueBusinessKeyLike(String uniqueBusinessKeyLike) {
        this.uniqueBusinessKeyLike = uniqueBusinessKeyLike;
        return this;
    }

    public BPMProcessInstanceQueryBuilder processInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
        return this;
    }

    public BPMProcessInstanceQueryBuilder variable(String variableName) {
        variableWithValueLike(variableName, null);
        return this;
    }

    public BPMProcessInstanceQueryBuilder variableWithValueLike(String variableName, String value) {
        this.variablesLike.put(variableName, value);
        return this;
    }

    public abstract BPMProcessInstanceQuery buildProcessInstanceQuery();

}
