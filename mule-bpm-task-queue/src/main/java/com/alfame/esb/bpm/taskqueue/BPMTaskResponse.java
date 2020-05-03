package com.alfame.esb.bpm.taskqueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPMTaskResponse {

    private final Object value;
    private final Throwable error;
    private Map<String, Object> variablesToUpdate = new HashMap<>();
    private List<String> variablesToRemove = new ArrayList<>();

    public BPMTaskResponse(Object value) {
        this.value = value;
        this.error = null;
    }

    public BPMTaskResponse(Throwable error) {
        this.value = null;
        this.error = error;
    }

    public BPMTaskResponse(Object value, Throwable error) {
        this.value = value;
        this.error = error;
    }

    public Object getValue() {
        return value;
    }

    public Throwable getError() {
        return error;
    }

    public Map<String, Object> getVariablesToUpdate() {
        return variablesToUpdate;
    }

    public void setVariablesToUpdate(Map<String, Object> variablesToUpdate) {
        this.variablesToUpdate = variablesToUpdate;
    }

    public List<String> getVariablesToRemove() {
        return variablesToRemove;
    }

    public void setVariablesToRemove(List<String> variablesToRemove) {
        this.variablesToRemove = variablesToRemove;
    }

}
