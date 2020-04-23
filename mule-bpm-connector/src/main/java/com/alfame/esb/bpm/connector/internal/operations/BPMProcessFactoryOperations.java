package com.alfame.esb.bpm.connector.internal.operations;

import org.flowable.engine.runtime.ProcessInstance;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.slf4j.Logger;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.connector.internal.BPMExtension;
import com.alfame.esb.bpm.connector.internal.BPMProcessBuilderImpl;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessFactoryOperations {

	private static final Logger LOGGER = getLogger( BPMProcessFactoryOperations.class );

	@Alias( "process-factory" )
	@MediaType( value = MediaType.ANY, strict = false )
	@OutputResolver( output = BPMProcessFactoryOutputMetadataResolver.class )
	public BPMProcessInstance processFactory(
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
		
		BPMProcessFactoryProcessInstanceProxy instanceProxy = new BPMProcessFactoryProcessInstanceProxy( instance );
		
		LOGGER.debug( "Started process instance " + instanceProxy.getProcessInstanceId() );
		
		return instanceProxy;

	}

}
