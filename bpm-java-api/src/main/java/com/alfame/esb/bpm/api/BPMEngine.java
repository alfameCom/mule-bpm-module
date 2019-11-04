package com.alfame.esb.bpm.api;

import java.util.Collection;
import java.util.Map;

public abstract class BPMEngine {

	public abstract String getName();
	
	public abstract String getDefaultTenantId();
	
	public abstract BPMProcessBuilder processInstanceBuilder();
	
	public abstract Map<String,Object> getVariables( String executionId, Collection< String > variableNames );

	public abstract void setVariables( String executionId, Map<String,Object> variables );
}
