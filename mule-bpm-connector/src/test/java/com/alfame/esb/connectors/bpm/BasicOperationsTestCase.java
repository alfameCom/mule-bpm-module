package com.alfame.esb.connectors.bpm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.alfame.esb.bpm.activity.queue.api.BPMActivityQueue;
import com.alfame.esb.bpm.activity.queue.api.BPMActivityQueueFactory;
import com.alfame.esb.bpm.activity.queue.api.BPMActivity;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.junit.Test;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;

public class BasicOperationsTestCase extends MuleArtifactFunctionalTestCase {

  /**
   * Specifies the mule config xml with the flows that are going to be executed in the tests, this file lives in the test resources.
   */
  @Override
  protected String getConfigFile() {
    return "test-mule-config.xml";
  }

  //@Test
  public void executeSayHiOperation() throws Exception {
    String payloadValue = ((String) flowRunner("sayHiFlow").run()
                                      .getMessage()
                                      .getPayload()
                                      .getValue());
    assertThat(payloadValue, is("Hello Mariano Gonzalez!!!"));
  }

  //@Test
  public void executeRetrieveInfoOperation() throws Exception {
    String payloadValue = ((String) flowRunner("retrieveInfoFlow")
                                      .run()
                                      .getMessage()
                                      .getPayload()
                                      .getValue());
    assertThat(payloadValue, is("Using Configuration [configId] with Connection id [aValue:100]"));
  }

  @Test
  public void executeBpmListenerTestFlow() throws Exception {
    BPMActivityQueue queue = BPMActivityQueueFactory.getInstance( "some.test.queue" );
    BPMActivity activity = new BPMActivity( null, null );
    queue.publish( activity );
    //flowRunner( "bpmListenerTestFlow" ).run();
    activity.waitForResponse();
  }

}
