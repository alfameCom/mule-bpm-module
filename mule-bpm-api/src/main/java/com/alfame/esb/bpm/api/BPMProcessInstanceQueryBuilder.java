package com.alfame.esb.bpm.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class BPMProcessInstanceQueryBuilder {

    protected String processInstanceId;
    protected String processDefinitionKey;
    protected String tenantId;
    protected String uniqueBusinessKeyLike;
    protected String processInstanceNameLike;
    protected Date startedAfter;
    protected Date startedBefore;
    protected Date finishedAfter;
    protected Date finishedBefore;
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

    public BPMProcessInstanceQueryBuilder processInstanceNameLike(String processInstanceNameLike) {
        this.processInstanceNameLike = processInstanceNameLike;
        return this;
    }

    public BPMProcessInstanceQueryBuilder startedAfter(Date startedAfter) {
        this.startedAfter = startedAfter;
        return this;
    }

    public BPMProcessInstanceQueryBuilder startedBefore(Date startedBefore) {
        this.startedBefore = startedBefore;
        return this;
    }

    public BPMProcessInstanceQueryBuilder finishedAfter(Date finishedAfter) {
        this.finishedAfter = finishedAfter;
        return this;
    }

    public BPMProcessInstanceQueryBuilder finishedBefore(Date finishedBefore) {
        this.finishedBefore = finishedBefore;
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
