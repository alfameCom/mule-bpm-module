package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BPMProcessInstanceQueryTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-process-instance-query-config.xml";
    }

    @Test
    public void testQueryingAllInstancesFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription activitySubscription = engine.eventSubscriptionBuilder()
                .eventType(BPMEngineEventType.ACTIVITY_STARTED)
                .processDefinitionKey("signalSleeperProcess").subscribeForEvents();
        BPMEngineEventSubscription endSubscription = engine.eventSubscriptionBuilder()
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .processDefinitionKey("signalSleeperProcess").subscribeForEvents();

        BPMProcessInstanceBuilder instanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("signalSleeperProcess");
        Assert.assertNotNull("Process instance builder should not be NULL", instanceBuilder);

        BPMProcessInstance startedInstance = instanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", startedInstance);

        BPMProcessInstanceQuery runningQuery = engine.processInstanceQueryBuilder().buildProcessInstanceQuery();
        List<BPMProcessInstance> runningInstances = runningQuery.instances(0, 10);
        BPMProcessInstance runningInstance = null;
        for (BPMProcessInstance instance : runningInstances) {
            if (instance.getProcessInstanceId().equals(startedInstance.getProcessInstanceId())) {
                runningInstance = instance;
            }
        }
        Assert.assertNotNull("Running instance must be found", runningInstance);
        Assert.assertNull("Running instance must be actually running", runningInstance.getEndTime());

        activitySubscription.waitForEvents(1, 5, TimeUnit.SECONDS);

        engine.triggerSignal(startedInstance.getProcessInstanceId(), "wakeUp");

        List<BPMEngineEvent> endEvents = endSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);
        Assert.assertTrue("One end event must be present", endEvents.size() == 1);

        BPMProcessInstanceQuery historicQuery = engine.processInstanceQueryBuilder().buildProcessInstanceQuery();
        List<BPMProcessInstance> historicInstances = historicQuery.instances(0, 10);
        BPMProcessInstance historicInstance = null;
        for (BPMProcessInstance instance : historicInstances) {
            if (instance.getProcessInstanceId().equals(startedInstance.getProcessInstanceId())) {
                historicInstance = instance;
            }
        }
        Assert.assertNotNull("Ended instance must be found", historicInstance);
        Assert.assertNotNull("Ended instance must be ended", historicInstance.getEndTime());
    }

    @Test
    public void testQueryByProcessInstanceIdFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription activitySubscription = engine.eventSubscriptionBuilder()
                .eventType(BPMEngineEventType.ACTIVITY_STARTED)
                .processDefinitionKey("signalSleeperProcess").subscribeForEvents();
        BPMEngineEventSubscription endSubscription = engine.eventSubscriptionBuilder()
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .processDefinitionKey("signalSleeperProcess").subscribeForEvents();

        BPMProcessInstanceBuilder instanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("signalSleeperProcess");
        Assert.assertNotNull("Process instance builder should not be NULL", instanceBuilder);

        BPMProcessInstance startedInstance = instanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", startedInstance);

        BPMProcessInstanceQuery runningQuery = engine.processInstanceQueryBuilder()
                .processInstanceId(startedInstance.getProcessInstanceId())
                .buildProcessInstanceQuery();
        BPMProcessInstance runningInstance = runningQuery.uniqueInstance();
        Assert.assertNotNull("Running instance must be found", runningInstance);
        Assert.assertNull("Running instance must be actually running", runningInstance.getEndTime());

        activitySubscription.waitForEvents(1, 5, TimeUnit.SECONDS);

        engine.triggerSignal(startedInstance.getProcessInstanceId(), "wakeUp");

        List<BPMEngineEvent> endEvents = endSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);
        Assert.assertTrue("One end event must be present", endEvents.size() == 1);

        BPMProcessInstanceQuery historicQuery = engine.processInstanceQueryBuilder()
                .processInstanceId(startedInstance.getProcessInstanceId())
                .buildProcessInstanceQuery();
        BPMProcessInstance historicInstance = historicQuery.uniqueInstance();
        Assert.assertNotNull("Ended instance must be found", historicInstance);
        Assert.assertNotNull("Ended instance must be ended", historicInstance.getEndTime());
    }

    @Test
    public void testQueryByBusinessIdLikeFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription activitySubscription = engine.eventSubscriptionBuilder()
                .eventType(BPMEngineEventType.ACTIVITY_STARTED)
                .processDefinitionKey("signalSleeperProcess").subscribeForEvents();
        BPMEngineEventSubscription endSubscription = engine.eventSubscriptionBuilder()
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .processDefinitionKey("signalSleeperProcess").subscribeForEvents();

        BPMProcessInstanceBuilder instanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("signalSleeperProcess")
                .uniqueBusinessKey("bond007");
        Assert.assertNotNull("Process instance builder should not be NULL", instanceBuilder);

        BPMProcessInstance startedInstance = instanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", startedInstance);

        BPMProcessInstanceQuery runningQuery = engine.processInstanceQueryBuilder()
                .uniqueBusinessKeyLike("bond%")
                .buildProcessInstanceQuery();
        BPMProcessInstance runningInstance = runningQuery.uniqueInstance();
        Assert.assertNotNull("Running instance must be found", runningInstance);
        Assert.assertNull("Running instance must be actually running", runningInstance.getEndTime());

        activitySubscription.waitForEvents(1, 5, TimeUnit.SECONDS);

        engine.triggerSignal(startedInstance.getProcessInstanceId(), "wakeUp");

        List<BPMEngineEvent> endEvents = endSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);
        Assert.assertTrue("One end event must be present", endEvents.size() == 1);

        BPMProcessInstanceQuery historicQuery = engine.processInstanceQueryBuilder()
                .uniqueBusinessKeyLike("bond%")
                .buildProcessInstanceQuery();
        BPMProcessInstance historicInstance = historicQuery.uniqueInstance();
        Assert.assertNotNull("Ended instance must be found", historicInstance);
        Assert.assertNotNull("Ended instance must be ended", historicInstance.getEndTime());
    }

    @Test
    public void testQueryByTenantIdFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription activitySubscription = engine.eventSubscriptionBuilder()
                .eventType(BPMEngineEventType.ACTIVITY_STARTED)
                .processDefinitionKey("signalSleeperProcess").subscribeForEvents();
        BPMEngineEventSubscription endSubscription = engine.eventSubscriptionBuilder()
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .processDefinitionKey("signalSleeperProcess").subscribeForEvents();

        BPMProcessInstanceBuilder instanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("signalSleeperProcess")
                .tenantId("com.alfame.esb");
        Assert.assertNotNull("Process instance builder should not be NULL", instanceBuilder);

        BPMProcessInstance startedInstance = instanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", startedInstance);

        activitySubscription.waitForEvents(1, 5, TimeUnit.SECONDS);

        engine.triggerSignal(startedInstance.getProcessInstanceId(), "wakeUp");

        List<BPMEngineEvent> endEvents = endSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);
        Assert.assertTrue("One end event must be present", endEvents.size() == 1);

        BPMProcessInstanceQuery defaultTenantQuery = engine.processInstanceQueryBuilder()
                .tenantId("com.alfame.esb")
                .processInstanceId(startedInstance.getProcessInstanceId())
                .buildProcessInstanceQuery();
        BPMProcessInstance defaultTenantInstance = defaultTenantQuery.uniqueInstance();
        Assert.assertNotNull("Ended instance must be found with default tenant", defaultTenantInstance);

        BPMProcessInstanceQuery nonExistingTenantQuery = engine.processInstanceQueryBuilder()
                .tenantId("non-existing")
                .processInstanceId(startedInstance.getProcessInstanceId())
                .buildProcessInstanceQuery();
        BPMProcessInstance nonExistingTenantInstance = nonExistingTenantQuery.uniqueInstance();
        Assert.assertNull("Ended instance must NOT be found with non-existing tenant", nonExistingTenantInstance);
    }

}
