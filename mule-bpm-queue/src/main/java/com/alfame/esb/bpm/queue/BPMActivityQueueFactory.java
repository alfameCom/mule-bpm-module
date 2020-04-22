package com.alfame.esb.bpm.queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class BPMActivityQueueFactory {

	private static final Log logger = LogFactory.getLog( BPMActivityQueueFactory.class );

	private static Map< String, BPMActivityQueue > instances = new ConcurrentHashMap<>();

	private static Semaphore semaphore = new Semaphore( 1 );

	public static BPMActivityQueue getInstance( String queueName ) {
		BPMActivityQueue activityQueue = null;
		
		try {
			semaphore.acquire();
			
			if( instances.get( queueName ) == null ) {
				instances.put( queueName, new BPMActivityQueue( queueName ) );
			}
			
			activityQueue = instances.get( queueName );
			
			semaphore.release();
		} catch( InterruptedException e ) {
			logger.info( e );
		}

		logger.trace( "Returning instance for queueName " + queueName );
		return activityQueue;
	}

	private BPMActivityQueueFactory() {}

}
