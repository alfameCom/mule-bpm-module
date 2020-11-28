package com.alfame.esb.bpm.taskqueue;

import com.alfame.esb.bpm.api.BPMTaskInstance;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class BPMTask implements BPMTaskInstance, BPMTaskResponseCallback {

    private CompletableFuture<BPMTaskResponse> completableFuture = new CompletableFuture<>();

    private long requestTimeoutMillis = 300000;

    abstract public Object getPayload();

    abstract public Optional<String> getCorrelationId();

    public long getRequestTimeoutMillis() {
        return this.requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(long requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
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
        completableFuture.cancel(true);
    }

}
