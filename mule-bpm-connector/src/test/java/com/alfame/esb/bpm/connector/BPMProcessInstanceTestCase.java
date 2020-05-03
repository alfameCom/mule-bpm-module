package com.alfame.esb.bpm.connector;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;
import org.mule.functional.api.flow.FlowRunner;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.event.CoreEvent;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BPMProcessInstanceTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-process-instance-config.xml";
    }

    @Test
    public void testProcessBuilderProcessFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("testProcess").tenantId("com.alfame.esb");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        BPMVariableInstance resultVariable = engine.getHistoricVariableInstance(
                processInstance.getProcessInstanceId(), "result");
        Assert.assertEquals("Result variable should be set to true", "true", resultVariable.getValue());
    }

    @Test
    public void testProcessFactoryProcessFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        FlowRunner flowRunner = flowRunner("startTestProcessFlow");
        Assert.assertNotNull("Runner should not be NULL", flowRunner);

        CoreEvent event = flowRunner.run();
        Assert.assertNotNull("Event returned by Runner should not be NULL", event);

        Assert.assertFalse("Returned event should not have an error present", event.getError().isPresent());

        Message message = event.getMessage();
        Assert.assertNotNull("Returned message should not not be NULL", message);

        TypedValue payload = message.getPayload();
        Assert.assertNotNull("Returned payload should not not be NULL", payload);

        BPMProcessInstance processInstance = (BPMProcessInstance) payload.getValue();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        BPMVariableInstance resultVariable = engine.getHistoricVariableInstance(
                processInstance.getProcessInstanceId(), "result");
        Assert.assertEquals("Result variable should be set to true", "true", resultVariable.getValue());
    }

    @Test
    public void testProcessBuilderProcessWithEventsFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder = engine.eventSubscriptionBuilder();
        Assert.assertNotNull("Engine subscription builder should not be NULL", eventSubscriptionBuilder);
        eventSubscriptionBuilder
                .processDefinitionKey("testProcess")
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .eventType(BPMEngineEventType.VARIABLE_CREATED)
                .variableWithValue("result", "true")
                .subscribeForEvents();

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("testProcess").tenantId("com.alfame.esb");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        List<BPMEngineEvent> engineEvents = eventSubscriptionBuilder
                .waitAndUnsubscribeForEvents(2, 5, TimeUnit.SECONDS);
        Assert.assertNotNull("Returned engine events should not not be NULL", engineEvents);
        Assert.assertEquals("Two engine events should be returned", 2, engineEvents.size());

        BPMEngineEvent processInstanceEndedEvent = null;
        for (BPMEngineEvent engineEvent : engineEvents) {
            if (engineEvent.getType().equals(BPMEngineEventType.PROCESS_INSTANCE_ENDED)) {
                processInstanceEndedEvent = engineEvent;
            }
        }
        Assert.assertNotNull("Returned process instance ended event should not not be NULL", processInstanceEndedEvent);
        Assert.assertEquals("Returned process instance ended event's process definition key should be set to testProcess",
                "testProcess", processInstanceEndedEvent.getProcessDefinitionKey());
        Assert.assertEquals("Returned process instance ended event's process instance id should be set to same as returned by process instance builder",
                processInstance.getProcessInstanceId(), processInstanceEndedEvent.getProcessInstanceId());

        BPMEngineEvent processVariableCreatedEvent = null;
        for (BPMEngineEvent engineEvent : engineEvents) {
            if (engineEvent.getType().equals(BPMEngineEventType.VARIABLE_CREATED)) {
                processVariableCreatedEvent = engineEvent;
            }
        }
        Assert.assertNotNull("Returned process variable created event should not not be NULL", processVariableCreatedEvent);
        Assert.assertEquals("Returned process variable created event's process definition key should be set to testProcess",
                "testProcess", processVariableCreatedEvent.getProcessDefinitionKey());
        Assert.assertEquals("Returned process variable created event's process instance id should be set to same as returned by process instance builder",
                processInstance.getProcessInstanceId(), processVariableCreatedEvent.getProcessInstanceId());
    }

}
