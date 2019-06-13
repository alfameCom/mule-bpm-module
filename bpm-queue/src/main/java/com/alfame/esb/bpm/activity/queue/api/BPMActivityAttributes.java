package com.alfame.esb.bpm.activity.queue.api;

import java.io.Serializable;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

public class BPMActivityAttributes implements Serializable {

	private final String queueName;
	private final String correlationId;
	private final LocalDateTime timestamp = now();

	/**
	 * Creates a new instance
	 *
	 * @param queueName			the name of the queue from which the message was taken
	 * @param correlationId		the message correlation id
	 */
	public BPMActivityAttributes( String queueName, String correlationId ) {
		this.queueName = queueName;
		this.correlationId = correlationId;
	}

	public String getQueueName() {
		return queueName;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

}
