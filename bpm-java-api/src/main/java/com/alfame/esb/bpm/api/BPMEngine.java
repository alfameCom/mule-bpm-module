package com.alfame.esb.bpm.api;

public abstract class BPMEngine {

	public abstract String getName();
	
	public abstract String getDefaultTenantId();
	
	public abstract Object startProcessInstance( String processDefinitionKey, String tenantId, String uniqueBusinessKey, String processName );

}
