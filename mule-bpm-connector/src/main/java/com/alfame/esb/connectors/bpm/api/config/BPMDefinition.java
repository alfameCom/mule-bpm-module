package com.alfame.esb.connectors.bpm.api.config;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias( "definition" )
public class BPMDefinition {
	
	@Parameter
	@Expression( NOT_SUPPORTED )
	private String classPath;

	public String getClassPath() {
		return classPath;
	}

}
