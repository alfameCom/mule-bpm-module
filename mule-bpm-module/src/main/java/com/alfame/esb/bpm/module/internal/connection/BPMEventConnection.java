package com.alfame.esb.bpm.module.internal.connection;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import org.mule.runtime.api.tx.TransactionException;
import org.mule.runtime.extension.api.connectivity.TransactionalConnection;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMEventConnection implements TransactionalConnection {

    private static final Logger LOGGER = getLogger(BPMEventConnection.class);

    private BPMEngineEvent event;

    public BPMEventConnection() {
    }

    public BPMEngineEvent getEvent() {
    	return event;
    }

    public void setEvent(BPMEngineEvent event) {
    	this.event = event;
    }

    @Override
    public void begin() throws TransactionException {
        LOGGER.debug("{} of event {} received for instance {}", this, event.getEventType(), event.getProcessInstanceId());
    }

    @Override
    public void commit() throws TransactionException {
        LOGGER.debug("{} of event {} handled for instance {}", this, event.getEventType(), event.getProcessInstanceId());
    }

    @Override
    public void rollback() throws TransactionException {
        LOGGER.debug("{} of event {} rolled for instance {}", this, event.getEventType(), event.getProcessInstanceId());
    }

}
