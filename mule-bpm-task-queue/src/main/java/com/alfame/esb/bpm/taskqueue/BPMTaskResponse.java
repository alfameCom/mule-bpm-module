package com.alfame.esb.bpm.taskqueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPMTaskResponse {

    private final Object value;
    private final Throwable throwable;
    private Map<String, Object> variablesToUpdate = new HashMap<>();
    private List<String> variablesToRemove = new ArrayList<>();

    public BPMTaskResponse(Object value) {
        this.value = value;
        this.throwable = null;
    }

    public BPMTaskResponse(Throwable throwable) {
        this.value = null;
        this.throwable = throwable;
    }

    public BPMTaskResponse(Object value, Throwable throwable) {
        this.value = value;
        this.throwable = throwable;
    }

    public Object getValue() {
        return value;
    }

    public Throwable getThrowable() {
        return throwable;
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
