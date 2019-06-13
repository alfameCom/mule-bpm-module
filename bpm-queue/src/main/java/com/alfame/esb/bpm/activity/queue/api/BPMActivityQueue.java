package com.alfame.esb.bpm.activity.queue.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BPMActivityQueue {

	private static final Log logger = LogFactory.getLog( BPMActivityQueue.class );

	private String queueName;
	private Queue< BPMActivity > queueData = new ConcurrentLinkedQueue<>();

	public BPMActivityQueue( String queueName ) {
		this.queueName = queueName;
	}

	public boolean publish( BPMActivity message ) throws InterruptedException {
		return queueData.offer( message );
	}

	public BPMActivity pop() {
		return queueData.poll();
	}

	public String getQueueName() {
		return this.queueName;
	}

}
