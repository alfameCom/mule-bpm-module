package com.alfame.esb.bpm.connector;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BPMEventSubscriptionTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-process-instance-config.xml";
    }

    @Test
    public void testProcessBuilderProcessWithEventsFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription eventSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("testProcess")
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .eventType(BPMEngineEventType.VARIABLE_CREATED)
                .variableWithValue("result", "true")
                .subscribeForEvents();
        Assert.assertNotNull("Engine subscription should not be NULL", eventSubscription);

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("testProcess").tenantId("com.alfame.esb");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        List<BPMEngineEvent> engineEvents = eventSubscription
                .waitAndUnsubscribeForEvents(2, 5, TimeUnit.SECONDS);
        Assert.assertNotNull("Returned engine events should not not be NULL", engineEvents);
        Assert.assertEquals("Two engine events should be returned", 2, engineEvents.size());

        BPMEngineEventFinder processInstanceEndedFinder = eventSubscription.eventFinder()
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED);
        Assert.assertNotNull("Returned process instance ended finder should not not be NULL", processInstanceEndedFinder);
        BPMEngineEvent processInstanceEndedEvent = processInstanceEndedFinder.uniqueEvent();
        Assert.assertNotNull("Returned process instance ended event should not not be NULL", processInstanceEndedEvent);
        Assert.assertEquals("Returned process instance ended event's process definition key should be set to testProcess",
                "testProcess", processInstanceEndedEvent.getProcessDefinitionKey());
        Assert.assertEquals("Returned process instance ended event's process instance id should be set to same as returned by process instance builder",
                processInstance.getProcessInstanceId(), processInstanceEndedEvent.getProcessInstanceId());

        BPMEngineEventFinder processVariableCreatedFinder = eventSubscription.eventFinder()
                .eventType(BPMEngineEventType.VARIABLE_CREATED);
        Assert.assertNotNull("Returned process variable created finder should not not be NULL", processVariableCreatedFinder);
        BPMEngineEvent processVariableCreatedEvent = processVariableCreatedFinder.uniqueEvent();
        Assert.assertNotNull("Returned process variable created event should not not be NULL", processVariableCreatedEvent);
        Assert.assertEquals("Returned process variable created event's process definition key should be set to testProcess",
                "testProcess", processVariableCreatedEvent.getProcessDefinitionKey());
        Assert.assertEquals("Returned process variable created event's process instance id should be set to same as returned by process instance builder",
                processInstance.getProcessInstanceId(), processVariableCreatedEvent.getProcessInstanceId());
    }

}
