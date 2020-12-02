package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;
import org.mule.functional.api.flow.FlowRunner;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.event.CoreEvent;

public class BPMMultiTenancyTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-multi-tenancy-config.xml";
    }

    @Test
    public void testProcessBuilderProcessFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);
        BPMEngine otherEngine = BPMEnginePool.getInstance("otherEngineConfig");
        Assert.assertNotNull("Other engine should not be NULL", otherEngine);


        BPMProcessInstanceBuilder otherProcessInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("testProcess")
                .tenantId("com.alfame.esb.other")
                .uniqueBusinessKey("otherone");
        Assert.assertNotNull("Other process instance builder should not be NULL", otherProcessInstanceBuilder);
        BPMProcessInstanceBuilder processInstanceBuilder = otherEngine.processInstanceBuilder()
                .processDefinitionKey("testProcess")
                .tenantId("com.alfame.esb")
                .uniqueBusinessKey("defaultone");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);
        BPMProcessInstance otherProcessInstance = otherProcessInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned other process instance should not not be NULL", otherProcessInstance);

        BPMVariableInstance otherResultVariable = otherEngine.getHistoricVariableInstance(
                otherProcessInstance.getProcessInstanceId(), "result");
        Assert.assertEquals("Result variable should be set to true",
                "true", otherResultVariable.getValue());
        Assert.assertNotEquals("Tenant id must NOT be the default one",
                "com.alfame.esb", otherProcessInstance.getTenantId());
        Assert.assertEquals("Tenant id must match with the other tenant",
                "com.alfame.esb.other", otherProcessInstance.getTenantId());
        Assert.assertEquals("Business key must match with the right one",
                "otherone", otherProcessInstance.getBusinessKey());

        BPMVariableInstance resultVariable = engine.getHistoricVariableInstance(
                processInstance.getProcessInstanceId(), "result");
        Assert.assertEquals("Result variable should be set to true",
                "true", resultVariable.getValue());
        Assert.assertEquals("Tenant id must be the default one",
                "com.alfame.esb", processInstance.getTenantId());
        Assert.assertNotEquals("Tenant id must NOT match with the other tenant",
                "com.alfame.esb.other", processInstance.getTenantId());
        Assert.assertEquals("Business key must match with the right one",
                "defaultone", processInstance.getBusinessKey());
    }

}
