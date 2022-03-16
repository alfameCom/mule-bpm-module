package com.alfame.esb.bpm.taskqueue;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.api.BPMTaskInstance;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class BPMTask implements BPMTaskInstance, BPMTaskResponseCallback {

    private CompletableFuture<BPMTaskResponse> completableFuture = new CompletableFuture<>();

    private long requestTimeoutMillis = 300000;
    private BPMTaskRollbackCallback rollbackCallback;
    private BPMTaskResponseCallback responseCallback;
    private Map<String, Object> variablesToUpdate = new HashMap<>();
    private List<String> variablesToRemove = new ArrayList<>();

    abstract public BPMProcessInstance getProcessInstance();

    abstract public Object getPayload();

    abstract public Optional<String> getCorrelationId();

    abstract public void applyCommandContext();

    public long getRequestTimeoutMillis() {
        return this.requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(long requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }

    public BPMTaskRollbackCallback getRollbackCallback() {
        return rollbackCallback;
    }

    public void setRollbackCallback(BPMTaskRollbackCallback rollbackCallback) {
        this.rollbackCallback = rollbackCallback;
    }

    public BPMTaskResponseCallback getResponseCallback() {
        return responseCallback;
    }

    public void setResponseCallback(BPMTaskResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
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

    public CompletableFuture<BPMTaskResponse> getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<BPMTaskResponse> completableFuture) {
        this.completableFuture = completableFuture;
    }

    public BPMTaskResponse waitForResponse() throws InterruptedException, ExecutionException, TimeoutException {
        return completableFuture.get(this.requestTimeoutMillis, TimeUnit.MILLISECONDS);
    }

    public BPMTaskResponse waitForResponse(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        this.requestTimeoutMillis = TimeUnit.MILLISECONDS.convert(timeout, unit);
        return completableFuture.get(this.requestTimeoutMillis, TimeUnit.MILLISECONDS);
    }

    public void submitResponse(BPMTaskResponse response) {
        if (!completableFuture.isCancelled()) {
            completableFuture.complete(response);
        } else {
            throw new IllegalStateException("Task has been already completed: instance " + this.getProcessInstanceId() + ": activity " + this.getActivityId());
        }
    }

    public void cancel() {
        if (rollbackCallback != null) {
            rollbackCallback.rollback();
        }
        completableFuture.cancel(true);
    }

}
