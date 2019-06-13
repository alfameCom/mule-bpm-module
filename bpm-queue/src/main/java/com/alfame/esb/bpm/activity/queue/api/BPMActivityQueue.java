package com.alfame.esb.bpm.activity.queue.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class BPMActivityQueue {

	private static final Log logger = LogFactory.getLog( BPMActivityQueue.class );

	private String queueName;
	private Queue< BPMActivity > queueData;

	private final Semaphore semaphore = new Semaphore( 1, true );

	public BPMActivityQueue( String queueName ) {
		try {
			semaphore.acquire();
			queueData = new ConcurrentLinkedQueue<>();
		} catch( InterruptedException e ) {

		}
		semaphore.release();
		this.queueName = queueName;
	}

	public boolean publish( BPMActivity message ) throws InterruptedException {
		logger.info( "PUBLISH" );
		boolean asd = queueData.offer( message );
		logger.info( "Publish: " + Integer.toHexString( System.identityHashCode( queueData ) ) );
		logger.info( asd );
		return asd;
	}

	public BPMActivity pop() {
		logger.info( "Pop: " + Integer.toHexString( System.identityHashCode( queueData ) ) );
		//logger.info( queueData.toArray().length );
		if( queueData.peek() != null ) {
			logger.info( "POP" );
		}
		return queueData.poll();
	}

	public String getQueueName() {
		return this.queueName;
	}

}
