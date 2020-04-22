package com.alfame.esb.bpm.connector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.slf4j.LoggerFactory.getLogger;

import com.alfame.esb.bpm.queue.BPMTaskQueue;
import com.alfame.esb.bpm.queue.BPMTaskQueueFactory;
import com.alfame.esb.bpm.queue.BPMTask;
import com.alfame.esb.bpm.queue.BPMTaskResponse;

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
		BPMTaskQueue queue = BPMTaskQueueFactory.getInstance( "bpm://some.success.test.queue" );
		BPMTask task = new BPMTask( null, null, "123" );
		queue.publish( task );
		BPMTaskResponse response = task.waitForResponse();
		LOGGER.info( (String)response.getValue().getValue() );
	}

	@Test
	public void executeBpmListenerErrorTestFlow() throws Exception {
		BPMTaskQueue queue = BPMTaskQueueFactory.getInstance( "bpm://some.error.test.queue" );
		BPMTask task = new BPMTask( null, null, "456" );
		queue.publish( task );
		BPMTaskResponse response = task.waitForResponse();
		LOGGER.info( response.getThrowable().getMessage() );
	}

	@Test
	public void executeBpmStartProcessTestFlow() throws Exception {
		flowRunner( "bpmStartProcessTestFlow" ).run();
		
		Thread.sleep( 1000 * 5 );
	}
	

}
