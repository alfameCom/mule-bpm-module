package com.alfame.esb.connectors.bpm.internal.processfactory;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.slf4j.Logger;
import com.alfame.esb.connectors.bpm.internal.BPMExtension;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessFactoryOperations {

	private static final Logger LOGGER = getLogger( BPMProcessFactoryOperations.class );

	@Alias( "process-factory" )
	@MediaType( value = MediaType.ANY, strict = false )
	@OutputResolver( output = BPMProcessFactoryMetadataResolver.class )
	public ProcessInstance processfactory(
			@ParameterGroup( name = "properties" ) BPMProcessFactoryProperties properties,
			@Config BPMExtension config
			) {

		ProcessInstance instance = null;
		
		LOGGER.debug("Starting process instance with definition key: " + properties.getProcessDefinitionKey());

		instance = startProcessInstance( config.getRuntimeService(), properties );

		return instance;

	}

	private static ProcessInstance startProcessInstance( RuntimeService runtimeService, BPMProcessFactoryProperties properties ) {

		ProcessInstance instance = null;

		if( properties.getTenantId() != null ) {

			instance = runtimeService.startProcessInstanceByKeyAndTenantId( properties.getProcessDefinitionKey(), properties.getUniqueBusinessKey(), null, properties.getTenantId() );

		} else {

			instance = runtimeService.startProcessInstanceByKey( properties.getProcessDefinitionKey(), properties.getUniqueBusinessKey(), null );

		}

		if( properties.getProcessName() != null ) {
			runtimeService.setProcessInstanceName( instance.getId(), properties.getProcessName() );
		}

		return instance;

	}

}
