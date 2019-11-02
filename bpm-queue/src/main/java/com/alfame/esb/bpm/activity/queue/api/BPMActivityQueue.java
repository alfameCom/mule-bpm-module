package com.alfame.esb.bpm.activity.queue.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class BPMActivityQueue {

	private static final Log logger = LogFactory.getLog( BPMActivityQueue.class );

	private String queueName;
	private BlockingQueue< BPMActivity > activityQueue = new LinkedTransferQueue<>();

	public BPMActivityQueue( String queueName ) {
		this.queueName = queueName;
	}

	public boolean publish( BPMActivity message ) throws InterruptedException {
		logger.debug( "Pushing to queue " + this.queueName );
		return activityQueue.offer( message );
	}

	@Deprecated
	public BPMActivity pop() throws InterruptedException {
		logger.debug( "Popping from queue " + this.queueName );
		return activityQueue.take();
	}
	
	public BPMActivity pop( long timeout, TimeUnit unit ) throws InterruptedException {
		logger.debug( "Popping from queue " + this.queueName );
		return activityQueue.poll( timeout, unit );
	}

	public String getQueueName() {
		return this.queueName;
	}

}
