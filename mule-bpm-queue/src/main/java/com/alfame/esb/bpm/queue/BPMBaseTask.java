package com.alfame.esb.bpm.queue;

import java.util.Optional;
import java.util.concurrent.*;

import com.alfame.esb.bpm.api.BPMTaskInstance;

public abstract class BPMBaseTask implements BPMTaskInstance, BPMTaskResponseCallback {

	private CompletableFuture< BPMTaskResponse > completableFuture = new CompletableFuture<>();

	private long requestTimeoutMillis = 300000;

	abstract public Object getPayload();

	abstract public Optional< String > getCorrelationId();

	public long getRequestTimeoutMillis() {
		return this.requestTimeoutMillis;
	}

	public void setRequestTimeoutMillis( long requestTimeoutMillis ) {
		this.requestTimeoutMillis = requestTimeoutMillis;
	}

	public BPMTaskResponse waitForResponse() throws InterruptedException, ExecutionException, TimeoutException {
		return completableFuture.get( this.requestTimeoutMillis, TimeUnit.MILLISECONDS );
	}

	public BPMTaskResponse waitForResponse( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException {
		this.requestTimeoutMillis = TimeUnit.MILLISECONDS.convert( timeout, unit );
		return completableFuture.get( this.requestTimeoutMillis, TimeUnit.MILLISECONDS );
	}

	public void submitResponse( BPMTaskResponse response ) {
		completableFuture.complete( response );
	}

}
