package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import com.alfame.esb.bpm.taskqueue.BPMTask;
import com.alfame.esb.bpm.taskqueue.BPMTaskQueue;
import com.alfame.esb.bpm.taskqueue.BPMTaskQueueFactory;
import com.alfame.esb.bpm.taskqueue.BPMTaskResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

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

    @Test(expected = java.util.concurrent.TimeoutException.class)
    public void timingOutMuleTaskTestFlow() throws Throwable {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("anxiousProcess");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        try {
            processInstanceBuilder.startProcessInstance();
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void timingOutAsyncMuleTaskTestFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription activitySubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("anxiousAsyncProcess")
                .eventType(BPMEngineEventType.ACTIVITY_FAILURE)
                .subscribeForEvents();

        BPMEngineEventSubscription processSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("anxiousAsyncProcess")
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .subscribeForEvents();

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("anxiousAsyncProcess");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        activitySubscription.waitForEvents(2, 30, TimeUnit.SECONDS);

        Assert.assertEquals("No process ending events should be found",
                0, processSubscription.eventFinder().events().size());

        Assert.assertEquals("Two failures should be found (one retry)",
                2, activitySubscription.eventFinder().events().size());

        Assert.assertTrue("Timeout exception message should be found",
                activitySubscription.eventFinder().events().get(1).getExceptionMessage().contains("java.util.concurrent.TimeoutException"));

        Assert.assertEquals("Activity name must match the element on BPMN",
                "longRunningTask", activitySubscription.eventFinder().events().get(1).getActivityName());
    }
}
