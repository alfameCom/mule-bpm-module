package com.alfame.esb.bpm.api;

public abstract class BPMEngine {
	
	public abstract String getName();
	
	public abstract String getDefaultTenantId();
	
	public abstract BPMProcessBuilder processInstanceBuilder();

	public abstract Object getVariableInstance( String executionId, String variableName );

	public abstract void setVariable( String executionId, String variableName, Object content );
	
}
