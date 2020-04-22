package com.alfame.esb.bpm.connector.internal;

import static org.slf4j.LoggerFactory.getLogger;

import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.slf4j.Logger;

import com.alfame.esb.bpm.api.BPMProcessBuilder;

public class BPMProcessBuilderImpl extends BPMProcessBuilder {

	private static final Logger LOGGER = getLogger( BPMProcessBuilderImpl.class );
	
	private BPMEngineDetails engineDetails;
	
	public BPMProcessBuilderImpl( BPMEngineDetails engineDetails ) {
		this.engineDetails = engineDetails;
	}

	@Override
	public Object startProcessInstance() {
		ProcessInstanceBuilder instanceBuilder = this.engineDetails.getRuntimeService().createProcessInstanceBuilder();

		LOGGER.debug( "Starting process instance with definition key: " + this.processDefinitionKey );
		instanceBuilder = instanceBuilder.processDefinitionKey( this.processDefinitionKey );
		
		if( this.tenantId != null ) {
			instanceBuilder = instanceBuilder.tenantId( this.tenantId );
		} else {
			instanceBuilder = instanceBuilder.tenantId( this.engineDetails.getDefaultTenantId() );
		}
		
		if( this.uniqueBusinessKey != null ) {
			instanceBuilder = instanceBuilder.businessKey( this.tenantId );
		}

		if( this.processInstanceName != null ) {
			instanceBuilder = instanceBuilder.name( this.processInstanceName );
		}

		if( this.variables != null ) {
			instanceBuilder = instanceBuilder.variables( this.variables );
		}
		
		return instanceBuilder.start();
	}
	
}
