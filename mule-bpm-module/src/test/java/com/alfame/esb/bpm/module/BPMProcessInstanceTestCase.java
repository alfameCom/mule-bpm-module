package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;
import org.mule.functional.api.flow.FlowRunner;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.event.CoreEvent;

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
                .processDefinitionKey("testProcess")
                .uniqueBusinessKey("theone")
                .processInstanceName("theone");
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
    public void testUniqueBusinessKeysFlow() {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("testProcess")
                .uniqueBusinessKey("uniqueone");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);
        BPMProcessInstanceBuilder otherProcessInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("testProcess")
                .uniqueBusinessKey("uniqueone");
        Assert.assertNotNull("Other process instance builder should not be NULL", otherProcessInstanceBuilder);


        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        try {
            otherProcessInstanceBuilder.startProcessInstance();
            Assert.fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException ignored) {}
    }

    @Test
    public void testReturningUniqueBusinessKeysFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("testProcess")
                .uniqueBusinessKey("uniquereturnone");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);
        BPMProcessInstanceBuilder otherProcessInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("testProcess")
                .uniqueBusinessKey("uniquereturnone")
                .returnCollidedInstance(true);
        Assert.assertNotNull("Other process instance builder should not be NULL", otherProcessInstanceBuilder);


        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        BPMProcessInstance otherProcessInstance = otherProcessInstanceBuilder.startProcessInstance();
        Assert.assertEquals("Other process instance must be the same as original one",
                processInstance.getProcessInstanceId(), otherProcessInstance.getProcessInstanceId());
    }

}
