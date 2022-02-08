package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.apache.commons.lang3.time.DateUtils;
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

    @Test
    public void testQueryByNameLikeFlow() throws Exception {
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
                .processInstanceName("superior100");
        Assert.assertNotNull("Process instance builder should not be NULL", instanceBuilder);

        BPMProcessInstance startedInstance = instanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", startedInstance);

        activitySubscription.waitForEvents(1, 5, TimeUnit.SECONDS);

        engine.triggerSignal(startedInstance.getProcessInstanceId(), "wakeUp");

        List<BPMEngineEvent> endEvents = endSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);
        Assert.assertTrue("One end event must be present", endEvents.size() == 1);

        BPMProcessInstanceQuery existingNameQuery = engine.processInstanceQueryBuilder()
                .processInstanceNameLike("%superior%")
                .processInstanceId(startedInstance.getProcessInstanceId())
                .buildProcessInstanceQuery();
        BPMProcessInstance existingNameInstance = existingNameQuery.uniqueInstance();
        Assert.assertNotNull("Ended instance with superior name must be found", existingNameInstance);

        BPMProcessInstanceQuery nonExistingNameQuery = engine.processInstanceQueryBuilder()
                .processInstanceNameLike("%lesser%")
                .processInstanceId(startedInstance.getProcessInstanceId())
                .buildProcessInstanceQuery();
        BPMProcessInstance nonExistingNameInstance = nonExistingNameQuery.uniqueInstance();
        Assert.assertNull("Ended instance with lesser name must NOT be found", nonExistingNameInstance);
    }

    @Test
    public void testQueryByVariableLikeFlow() throws Exception {
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
                .variableWithValue("agent", "bond007");
        Assert.assertNotNull("Process instance builder should not be NULL", instanceBuilder);

        BPMProcessInstance startedInstance = instanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", startedInstance);

        activitySubscription.waitForEvents(1, 5, TimeUnit.SECONDS);

        engine.triggerSignal(startedInstance.getProcessInstanceId(), "wakeUp");

        List<BPMEngineEvent> endEvents = endSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);
        Assert.assertTrue("One end event must be present", endEvents.size() == 1);

        BPMProcessInstanceQuery instanceQueryWithVariable = engine.processInstanceQueryBuilder()
                .variable("agent")
                .buildProcessInstanceQuery();
        BPMProcessInstance instanceWithVariable = instanceQueryWithVariable.uniqueInstance();
        Assert.assertNotNull("Ended instance with agent variable must be found", instanceWithVariable);

        BPMProcessInstanceQuery instanceQueryWithVariableValueLike = engine.processInstanceQueryBuilder()
                .variableWithValueLike("agent", "%007")
                .buildProcessInstanceQuery();
        BPMProcessInstance instanceWithVariableValueLike = instanceQueryWithVariableValueLike.uniqueInstance();
        Assert.assertNotNull("Ended instance with agent variable with value 007 must be found", instanceWithVariableValueLike);

        BPMProcessInstanceQuery instanceQueryWithWrongVariableValueLike = engine.processInstanceQueryBuilder()
                .variableWithValueLike("agent", "%006")
                .buildProcessInstanceQuery();
        BPMProcessInstance instanceWithWrongVariableValueLike = instanceQueryWithWrongVariableValueLike.uniqueInstance();
        Assert.assertNull("Ended instance with agent variable with value 006 must NOT be found", instanceWithWrongVariableValueLike);
    }

    @Test
    public void testQueryByDatesFlow() throws Exception {
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
                .startedAfter(DateUtils.addMinutes(startedInstance.getStartTime(), -1))
                .startedBefore(DateUtils.addMinutes(startedInstance.getStartTime(), 1))
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
                .finishedAfter(DateUtils.addMinutes(startedInstance.getStartTime(), -1))
                .finishedBefore(DateUtils.addMinutes(startedInstance.getStartTime(), 1))
                .buildProcessInstanceQuery();
        BPMProcessInstance historicInstance = historicQuery.uniqueInstance();
        Assert.assertNotNull("Ended instance must be found", historicInstance);
        Assert.assertNotNull("Ended instance must be ended", historicInstance.getEndTime());
    }

    @Test
    public void testQueryByRunningStatusFlow() throws Exception {
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

        BPMProcessInstanceQuery runningUnfinishedQuery = engine.processInstanceQueryBuilder()
                .processInstanceId(startedInstance.getProcessInstanceId())
                .onlyUnfinished(true)
                .buildProcessInstanceQuery();
        BPMProcessInstance runningUnfinishedInstance = runningUnfinishedQuery.uniqueInstance();
        Assert.assertNotNull("Running unfinished instance must be found", runningUnfinishedInstance);
        Assert.assertNull("Running unfinished instance must be actually running", runningUnfinishedInstance.getEndTime());

        BPMProcessInstanceQuery runningFinishedQuery = engine.processInstanceQueryBuilder()
                .processInstanceId(startedInstance.getProcessInstanceId())
                .onlyFinished(true)
                .buildProcessInstanceQuery();
        BPMProcessInstance runningFinishedInstance = runningFinishedQuery.uniqueInstance();
        Assert.assertNull("Running finished instance must NOT be found", runningFinishedInstance);

        activitySubscription.waitForEvents(1, 5, TimeUnit.SECONDS);

        engine.triggerSignal(startedInstance.getProcessInstanceId(), "wakeUp");

        List<BPMEngineEvent> endEvents = endSubscription.waitForEvents(1, 5, TimeUnit.SECONDS);
        Assert.assertTrue("One end event must be present", endEvents.size() == 1);

        BPMProcessInstanceQuery historicUnfinishedQuery = engine.processInstanceQueryBuilder()
                .processInstanceId(startedInstance.getProcessInstanceId())
                .onlyUnfinished(true)
                .buildProcessInstanceQuery();
        BPMProcessInstance historicUnfinishedInstance = historicUnfinishedQuery.uniqueInstance();
        Assert.assertNull("Historic unfinished instance must NOT be found", historicUnfinishedInstance);

        BPMProcessInstanceQuery historicFinishedQuery = engine.processInstanceQueryBuilder()
                .processInstanceId(startedInstance.getProcessInstanceId())
                .onlyFinished(true)
                .buildProcessInstanceQuery();
        BPMProcessInstance historicFinishedInstance = historicFinishedQuery.uniqueInstance();
        Assert.assertNotNull("Historic finished instance must be found", historicFinishedInstance);
        Assert.assertNotNull("Historic finished instance must have end date", historicFinishedInstance.getEndTime());
    }

}
