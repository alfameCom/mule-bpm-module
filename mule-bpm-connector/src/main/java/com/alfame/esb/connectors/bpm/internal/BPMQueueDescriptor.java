package com.alfame.esb.connectors.bpm.internal;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class BPMQueueDescriptor {

	@Parameter
	private String queueName;

	@Parameter
	@Optional( defaultValue = "5")
	private int timeout = 5;

	@Parameter
	@Optional( defaultValue = "SECONDS" )
	private TimeUnit timeoutUnit = SECONDS;

	public BPMQueueDescriptor() {}

	public BPMQueueDescriptor( String queueName ) {
		this.queueName = queueName;
	}

	public String getQueueName() {
		return queueName;
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
