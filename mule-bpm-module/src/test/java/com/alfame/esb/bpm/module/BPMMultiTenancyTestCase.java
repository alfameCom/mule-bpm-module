package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

        BPMEngineEventSubscription processEndSubscription = engine.eventSubscriptionBuilder()
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .processDefinitionKey("testProcess").subscribeForEvents();
        BPMEngineEventSubscription otherProcessEndSubscription = engine.eventSubscriptionBuilder()
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .processDefinitionKey("testProcess").subscribeForEvents();

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

        List<BPMEngineEvent> processEndEvents =
                otherProcessEndSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);
        Assert.assertTrue("One end event for process must be present", processEndEvents.size() == 1);
        List<BPMEngineEvent> otherProcessEndEvents =
                otherProcessEndSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);
        Assert.assertTrue("One end event for other process must be present", otherProcessEndEvents.size() == 1);

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
