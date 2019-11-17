package com.alfame.esb.connectors.bpm.internal.operations;

import org.flowable.engine.runtime.ProcessInstance;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.slf4j.Logger;

import com.alfame.esb.connectors.bpm.internal.BPMExtension;
import com.alfame.esb.connectors.bpm.internal.BPMProcessBuilderImpl;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessFactoryOperations {

	private static final Logger LOGGER = getLogger( BPMProcessFactoryOperations.class );

	@Alias( "process-factory" )
	@MediaType( value = MediaType.ANY, strict = false )
	@OutputResolver( output = BPMProcessFactoryMetadataResolver.class )
	public ProcessInstance processfactory(
			@ParameterGroup( name = "properties" ) BPMProcessFactoryProperties properties,
			@Config BPMExtension engine
			) {

		ProcessInstance instance = null;
		
		BPMProcessBuilderImpl instanceBuilder = new BPMProcessBuilderImpl( engine );
		
		instance = (ProcessInstance) instanceBuilder
				.processDefinitionKey( properties.getProcessDefinitionKey() )
				.tenantId( properties.getTenantId() )
				.uniqueBusinessKey( properties.getUniqueBusinessKey() )
				.processInstanceName( properties.getProcessName() )
				.startProcessInstance();
		
		return instance;

	}

}