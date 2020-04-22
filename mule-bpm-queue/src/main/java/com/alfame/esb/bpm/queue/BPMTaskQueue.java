package com.alfame.esb.bpm.queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class BPMTaskQueue {

	private static final Log logger = LogFactory.getLog( BPMTaskQueue.class );

	private String queueName;
	private BlockingQueue< BPMTask > activityQueue = new LinkedTransferQueue<>();

	public BPMTaskQueue( String queueName ) {
		this.queueName = queueName;
	}

	public boolean publish( BPMTask message ) throws InterruptedException {
		logger.debug( "Pushing to queue " + this.queueName );
		return activityQueue.offer( message );
	}

	@Deprecated
	public BPMTask pop() throws InterruptedException {
		logger.debug( "Popping from queue " + this.queueName );
		return activityQueue.take();
	}
	
	public BPMTask pop( long timeout, TimeUnit unit ) throws InterruptedException {
		logger.debug( "Popping from queue " + this.queueName );
		return activityQueue.poll( timeout, unit );
	}

	public String getQueueName() {
		return this.queueName;
	}

}
