package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BPMAttachmentTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-attachment-config.xml";
    }

    @Test
    public void testAttachmentHandlerProcessFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMEngineEventSubscription eventSubscription = engine.eventSubscriptionBuilder()
                .processDefinitionKey("attachmentHandler")
                .eventType(BPMEngineEventType.PROCESS_INSTANCE_ENDED)
                .subscribeForEvents();
        Assert.assertNotNull("Engine subscription should not be NULL", eventSubscription);

        flowRunner("startAttachmentHandlerProcessFlow").run();

        List<BPMEngineEvent> engineEvents = eventSubscription
                .waitForEvents(1, 10, TimeUnit.SECONDS);
        Assert.assertEquals("One ended attachment handler instance must be found",1, engineEvents.size());
    }

}
