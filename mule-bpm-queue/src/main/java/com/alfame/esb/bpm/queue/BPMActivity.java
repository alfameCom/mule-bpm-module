package com.alfame.esb.bpm.queue;

import java.util.Optional;
import java.util.concurrent.*;

import static java.util.Optional.ofNullable;

public class BPMActivity implements BPMActivityResponseCallback {

	private CompletableFuture< BPMActivityResponse > completableFuture = new CompletableFuture<>();

	private final Object payload;
	private final Object attributes;
	private final String correlationId;
	private long requestTimeoutMillis = 300000;

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
	
	public long requestTimeoutMillis() {
		return requestTimeoutMillis;
	}

	public BPMActivityResponse waitForResponse() throws InterruptedException, ExecutionException, TimeoutException {
		return completableFuture.get( requestTimeoutMillis, TimeUnit.MILLISECONDS );
	}

	public BPMActivityResponse waitForResponse( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException {
		this.requestTimeoutMillis = TimeUnit.MILLISECONDS.convert( timeout, unit );
		return completableFuture.get( this.requestTimeoutMillis, TimeUnit.MILLISECONDS );
	}

	public void submitResponse( BPMActivityResponse response ) {
		completableFuture.complete( response );
	}

}
