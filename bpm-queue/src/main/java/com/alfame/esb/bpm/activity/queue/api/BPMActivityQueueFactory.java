package com.alfame.esb.bpm.activity.queue.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BPMActivityQueueFactory {

	private static final Log logger = LogFactory.getLog( BPMActivityQueueFactory.class );

	private static volatile Map< String, BPMActivityQueue > instances = new ConcurrentHashMap<>();

	public static BPMActivityQueue getInstance( String queueName ) {
		if( instances.get( queueName ) == null )
			instances.put( queueName, new BPMActivityQueue( queueName ) );

		return instances.get( queueName );
	}

	private BPMActivityQueueFactory() {}

}
