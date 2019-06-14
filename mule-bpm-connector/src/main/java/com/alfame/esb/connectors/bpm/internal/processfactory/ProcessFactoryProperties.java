package com.alfame.esb.connectors.bpm.internal.processfactory;

import com.alfame.esb.connectors.bpm.api.processfactory.model.PropertyValue;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class ProcessFactoryProperties {

	@Parameter
	private PropertyValue tenantId;

}
