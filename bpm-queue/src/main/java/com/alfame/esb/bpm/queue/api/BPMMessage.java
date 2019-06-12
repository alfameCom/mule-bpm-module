package com.alfame.esb.bpm.queue.api;

import org.mule.runtime.api.metadata.TypedValue;

import java.io.Serializable;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class BPMMessage implements Serializable {

	private final TypedValue< Serializable > value;
	private final String correlationId;

	public BPMMessage( TypedValue< Serializable > value, String correlationId ) {
		this.value = value;
		this.correlationId = correlationId;
	}

	public TypedValue< Serializable > getValue() {
		return value;
	}

	public Optional< String > getCorrelationId() {
		return ofNullable( correlationId );
	}

}
