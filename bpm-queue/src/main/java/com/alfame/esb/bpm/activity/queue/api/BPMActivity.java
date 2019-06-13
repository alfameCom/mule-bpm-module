package com.alfame.esb.bpm.activity.queue.api;

import java.util.Optional;
import java.util.concurrent.*;

import static java.util.Optional.ofNullable;

public class BPMActivity implements BPMActivityResponseCallback {

	private CompletableFuture< BPMActivityResponse > completableFuture = new CompletableFuture<>();

	private final Object value;
	private final String correlationId;

	public BPMActivity( Object value, String correlationId ) {
		this.value = value;
		this.correlationId = correlationId;
	}

	public Object getValue() {
		return value;
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
