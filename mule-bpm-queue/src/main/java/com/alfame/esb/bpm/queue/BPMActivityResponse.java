package com.alfame.esb.bpm.queue;

import org.mule.runtime.api.metadata.TypedValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPMActivityResponse {

	private Throwable throwable;

	private TypedValue< Serializable > value;
	
	private Map< String, TypedValue< Serializable > > variablesToUpdate = new HashMap< String, TypedValue< Serializable > >();
	
	private List< String > variablesToRemove = new ArrayList< String >();

	public BPMActivityResponse( TypedValue< Serializable > value ) {
		this.value = value;
	}

	public BPMActivityResponse( Throwable throwable ) {
		this.throwable = throwable;
	}

	public BPMActivityResponse( TypedValue< Serializable > value, Throwable throwable ) {
		this.value = value;
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public TypedValue< Serializable > getValue() {
		return value;
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

	public void setVariablesToRemove( List< String > variablesToRemove ) {
		this.variablesToRemove = variablesToRemove;
	}

}
