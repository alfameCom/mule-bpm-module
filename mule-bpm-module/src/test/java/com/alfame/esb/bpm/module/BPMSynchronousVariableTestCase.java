package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BPMSynchronousVariableTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-synchronous-variables-config.xml";
    }

    @Test
    public void testProcessVariablesFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription eventSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("synchronousVariableProcess")
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .eventType(BPMEngineEventType.VARIABLE_CREATED)
                .variable("firstVariable")
                .variable("secondVariable")
                .variable("thirdVariable")
                .subscribeForEvents();
        Assert.assertNotNull("Engine subscription should not be NULL", eventSubscription);

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("synchronousVariableProcess")
                .tenantId("com.alfame.esb")
                .variableWithValue("firstVariable", "Important value");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        List<BPMEngineEvent> engineEvents = eventSubscription
                .waitForEvents(1 + 3, 10, TimeUnit.SECONDS);
        eventSubscription.unsubscribeForEvents();
        Assert.assertNotNull("Returned engine events should not not be NULL", engineEvents);
        Assert.assertEquals("Four events should returned", 4, engineEvents.size());

        BPMEngineEventFinder finder = eventSubscription.eventFinder()
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED);
        Assert.assertNotNull("Returned process instance ended finder should not not be NULL", finder);
        BPMEngineEvent finderEvent = finder.uniqueEvent();
        Assert.assertNotNull("Returned process instance ended event should not not be NULL", finderEvent);

        finder = eventSubscription.eventFinder()
                .eventType(BPMEngineEventType.VARIABLE_CREATED)
                .variable("firstVariable");
        Assert.assertNotNull("Returned process firstVariable created finder should not not be NULL", finder);
        finderEvent = finder.uniqueEvent();
        Assert.assertNotNull("Returned process firstVariable created event should not not be NULL", finderEvent);
        Assert.assertEquals("Value firstVariable should be Important value", "Important value", finderEvent.getVariableValue());

        finder = eventSubscription.eventFinder()
                .eventType(BPMEngineEventType.VARIABLE_CREATED)
                .variable("secondVariable");
        Assert.assertNotNull("Returned process secondVariable created finder should not not be NULL", finder);
        finderEvent = finder.uniqueEvent();
        Assert.assertNotNull("Returned process secondVariable created event should not not be NULL", finderEvent);
        Assert.assertEquals("Value secondVariable should be Important value", "Important value", finderEvent.getVariableValue());

        finder = eventSubscription.eventFinder()
                .eventType(BPMEngineEventType.VARIABLE_CREATED)
                .variable("thirdVariable");
        Assert.assertNotNull("Returned process thirdVariable created finder should not not be NULL", finder);
        finderEvent = finder.uniqueEvent();
        Assert.assertNotNull("Returned process thirdVariable created event should not not be NULL", finderEvent);
        Assert.assertEquals("Value thirdVariable should be Important value", "Important value", finderEvent.getVariableValue());
    }

}
