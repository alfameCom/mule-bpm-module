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

}
