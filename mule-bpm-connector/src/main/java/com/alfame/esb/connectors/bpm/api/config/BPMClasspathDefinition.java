package com.alfame.esb.connectors.bpm.api.config;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import java.io.File;

import org.flowable.engine.repository.DeploymentBuilder;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias( "classpath-definition" )
public class BPMClasspathDefinition extends BPMDefinition {
	
	@Parameter
	@Optional
	@Expression( NOT_SUPPORTED )
	private String resourceClasspath;

	@Override
	public String getType() {
		return "classpath";
	}

	@Override
	public String getResourceName() {
		return new File( this.resourceClasspath ).getName();
	}
	
	@Override
	public void addToDeploymentBuilder( DeploymentBuilder deploymentBuilder ) {
		deploymentBuilder.addClasspathResource( this.resourceClasspath );
	}

}
