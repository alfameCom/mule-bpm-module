package com.alfame.esb.bpm.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BPMEngineEventFilter<T> {

    protected List<BPMEngineEventType> eventTypes = new ArrayList<>();
    protected List<String> processDefinitionKeys = new ArrayList<>();
    protected List<String> processInstanceIds = new ArrayList<>();
    protected List<String> activityNames = new ArrayList<>();
    protected Map<String, Object> variableValues = new HashMap<>();

    public T eventType(BPMEngineEventType eventType) {
        this.eventTypes.add(eventType);
        return (T) this;
    }

    public T processDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKeys.add(processDefinitionKey);
        return (T) this;
    }

    public T processInstanceId(String processInstanceId) {
        this.processInstanceIds.add(processInstanceId);
        return (T) this;
    }

    public T activityName(String activityName) {
        this.activityNames.add(activityName);
        return (T) this;
    }

    public T variable(String variable) {
        this.variableValues.put(variable, null);
        return (T) this;
    }

    public T variableWithValue(String variable, Object value) {
        this.variableValues.put(variable, value);
        return (T) this;
    }

    protected boolean isUnfilteredEvent(BPMEngineEvent engineEvent) {
        boolean isUnfilteredEvent = true;

        if (engineEvent == null) {
            isUnfilteredEvent = false;
        } else {
            // Filter events, if filter of any single type has been set, and any of those are not matching the event
            if (!isUnfilteredEventKey((List) this.eventTypes, engineEvent.getEventType())) {
                isUnfilteredEvent = false;
            } else if (!isUnfilteredEventKey((List) this.processDefinitionKeys, engineEvent.getProcessDefinitionKey())) {
                isUnfilteredEvent = false;
            } else if (!isUnfilteredEventKey((List) this.processInstanceIds, engineEvent.getProcessInstanceId())) {
                isUnfilteredEvent = false;
            } else if (!isUnfilteredEventKey((List) this.activityNames, engineEvent.getActivityName())) {
                isUnfilteredEvent = false;
            } else if (!isUnfilteredEventValue((Map) this.variableValues, engineEvent.getVariableName(), engineEvent.getVariableValue())) {
                isUnfilteredEvent = false;
            }
        }

        return isUnfilteredEvent;
    }

    private boolean isUnfilteredEventKey(List<Object> filterKeys, Object eventKey) {
        boolean isUnfiltered = true;

        // Some filters of this type have been set?
        if (filterKeys != null
                && !filterKeys.isEmpty()
                && eventKey != null) {
            // Filter non-matching events
            isUnfiltered = filterKeys.contains(eventKey);
        }

        return isUnfiltered;
    }

    private boolean isUnfilteredEventValue(Map<Object, Object> filterMap, Object eventKey, Object eventValue) {
        boolean isUnfiltered = true;

        // Some filters of this type have been set?
        if (filterMap != null
                && !filterMap.isEmpty()
                && eventKey != null) {
            // Filter non-matching events
            if (filterMap.containsKey(eventKey)) {
                // Filter events with non-matching values
                if (filterMap.get(eventKey) != null) {
                    isUnfiltered = filterMap.get(eventKey).equals(eventValue);
                }
            } else {
                isUnfiltered = false;
            }
        }

        return isUnfiltered;
    }

}