package com.alfame.esb.bpm.connector;

import static java.util.Optional.ofNullable;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import com.alfame.esb.bpm.queue.BPMTaskQueue;
import com.alfame.esb.bpm.queue.BPMTaskQueueFactory;
import com.alfame.esb.bpm.queue.BPMBaseTask;
import com.alfame.esb.bpm.queue.BPMTaskResponse;

import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.test.runner.ArtifactClassLoaderRunnerConfig;
import org.junit.Test;
import org.slf4j.Logger;

@ArtifactClassLoaderRunnerConfig(
		applicationSharedRuntimeLibs = { "com.h2database:h2", "com.alfame.esb:mule-bpm-api", "com.alfame.esb:mule-bpm-queue" } )
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
		
		BPMBaseTask task = new DummyMuleTask( null, "123" );
		queue.publish( task );
		
		BPMTaskResponse response = task.waitForResponse();
		
		assertThat( response, is( notNullValue() ) );
		assertThat( response.getValue(), is( notNullValue() ) );
		assertThat( response.getValue().getValue(), is( notNullValue() ) );
		LOGGER.info( (String) response.getValue().getValue() );
		assertThat( response.getValue().getValue(), is( "true" ) );
		assertThat( response.getThrowable(), is( nullValue() ) );
		
	}

	@Test
	public void executeBpmListenerErrorTestFlow() throws Exception {
		BPMTaskQueue queue = BPMTaskQueueFactory.getInstance( "bpm://some.error.test.queue" );
		
		BPMBaseTask task = new DummyMuleTask( null, "456" );
		queue.publish( task );
		
		BPMTaskResponse response = task.waitForResponse();

		assertThat( response, is( notNullValue() ) );
		assertThat( response.getValue(), is( notNullValue() ) );
		assertThat( response.getValue().getValue(), is( notNullValue() ) );
		LOGGER.info( (String) response.getValue().getValue() );
		assertThat( response.getValue().getValue(), is( "false" ) );
		assertThat( response.getThrowable(), is( notNullValue() ) );
	}

	@Test
	public void executeBpmStartProcessTestFlow() throws Exception {
		flowRunner( "bpmStartProcessTestFlow" ).run();
		
		Thread.sleep( 1000 * 5 );
	}
	
	protected class DummyMuleTask extends BPMBaseTask {
		
		private final Object payload;
		private final String correlationId;

		DummyMuleTask( Object payload, String correlationId ) {
			this.payload = payload;
			this.correlationId = correlationId;
		}
		
		@Override
		public String getActivityId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<String> getCorrelationId() {
			return ofNullable( correlationId );
		}

		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getParentId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getPayload() {
			return payload;
		}

		@Override
		public String getProcessInstanceId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getSuperExecutionId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getTenantId() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
