package com.alfame.esb.bpm.activity.queue.api;

import org.mule.runtime.api.metadata.TypedValue;

import java.io.Serializable;

public class BPMActivityResponse {

	private Throwable throwable;

	private TypedValue< Serializable > value;

	public BPMActivityResponse( TypedValue< Serializable > value ) {
		this.value = value;
	}

	public BPMActivityResponse( Throwable throwable ) {
		this.throwable = throwable;
	}

	public BPMActivityResponse( TypedValue< Serializable > value, Throwable throwable ) {
		this.value = value;
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public TypedValue< Serializable > getValue() {
		return value;
	}

}
