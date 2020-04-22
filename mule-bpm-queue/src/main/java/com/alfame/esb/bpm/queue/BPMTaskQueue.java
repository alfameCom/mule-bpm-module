package com.alfame.esb.bpm.queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class BPMTaskQueue {

	private static final Log logger = LogFactory.getLog( BPMTaskQueue.class );

	private String queueName;
	private BlockingQueue< BPMBaseTask > taskQueue = new LinkedTransferQueue<>();

	public BPMTaskQueue( String queueName ) {
		this.queueName = queueName;
	}

	public boolean publish( BPMBaseTask task ) throws InterruptedException {
		logger.debug( "Pushing to queue " + this.queueName );
		return taskQueue.offer( task );
	}

	@Deprecated
	public BPMBaseTask pop() throws InterruptedException {
		logger.debug( "Popping from queue " + this.queueName );
		return taskQueue.take();
	}
	
	public BPMBaseTask pop( long timeout, TimeUnit unit ) throws InterruptedException {
		logger.debug( "Popping from queue " + this.queueName );
		return taskQueue.poll( timeout, unit );
	}

	public String getQueueName() {
		return this.queueName;
	}

}
