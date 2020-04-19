package com.alfame.esb.connectors.bpm.internal.connection;

import com.alfame.esb.bpm.activity.queue.api.BPMActivityResponseCallback;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.delegate.DelegateExecution;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.tx.TransactionException;
import org.mule.runtime.extension.api.connectivity.TransactionalConnection;

public class BPMConnection implements TransactionalConnection {

	private static final Logger LOGGER = getLogger( BPMConnection.class );

	private BPMActivityResponseCallback responseCallback;

	private Map< String, TypedValue< Serializable > > variablesToUpdate = new HashMap< String, TypedValue< Serializable > >();
	
	private List< String > variablesToRemove = new ArrayList< String >();

	private DelegateExecution execution;

	public BPMConnection() {}

	public BPMActivityResponseCallback getResponseCallback() {
		return responseCallback;
	}

	public void setResponseCallback( BPMActivityResponseCallback responseCallback ) {
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

	public DelegateExecution getExecution() {
		return execution;
	}

	public void setExecution( DelegateExecution execution ) {
		this.execution = execution;
	}
	
	@Override
	public void begin() throws TransactionException {
		LOGGER.debug( "BPMConnection transaction of activity {} beginning for instance {}", execution.getCurrentActivityId(), execution.getProcessInstanceId() );
	}

	@Override
	public void commit() throws TransactionException {
		LOGGER.debug( "BPMConnection transaction of activity {} committing for instance {}", execution.getCurrentActivityId(), execution.getProcessInstanceId() );
	}

	@Override
	public void rollback() throws TransactionException {
		LOGGER.debug( "BPMConnection transaction of activity {} rolling back for instance {}", execution.getCurrentActivityId(), execution.getProcessInstanceId() );
	}
	
}
