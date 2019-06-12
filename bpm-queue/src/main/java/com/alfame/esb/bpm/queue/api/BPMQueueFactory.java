package com.alfame.esb.bpm.queue.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BPMQueueFactory {

	private static Map< String, BPMQueue > instance = new ConcurrentHashMap<>();

	public static BPMQueue getInstance( String queueName ) {
		if( instance.get( queueName ) == null )
			instance.put( queueName, new BPMQueue( queueName ) );

		return instance.get( queueName );
	}

	private BPMQueueFactory() {}

}
