package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BPMEventTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-events-config.xml";
    }

    @Test
    public void testProcessEventFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription processEventSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("variableProcess")
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .subscribeForEvents();
        Assert.assertNotNull("Process subscription should not be NULL", processEventSubscription);

        BPMEngineEventSubscription taskEventSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("variableProcess")
                .eventType(BPMEngineEventType.TASK_CREATED)
                .subscribeForEvents();
        Assert.assertNotNull("Task subscription should not be NULL", processEventSubscription);

        BPMEngineEventSubscription taskCompletionEventSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("variableProcess")
                .eventType(BPMEngineEventType.TASK_COMPLETED)
                .subscribeForEvents();
        Assert.assertNotNull("Task completion subscription should not be NULL", taskCompletionEventSubscription);

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("variableProcess")
                .tenantId("com.alfame.esb")
                .variableWithValue("inputVariable", 1);
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        List<BPMEngineEvent> taskEvents = taskEventSubscription
                .waitForEvents(1, 10, TimeUnit.SECONDS);
        taskEventSubscription.unsubscribeForEvents();
        Assert.assertNotNull("Returned task events should not not be NULL", taskEvents);
        Assert.assertEquals("One task event should returned", 1, taskEvents.size());

        Object taskEntity = taskEvents.get(0).getEntity();
        Assert.assertNotNull("Returned task entity should not not be NULL", taskEntity);

        String taskId = FieldUtils.readField(taskEntity, "id",  true).toString();
        Assert.assertNotNull("Returned task id should not not be NULL", taskId);
        String formKey = FieldUtils.readField(taskEntity, "formKey", true).toString();
        Assert.assertNotNull("Returned form key should not not be NULL", formKey);

        final int attempts = 10;
        Map<String, Object> variables = new HashMap<>();
        variables.put("tryAgain", "false");
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                engine.completeTask(taskId, formKey, "Done!", variables);
                break;
            } catch (Exception e) {
                if (attempt < (attempts - 1)) {
                    int ms = 100 + (100 * attempt);
                    LOGGER.info("Waiting for transaction to commit for {} ms", ms);
                    Thread.sleep(ms);
                } else {
                    throw e;
                }
            }
        }

        List<BPMEngineEvent> taskCompletionEvents = taskCompletionEventSubscription
                .waitForEvents(1, 15, TimeUnit.SECONDS);
        taskCompletionEventSubscription.unsubscribeForEvents();
        Assert.assertNotNull("Returned task completion events should not not be NULL", taskCompletionEvents);
        Assert.assertEquals("One task completion event should returned", 1, taskCompletionEvents.size());

        List<BPMEngineEvent> processEvents = processEventSubscription
                .waitForEvents(1, 20, TimeUnit.SECONDS);
        processEventSubscription.unsubscribeForEvents();
        Assert.assertNotNull("Returned process events should not not be NULL", processEvents);
        Assert.assertEquals("One process event should returned", 1, processEvents.size());
    }

}
