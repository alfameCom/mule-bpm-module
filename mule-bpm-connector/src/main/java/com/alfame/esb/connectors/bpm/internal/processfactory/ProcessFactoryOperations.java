package com.alfame.esb.connectors.bpm.internal.processfactory;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ProcessFactoryOperations {

	private static final Logger LOGGER = getLogger( ProcessFactoryOperations.class );

	@Autowired
	private RuntimeService runtimeService;

	@MediaType( value = MediaType.ANY, strict = false )
	@OutputResolver( output = ProcessFactoryMetadataResolver.class )
	public ProcessInstance processfactory( @ParameterGroup( name = "Properties", showInDsl = true ) ProcessFactoryProperties properties ) {

		ProcessInstance instance = null;

		/*if( properties.isPayloadAsAttachment() ) {

		} else {
			instance = startProcessInstance( this.runtimeService, properties );
		}*/

		return instance;

	}

	private static ProcessInstance startProcessInstance( RuntimeService runtimeService, ProcessFactoryProperties properties ) {

		/*ProcessInstance instance = null;

		if( properties.getTenantId() != null ) {

			instance = runtimeService.startProcessInstanceByKeyAndTenantId( properties.getProcessDefinitionKey(), properties.getUniqueBusinessKey(), null, properties.getTenantId() );

		} else {

			instance = runtimeService.startProcessInstanceByKey( properties.getProcessDefinitionKey(), properties.getUniqueBusinessKey(), null );

		}

		if( properties.getProcessName() != null ) {
			runtimeService.setProcessInstanceName( instance.getId(), properties.getProcessName() );
		}

		return instance;*/

		return null;

	}

}
