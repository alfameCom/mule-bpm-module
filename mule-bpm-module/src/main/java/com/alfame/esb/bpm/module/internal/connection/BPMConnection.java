package com.alfame.esb.bpm.module.internal.connection;

import com.alfame.esb.bpm.api.BPMEngineEvent;
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

    private BPMTask task;
    private BPMEngineEvent event;
    private final Map<String, BPMConnection> connectionCache;

    public BPMConnection(Map<String, BPMConnection> connectionCache) {
        this.connectionCache = connectionCache;
    }

    public BPMTask getTask() {
        return task;
    }

    public void setTask(BPMTask task) {
        this.task = task;
    }

    public BPMEngineEvent getEvent() {
        return event;
    }

    public void setEvent(BPMEngineEvent event) {
        this.event = event;
    }

    public Optional<String> getCorrelationId() {
        if (this.task != null) {
            return this.task.getCorrelationId();
        }
        return Optional.empty();
    }

    public BPMConnection joinIfForked(CorrelationInfo correlationInfo) {
        BPMConnection connection = this;

        BPMConnection cachedConnection = this.connectionCache.get(correlationInfo.getCorrelationId());
        if (cachedConnection != null) {
            LOGGER.debug("{} joined cached connection {}", connection, cachedConnection);
            connection = cachedConnection;
        }

        return connection;
    }

    @Override
    public void begin() throws TransactionException {
        String correlationId = this.getCorrelationId().orElse(null);
        if (correlationId != null && !correlationId.isEmpty()) {
            this.connectionCache.put(correlationId, this);
            LOGGER.debug("cached connection {}", this);
        }
        LOGGER.debug("beginning connection {}", this);
    }

    @Override
    public void commit() throws TransactionException {
        String correlationId = this.getCorrelationId().orElse(null);
        if (correlationId != null && !correlationId.isEmpty()) {
            this.connectionCache.remove(correlationId);
            LOGGER.debug("uncached connection {}", this);
        }
        LOGGER.debug("committing connection {}", this);
    }

    @Override
    public void rollback() throws TransactionException {
        String correlationId = this.getCorrelationId().orElse(null);
        if (correlationId != null && !correlationId.isEmpty()) {
            this.connectionCache.remove(correlationId);
            LOGGER.debug("uncached connection {}", this);
        }
        LOGGER.debug("rolling back connection {}", this);
    }

}
