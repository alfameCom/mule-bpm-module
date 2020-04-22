package com.alfame.esb.bpm.connector.internal.connection;

import com.alfame.esb.bpm.queue.BPMBaseTask;
import com.alfame.esb.bpm.queue.BPMTaskResponseCallback;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.tx.TransactionException;
import org.mule.runtime.extension.api.connectivity.TransactionalConnection;

public class BPMConnection implements TransactionalConnection {

	private static final Logger LOGGER = getLogger( BPMConnection.class );

	private BPMTaskResponseCallback responseCallback;

	private Map< String, TypedValue< Serializable > > variablesToUpdate = new HashMap< String, TypedValue< Serializable > >();
	
	private List< String > variablesToRemove = new ArrayList< String >();

	private BPMBaseTask task;

	public BPMConnection() {}

	public BPMTaskResponseCallback getResponseCallback() {
		return responseCallback;
	}

	public void setResponseCallback( BPMTaskResponseCallback responseCallback ) {
		this.responseCallback = responseCallback;
	}

	public Map< String, TypedValue< Serializable > > getVariablesToUpdate() {
		return variablesToUpdate;
	}

	public void setVariablesToUpdate( Map< String, TypedValue< Serializable > > variablesToUpdate ) {
		this.variablesToUpdate = variablesToUpdate;
	}

	public List< String > getVariablesToRemove() {
		return variablesToRemove;
	}

	public void setVariablesToDelete( List< String > variablesToRemove ) {
		this.variablesToRemove = variablesToRemove;
	}

	public BPMBaseTask getTask() {
		return task;
	}

	public void setTask( BPMBaseTask task ) {
		this.task = task;
	}
	
	@Override
	public void begin() throws TransactionException {
		LOGGER.debug( "BPMConnection transaction of activity {} beginning for instance {}", task.getActivityId(), task.getProcessInstanceId() );
	}

	@Override
	public void commit() throws TransactionException {
		LOGGER.debug( "BPMConnection transaction of activity {} committing for instance {}", task.getActivityId(), task.getProcessInstanceId() );
	}

	@Override
	public void rollback() throws TransactionException {
		LOGGER.debug( "BPMConnection transaction of activity {} rolling back for instance {}", task.getActivityId(), task.getProcessInstanceId() );
	}
	
}
