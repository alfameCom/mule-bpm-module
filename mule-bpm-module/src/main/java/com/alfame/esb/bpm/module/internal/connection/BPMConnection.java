package com.alfame.esb.bpm.module.internal.connection;

import com.alfame.esb.bpm.taskqueue.BPMTask;
import com.alfame.esb.bpm.taskqueue.BPMTaskResponseCallback;
import org.mule.runtime.api.tx.TransactionException;
import org.mule.runtime.extension.api.connectivity.TransactionalConnection;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMConnection implements TransactionalConnection {

    private static final Logger LOGGER = getLogger(BPMConnection.class);

    private BPMTaskResponseCallback responseCallback;
    private Map<String, Object> variablesToUpdate = new HashMap<>();
    private List<String> variablesToRemove = new ArrayList<>();
    private BPMTask task;

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

    public void setVariablesToDelete(List<String> variablesToRemove) {
    	this.variablesToRemove = variablesToRemove;
    }

    public BPMTask getTask() {
    	return task;
    }

    public void setTask(BPMTask task) {
    	this.task = task;
    }

    @Override
    public void begin() throws TransactionException {
        LOGGER.debug("BPMConnection transaction of activity {} beginning for instance {}", task.getActivityId(), task.getProcessInstanceId());
    }

    @Override
    public void commit() throws TransactionException {
        LOGGER.debug("BPMConnection transaction of activity {} committing for instance {}", task.getActivityId(), task.getProcessInstanceId());
    }

    @Override
    public void rollback() throws TransactionException {
        LOGGER.debug("BPMConnection transaction of activity {} rolling back for instance {}", task.getActivityId(), task.getProcessInstanceId());
    }

}
