package com.alfame.esb.connectors.bpm.internal.processfactory;

import org.mule.runtime.extension.api.annotation.param.Parameter;

public final class ProcessFactorySettings {

	@Parameter
	private ProcessFactoryProperties properties;

	public ProcessFactoryProperties getProperties() {
		return properties;
	}

}
