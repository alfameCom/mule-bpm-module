package com.alfame.esb.bpm.connector.internal.listener;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;

public class BPMTaskListenerEndpointDescriptor {

	@Parameter
	private String endpointUrl;

	@Parameter
	@Optional( defaultValue = "5" )
	private int timeout;

	@Parameter
	@Optional( defaultValue = "MINUTES" )
	private TimeUnit timeoutUnit = MINUTES;

	public BPMTaskListenerEndpointDescriptor() {}

	public BPMTaskListenerEndpointDescriptor( String endpointUrl ) {
		this.endpointUrl = endpointUrl;
	}

	public String getEndpointUrl() {
		return endpointUrl;
	}

	public int getTimeout() {
		return timeout;
	}

	public TimeUnit getTimeoutUnit() {
		return timeoutUnit;
	}

	public long getQueueTimeoutInMillis() {
		return timeoutUnit.toMillis( timeout );
	}

}
