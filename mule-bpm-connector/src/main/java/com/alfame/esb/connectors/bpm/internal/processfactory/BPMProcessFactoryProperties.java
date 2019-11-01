package com.alfame.esb.connectors.bpm.internal.processfactory;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class BPMProcessFactoryProperties {

	@Parameter
	private String processDefinitionKey;
	
	@Parameter
	@Optional
	private String uniqueBusinessKey;
	
	@Parameter
	@Optional
	private String tenantId;

	@Parameter
	@Optional
	private String processName;

	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public String getUniqueBusinessKey() {
		return uniqueBusinessKey;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getProcessName() {
		return processName;
	}

}
