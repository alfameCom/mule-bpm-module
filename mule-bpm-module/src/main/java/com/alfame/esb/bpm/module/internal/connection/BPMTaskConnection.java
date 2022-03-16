package com.alfame.esb.bpm.module.internal.connection;

import com.alfame.esb.bpm.taskqueue.BPMTask;
import com.alfame.esb.bpm.taskqueue.BPMTaskResponseCallback;
import org.mule.runtime.api.tx.TransactionException;
import org.mule.runtime.extension.api.connectivity.TransactionalConnection;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.slf4j.Logger;

import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMTaskConnection implements TransactionalConnection {

    private static final Logger LOGGER = getLogger(BPMTaskConnection.class);

    private BPMTaskResponseCallback responseCallback;
    private Map<String, Object> variablesToUpdate = new HashMap<>();
    private List<String> variablesToRemove = new ArrayList<>();

    private BPMTask task;

    private final Map<String, BPMTaskConnection> connectionCache;

    public BPMTaskConnection(Map<String, BPMTaskConnection> connectionCache) {
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

    public BPMTaskConnection joinIfForked(CorrelationInfo correlationInfo) {
        BPMTaskConnection connection = this;
        if (connection.getTask() == null) {
            BPMTaskConnection cachedConnection = this.connectionCache.get(correlationInfo.getCorrelationId());
            if (cachedConnection != null && cachedConnection.getTask() != null) {
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
        if (this.task != null) {
            String correlationId = this.task.getCorrelationId().orElse(null);
            if (correlationId != null && !correlationId.isEmpty()) {
                this.connectionCache.put(correlationId, this);
                LOGGER.debug("{} of activity {} cached for instance {}", this, task.getActivityId(), task.getProcessInstanceId());
            }
            LOGGER.debug("{} of activity {} beginning for instance {}", this, task.getActivityId(), task.getProcessInstanceId());
        }
    }

    @Override
    public void commit() throws TransactionException {
        if (this.task != null) {
            String correlationId = this.task.getCorrelationId().orElse(null);
            if (correlationId != null && !correlationId.isEmpty()) {
                this.connectionCache.remove(correlationId);
                LOGGER.debug("{} of activity {} uncached for instance {}", this, task.getActivityId(), task.getProcessInstanceId());
            }
            LOGGER.debug("{} of activity {} committing for instance {}", this, task.getActivityId(), task.getProcessInstanceId());
        }
    }

    @Override
    public void rollback() throws TransactionException {
        if (this.task != null) {
            String correlationId = this.task.getCorrelationId().orElse(null);
            if (correlationId != null && !correlationId.isEmpty()) {
                this.connectionCache.remove(correlationId);
                LOGGER.debug("{} of activity {} uncached for instance {}", this, task.getActivityId(), task.getProcessInstanceId());
            }
            LOGGER.debug("{} of activity {} rolling back for instance {}", this, task.getActivityId(), task.getProcessInstanceId());
        }
    }

}
