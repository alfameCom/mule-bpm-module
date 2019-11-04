package com.alfame.esb.bpm.api;

public abstract class BPMProcessBuilder {
	
	protected String processDefinitionKey;
	protected String tenantId;
	protected String uniqueBusinessKey;
	protected String processInstanceName;
	
	public BPMProcessBuilder tenantId( String tenantId ) {
		this.tenantId = tenantId;
		return this;
	}

	public BPMProcessBuilder processDefinitionKey( String processDefinitionKey ) {
		this.processDefinitionKey = processDefinitionKey;
		return this;
	}

	public BPMProcessBuilder uniqueBusinessKey( String uniqueBusinessKey ) {
		this.uniqueBusinessKey = uniqueBusinessKey;
		return this;
	}

	public BPMProcessBuilder processInstanceName( String processInstanceName ) {
		this.processInstanceName = processInstanceName;
		return this;
	}
	
	public abstract Object startProcessInstance();

}
