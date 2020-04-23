package com.alfame.esb.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.log4j.Logger;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.mule.munit.assertion.api.MunitAssertion;
import org.mule.munit.assertion.api.TypedValue;

public class FlowableAssertion implements MunitAssertion, FlowableEventListener {

	//private static final Logger logger = Logger.getLogger( FlowableAssertion.class );
	private static final Log logger = LogFactory.getLog( FlowableAssertion.class );
	
	private CountDownLatch latch = new CountDownLatch( 1 );
	
	public void execute( TypedValue expression, Object params ) throws AssertionError {
		
		try {
			latch.await( 5L, TimeUnit.MINUTES );
		} catch (InterruptedException e) {
			throw new AssertionError( "Stopped waiting for flowable", e );
		}
		
	}

	public void onEvent(FlowableEvent event) {
		logger.info( event.getType() );
		if( event.getType() == FlowableEngineEventType.TASK_CREATED ) {
			logger.info( "User Task Created" );
			latch.countDown();
		}
		
	}

	public boolean isFailOnException() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFireOnTransactionLifecycleEvent() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getOnTransaction() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
