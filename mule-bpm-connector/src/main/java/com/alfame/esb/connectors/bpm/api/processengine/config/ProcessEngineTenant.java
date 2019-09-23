package com.alfame.esb.connectors.bpm.api.processengine.config;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias("tenant")
public class ProcessEngineTenant {
	
	@Parameter
	@Expression(NOT_SUPPORTED)
	private String tenantId;

	public String tenantId() {
		return tenantId;
	}

}
