package com.alfame.esb.connectors.bpm.internal.processfactory;

import org.mule.runtime.extension.api.annotation.param.Parameter;

public final class BPMProcessFactorySettings {

	@Parameter
	private BPMProcessFactoryProperties properties;

	public BPMProcessFactoryProperties getProperties() {
		return properties;
	}

}
