package com.alfame.esb.bpm.activity.queue.api;

import java.util.Optional;
import java.util.concurrent.*;

import static java.util.Optional.ofNullable;

public class BPMActivity implements BPMActivityResponseCallback {

	private CompletableFuture< BPMActivityResponse > completableFuture = new CompletableFuture<>();

	private final Object payload;
	private final Object attributes;
	private final String correlationId;

	public BPMActivity( Object value, Object attributes, String correlationId ) {
		this.payload = value;
		this.attributes = attributes;
		this.correlationId = correlationId;
	}

	public Object getPayload() {
		return payload;
	}

	public Object getAttributes() {
		return attributes;
	}

	public Optional< String > getCorrelationId() {
		return ofNullable( correlationId );
	}

	public BPMActivityResponse waitForResponse() throws InterruptedException, ExecutionException, TimeoutException {
		return completableFuture.get( 5L, TimeUnit.MINUTES );
	}

	public void submitResponse( BPMActivityResponse response ) {
		completableFuture.complete( response );
	}

}
