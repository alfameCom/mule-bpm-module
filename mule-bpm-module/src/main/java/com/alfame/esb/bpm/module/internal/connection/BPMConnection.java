package com.alfame.esb.bpm.module.internal.connection;

import com.alfame.esb.bpm.taskqueue.BPMTask;
import com.alfame.esb.bpm.taskqueue.BPMTaskResponseCallback;
import org.mule.runtime.api.tx.TransactionException;
import org.mule.runtime.extension.api.connectivity.TransactionalConnection;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.slf4j.Logger;

import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMConnection implements TransactionalConnection {

    private static final Logger LOGGER = getLogger(BPMConnection.class);

    private BPMTaskResponseCallback responseCallback;
    private Map<String, Object> variablesToUpdate = new HashMap<>();
    private List<String> variablesToRemove = new ArrayList<>();

    private BPMTask task;

    private final Map<String, BPMConnection> connectionCache;

    public BPMConnection(Map<String, BPMConnection> connectionCache) {
        this.connectionCache = connectionCache;
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

    public void setVariablesToDelete(List<String> variablesToRemove) {
        this.variablesToRemove = variablesToRemove;
    }

    public BPMTask getTask() {
    	return task;
    }

    public BPMConnection joinIfForked(CorrelationInfo correlationInfo) {
        BPMConnection connection = this;
        if (connection.getTask() == null) {
            BPMConnection cachedConnection = this.connectionCache.get(correlationInfo.getCorrelationId());
            if (cachedConnection != null) {
                connection = cachedConnection;
                LOGGER.debug("{} of activity {} joined {} for instance {}", this, connection.getTask().getActivityId(), connection, connection.getTask().getProcessInstanceId());
            }
        }
        if (connection.getTask() == null) {
            throw new IllegalStateException("No active task listener transaction to join");
        }
        return connection;
    }

    public void setTask(BPMTask task) {
    	this.task = task;
    }

    @Override
    public void begin() throws TransactionException {
        String correlationId = this.task.getCorrelationId().orElse(null);
        if (this.task != null && correlationId != null && !correlationId.isEmpty()) {
            this.connectionCache.put(correlationId, this);
            LOGGER.debug("{} of activity {} beginning for instance {}", this, task.getActivityId(), task.getProcessInstanceId());
        }
    }

    @Override
    public void commit() throws TransactionException {
        String correlationId = this.task.getCorrelationId().orElse(null);
        if (correlationId != null && !correlationId.isEmpty()) {
            this.connectionCache.remove(this);
        }
        LOGGER.debug("{} of activity {} committing for instance {}", this, task.getActivityId(), task.getProcessInstanceId());
    }

    @Override
    public void rollback() throws TransactionException {
        String correlationId = this.task.getCorrelationId().orElse(null);
        if (correlationId != null && !correlationId.isEmpty()) {
            this.connectionCache.remove(this);
        }
        LOGGER.debug("{} transaction of activity {} rolling back for instance {}", this, task.getActivityId(), task.getProcessInstanceId());
    }

}
