package com.alfame.esb.bpm.queue.api;

import com.alfame.esb.bpm.queue.api.BPMMessage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class BPMQueue {

	private String queueName;
	private Queue< BPMMessage > queueData = new ConcurrentLinkedQueue<>();

	private Semaphore semaphore;

	public BPMQueue( String queueName ) {
		semaphore = new Semaphore( 1, false );
		this.queueName = queueName;
	}

	public boolean publish( BPMMessage message ) throws InterruptedException {
		queueData.offer( message );
		semaphore.acquire();
		return true;
	}

	public BPMMessage pop() {
		return queueData.poll();
	}



	public String getQueueName() {
		return this.queueName;
	}

}
