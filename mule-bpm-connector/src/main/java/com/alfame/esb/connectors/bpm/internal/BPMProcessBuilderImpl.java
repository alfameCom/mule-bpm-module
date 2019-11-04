package com.alfame.esb.connectors.bpm.internal;

import static org.slf4j.LoggerFactory.getLogger;

import org.flowable.engine.runtime.ProcessInstance;
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
		ProcessInstance instance = null;

		LOGGER.debug( "Starting process instance with definition key: " + this.processDefinitionKey );

		if( this.tenantId != null ) {
			instance = this.engineDetails.getRuntimeService().startProcessInstanceByKeyAndTenantId( 
					processDefinitionKey, uniqueBusinessKey, null, tenantId );
		} else {
			instance = this.engineDetails.getRuntimeService().startProcessInstanceByKeyAndTenantId( 
					processDefinitionKey, uniqueBusinessKey, null, this.engineDetails.getDefaultTenantId() );
		}

		if( this.processInstanceName != null ) {
			this.engineDetails.getRuntimeService().setProcessInstanceName( 
					instance.getId(), this.processInstanceName );
		}

		return instance;
	}
	
}
