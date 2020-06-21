package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BPMVariableTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-variables-config.xml";
    }

    @Test
    public void testProcessVariablesFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription eventSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("variableProcess")
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .eventType(BPMEngineEventType.VARIABLE_CREATED)
                .eventType(BPMEngineEventType.VARIABLE_UPDATED)
                .eventType(BPMEngineEventType.VARIABLE_REMOVED)
                .variable("inputVariable")
                .variable("outputVariable")
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
                .waitForEvents(5, 10, TimeUnit.SECONDS);
        eventSubscription.unsubscribeForEvents();
        Assert.assertNotNull("Returned engine events should not not be NULL", engineEvents);
        Assert.assertEquals("Five events should returned", 5, engineEvents.size());
        
        BPMEngineEventFinder processVariableRemovedFinder = eventSubscription.eventFinder()
                .eventType(BPMEngineEventType.VARIABLE_REMOVED)
                .variable("inputVariable");
        Assert.assertNotNull("Returned process variable removed finder should not not be NULL", processVariableRemovedFinder);
        BPMEngineEvent processVariableRemovedEvent = processVariableRemovedFinder.uniqueEvent();
        Assert.assertNotNull("Returned process variable removed event should not not be NULL", processVariableRemovedEvent);

        BPMEngineEventFinder processVariableCreatedFinder = eventSubscription.eventFinder()
                .eventType(BPMEngineEventType.VARIABLE_CREATED)
                .variable("outputVariable");
        Assert.assertNotNull("Returned process variable created finder should not not be NULL", processVariableCreatedFinder);
        BPMEngineEvent processVariableCreatedEvent = processVariableCreatedFinder.uniqueEvent();
        Assert.assertNotNull("Returned process variable removed event should not not be NULL", processVariableCreatedEvent);
        Assert.assertEquals("outputVariable value should be increased by one", 1 + 1, processVariableCreatedEvent.getVariableValue());
    }

}
