package com.alfame.esb.bpm.activity.queue.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class BPMActivityQueue {

	private static final Log logger = LogFactory.getLog( BPMActivityQueue.class );

	private String queueName;
	private BlockingQueue< BPMActivity > activityQueue = new LinkedTransferQueue<>();

	public BPMActivityQueue( String queueName ) {
		this.queueName = queueName;
	}

	public boolean publish( BPMActivity message ) throws InterruptedException {
		return activityQueue.offer( message );
	}

	public BPMActivity pop() throws InterruptedException {
		return activityQueue.take();
	}

	public String getQueueName() {
		return this.queueName;
	}

}
