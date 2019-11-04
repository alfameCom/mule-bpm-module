package com.alfame.esb.bpm.api;

public abstract class BPMEngine {

	public abstract String getName();
	
	public abstract String getDefaultTenantId();
	
	public abstract BPMProcessBuilder processInstanceBuilder();
	
}
