package com.alfame.esb.connectors.bpm.internal;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class BPMQueueDescriptor {

	@Parameter
	private String endpoint;

	@Parameter
	@Optional( defaultValue = "3")
	private int timeout;

	@Parameter
	@Optional( defaultValue = "SECONDS" )
	private TimeUnit timeoutUnit = SECONDS;

	public BPMQueueDescriptor() {}

	public BPMQueueDescriptor( String endpoint ) {
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
