package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BPMEventTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-events-config.xml";
    }

    @Test
    public void testProcessEventFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription eventSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("variableProcess")
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .subscribeForEvents();
        Assert.assertNotNull("Engine subscription should not be NULL", eventSubscription);

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("variableProcess")
                .tenantId("com.alfame.esb")
                .variableWithValue("inputVariable", 1);
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        List<BPMEngineEvent> engineEvents = eventSubscription
                .waitForEvents(1, 10, TimeUnit.SECONDS);
        eventSubscription.unsubscribeForEvents();
        Assert.assertNotNull("Returned engine events should not not be NULL", engineEvents);
        Assert.assertEquals("One events should returned", 1, engineEvents.size());
    }

}
