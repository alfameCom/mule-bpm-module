package com.alfame.esb.bpm.api;

import java.util.Collection;
import java.util.Map;

public abstract class BPMEngine {
	
	public static final String BPM_VARIABLE_PREFIX = "mule_invocation_";

	public abstract String getName();
	
	public abstract String getDefaultTenantId();
	
	public abstract BPMProcessBuilder processInstanceBuilder();
	
	public abstract Map<String,Object> getVariables( String executionId, Collection< String > variableNames );

	public abstract void setVariables( String executionId, Map< String, Object > variables );
	
	protected Map< String, Object > toBpmVariables( Map< String, Object > variables ) {
		for( String key : variables.keySet() ) {
			variables.put( BPM_VARIABLE_PREFIX.concat( key ), variables.remove( key ) );
		}
		return variables;
	}
	
	protected Map< String, Object > fromBpmVariables( Map< String, Object > variables ) {
		for( String key : variables.keySet() ) {
			variables.put( key.replaceFirst( "BPM_VARIABLE_PREFIX", "" ), variables.remove( key ) );
		}
		return variables;
	}
}
