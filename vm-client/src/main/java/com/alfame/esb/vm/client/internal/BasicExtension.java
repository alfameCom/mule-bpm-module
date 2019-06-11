package com.alfame.esb.vm.client.internal;

import org.flowable.mule.VMApi;
import org.mule.extensions.vm.internal.VMConnectorQueueManager;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

import javax.inject.Inject;

/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "VMClient")
@Extension(name = "VMClient")
@Configurations(BasicConfiguration.class)
public class BasicExtension implements Startable, Stoppable {

	@Inject
	private VMConnectorQueueManager vmConnectorQueueManager;

	@Override
	public void start() throws MuleException {
		VMApi vmApi = VMApi.getInstance();
		vmApi.setVmConnectorQueueManager( vmConnectorQueueManager );
	}

	@Override
	public void stop() throws MuleException {

	}

}
