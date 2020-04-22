package com.alfame.esb.bpm.connector.internal.operations;

import org.mule.runtime.extension.api.annotation.param.Parameter;

public final class BPMProcessFactorySettings {

	@Parameter
	private BPMProcessFactoryProperties properties;

	public BPMProcessFactoryProperties getProperties() {
		return properties;
	}

}
