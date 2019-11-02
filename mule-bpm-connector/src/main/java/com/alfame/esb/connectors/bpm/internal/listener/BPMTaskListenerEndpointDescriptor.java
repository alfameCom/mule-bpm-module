package com.alfame.esb.connectors.bpm.internal.listener;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class BPMTaskListenerEndpointDescriptor {

	@Parameter
	private String endpoint;

	@Parameter
	@Optional( defaultValue = "5" )
	private int timeout;

	@Parameter
	@Optional( defaultValue = "SECONDS" )
	private TimeUnit timeoutUnit = SECONDS;

	public BPMTaskListenerEndpointDescriptor() {}

	public BPMTaskListenerEndpointDescriptor( String endpoint ) {
		this.endpoint = endpoint;
	}

	public String getEndpoint() {
		return endpoint;
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
