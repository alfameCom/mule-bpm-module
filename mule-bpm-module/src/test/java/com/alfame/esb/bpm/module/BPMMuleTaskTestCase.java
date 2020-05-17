package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.taskqueue.BPMTask;
import com.alfame.esb.bpm.taskqueue.BPMTaskQueue;
import com.alfame.esb.bpm.taskqueue.BPMTaskQueueFactory;
import com.alfame.esb.bpm.taskqueue.BPMTaskResponse;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class BPMMuleTaskTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-mule-task-config.xml";
    }

    @Test
    public void executeSuccessfulMuleTaskTestFlow() throws Exception {
        BPMTaskQueue queue = BPMTaskQueueFactory.getInstance("bpm://some.success.test.queue");
        Assert.assertNotNull("Task queue should not be NULL", queue);

        BPMTask task = new BPMDummyMuleTask(null, "123");
        boolean publishResult = queue.publish(task);
        Assert.assertEquals("Publish result should be true", true, publishResult);

        BPMTaskResponse response = task.waitForResponse();
        Assert.assertNotNull("Response should not be NULL", response);

        Throwable reponseError = response.getError();
        Assert.assertNull("Error should be NULL", reponseError);

        Object responseValue = response.getValue();
        Assert.assertNotNull("Response value should not be NULL", responseValue);
        Assert.assertEquals("Response value should be set to true", "true", responseValue);
    }

    @Test
    public void executeErroneousMuleTaskTestFlow() throws Exception {
        BPMTaskQueue queue = BPMTaskQueueFactory.getInstance("bpm://some.error.test.queue");
        Assert.assertNotNull("Task queue should not be NULL", queue);

        BPMTask task = new BPMDummyMuleTask(null, "456");
        boolean publishResult = queue.publish(task);
        Assert.assertEquals("Publish result should be true", true, publishResult);

        BPMTaskResponse response = task.waitForResponse();
        Assert.assertNotNull("Response should not be NULL", response);

        Throwable responseError = response.getError();
        Assert.assertNotNull("Error should not be NULL", responseError);
        Assert.assertEquals("Error message should be set to SOME_TEST_ERROR",
                "SOME_TEST_ERROR", responseError.getMessage());

        Object responseValue = response.getValue();
        Assert.assertNotNull("Response value should not be NULL", responseValue);
        Assert.assertEquals("Response value should be set to true", "false", responseValue);
    }

}
