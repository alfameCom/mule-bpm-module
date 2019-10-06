package com.alfame.esb.connectors.bpm.api.config;

import static org.mule.runtime.api.meta.ExpressionSupport.SUPPORTED;

import java.io.InputStream;

import org.flowable.engine.repository.DeploymentBuilder;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias( "stream-definition" )
public class BPMStreamDefinition extends BPMDefinition {
	
	@Parameter
	@Optional
	@Expression( SUPPORTED )
	private String resourceName;

	@Parameter
	@Optional
	@Expression( SUPPORTED )
	private InputStream resourceStream;

	@Override
	public String getType() {
		return "stream";
	}

	@Override
	public String getResourceName() {
		return this.resourceName;
	}
	
	@Override
	public void addToDeploymentBuilder( DeploymentBuilder deploymentBuilder ) {
		deploymentBuilder.addInputStream( this.resourceName, this.resourceStream );
	}

}
