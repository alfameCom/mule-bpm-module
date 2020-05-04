package com.alfame.esb.bpm.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class BPMEngineEventSubscriptionBuilder {

    protected List<BPMEngineEventType> eventTypes = new ArrayList<>();
    protected List<String> processDefinitionKeys = new ArrayList<>();
    protected List<String> processInstanceIds = new ArrayList<>();
    protected Map<String, Object> variableValues = new HashMap<>();

    public BPMEngineEventSubscriptionBuilder eventType(BPMEngineEventType eventType) {
        this.eventTypes.add(eventType);
        return this;
    }

    public BPMEngineEventSubscriptionBuilder processDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKeys.add(processDefinitionKey);
        return this;
    }

    public BPMEngineEventSubscriptionBuilder processInstanceId(String processInstanceId) {
        this.processInstanceIds.add(processInstanceId);
        return this;
    }

    public BPMEngineEventSubscriptionBuilder variable(String variable) {
        this.variableValues.put(variable, null);
        return this;
    }

    public BPMEngineEventSubscriptionBuilder variableWithValue(String variable, Object value) {
        this.variableValues.put(variable, value);
        return this;
    }

    public abstract BPMEngineEventSubscription subscribeForEvents();

    public abstract BPMEngineEventSubscription subscribeForEvents(BPMEngineEventListener engineEventListener);

}
