package com.alfame.esb.connectors.bpm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.Execution;
import org.flowable.variable.api.persistence.entity.VariableInstance;

import com.alfame.esb.bpm.activity.queue.api.BPMActivityQueue;
import com.alfame.esb.bpm.activity.queue.api.BPMActivityQueueFactory;
import com.alfame.esb.bpm.activity.queue.api.BPMActivity;
import com.alfame.esb.bpm.activity.queue.api.BPMActivityResponse;

import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.test.runner.ArtifactClassLoaderRunnerConfig;
import org.junit.Test;
import org.slf4j.Logger;

@ArtifactClassLoaderRunnerConfig(
		applicationSharedRuntimeLibs = { "com.h2database:h2", "com.alfame.esb:bpm-queue" } )
public class BPMListenerTestCase extends MuleArtifactFunctionalTestCase {

	private static final Logger LOGGER = getLogger( BPMListenerTestCase.class );

	/**
	 * Specifies the mule config xml with the flows that are going to be executed in the tests, this file lives in the test resources.
	 */
	@Override
	protected String getConfigFile() {
		return "test-mule-config.xml";
	}

	@Test
	public void executeBpmListenerSuccessTestFlow() throws Exception {
		BPMActivityQueue queue = BPMActivityQueueFactory.getInstance( "bpm://some.success.test.queue" );
		BPMActivity activity = new BPMActivity( null, null, "123" );
		queue.publish( activity );
		BPMActivityResponse response = activity.waitForResponse();
		LOGGER.info( (String)response.getValue().getValue() );
	}

	@Test
	public void executeBpmListenerErrorTestFlow() throws Exception {
		BPMActivityQueue queue = BPMActivityQueueFactory.getInstance( "bpm://some.error.test.queue" );
		BPMActivity activity = new BPMActivity( null, null, "456" );
		queue.publish( activity );
		BPMActivityResponse response = activity.waitForResponse();
		LOGGER.info( response.getThrowable().getMessage() );
	}

	@Test
	public void executeBpmStartProcessTestFlow() throws Exception {
		flowRunner( "bpmStartProcessTestFlow" ).run();
		
		Thread.sleep( 1000 * 5 );
	}
	

}
