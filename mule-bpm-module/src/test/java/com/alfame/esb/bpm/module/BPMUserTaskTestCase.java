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
                .processDefinitionKey("anxiousAsyncProcess")
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .subscribeForEvents();

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("userTaskProcess");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        processSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);

        Assert.assertEquals("No process ending events should be found",
                0, processSubscription.eventFinder().events().size());
    }
}
