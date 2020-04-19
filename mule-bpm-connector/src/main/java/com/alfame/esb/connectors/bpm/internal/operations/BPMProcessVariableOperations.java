package com.alfame.esb.connectors.bpm.internal.operations;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.operation.Result.Builder;
import org.slf4j.Logger;

import com.alfame.esb.connectors.bpm.internal.BPMExtension;
import com.alfame.esb.connectors.bpm.internal.connection.BPMConnection;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.Serializable;

public class BPMProcessVariableOperations {

	private static final Logger LOGGER = getLogger( BPMProcessVariableOperations.class );

	@Alias( "get-variable" )
	@MediaType( value = ANY, strict = false )
	@OutputResolver(output = BPMProcessVariableMetadataResolver.class)
	public Result< Object, VariableInstance > getVariable(
			@Config BPMExtension config,
			@Connection BPMConnection connection,
			@DisplayName( "Variable name" ) String variableName
		) {
		Builder< Object, VariableInstance > resultBuilder = Result.builder();
		
		VariableInstance variableInstance = (VariableInstance) config.getVariableInstance( 
				connection.getExecution().getProcessInstanceId(), variableName );
		
		if ( variableInstance != null ) {
			LOGGER.debug( "Variable " + variableName + " found for process " + connection.getExecution().getProcessInstanceId() );
			
			resultBuilder.output( variableInstance.getValue() );
			resultBuilder.attributes( variableInstance );
		} else {
			LOGGER.debug( "Variable " + variableName + " not found for process " + connection.getExecution().getProcessInstanceId() );
		}
		
		
		return resultBuilder.build();		
	}
	
	@Alias( "set-variable" )
	public void setVariable(
			@Config BPMExtension config,
			@Connection BPMConnection connection,
			@DisplayName( "Variable name" ) String variableName,
			@Content @Summary( "Content for variable" ) TypedValue< Serializable > content
		) throws IOException {
		
		connection.getVariablesToUpdate().put( variableName, content );
		
		LOGGER.debug( "Variable " + variableName + " set to be updated for process " + connection.getExecution().getProcessInstanceId() );
	}

	@Alias( "remove-variable" )
	@MediaType( value = ANY, strict = false )
	public void removeVariable(
			@Config BPMExtension config,
			@Connection BPMConnection connection,
			@DisplayName( "Variable name" ) String variableName
		) {
		
		connection.getVariablesToRemove().add( variableName );
		
		LOGGER.debug( "Variable " + variableName + " set to be removed for process " + connection.getExecution().getProcessInstanceId() );
	}
	
}
