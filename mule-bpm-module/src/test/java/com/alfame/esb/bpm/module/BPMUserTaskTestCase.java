package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class BPMUserTaskTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-user-task-config.xml";
    }

    @Test
    public void userTaskTest() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription processSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("userTaskProcess")
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .subscribeForEvents();

        BPMEngineEventSubscription userTaskSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("userTaskProcess")
                .eventType(BPMEngineEventType.TASK_CREATED)
                .subscribeForEvents();

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("userTaskProcess");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        userTaskSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);

        processSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);

        Assert.assertEquals("User task event should be found",
                1, userTaskSubscription.eventFinder().events().size());

        Assert.assertEquals("Activity name must match the element on BPMN",
                "handleFailure", userTaskSubscription.eventFinder().uniqueEvent().getActivityName());

        Assert.assertEquals("No process ending events should be found",
                0, processSubscription.eventFinder().events().size());
    }
}
