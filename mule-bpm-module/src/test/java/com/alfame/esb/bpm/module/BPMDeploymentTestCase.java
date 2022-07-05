package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.BPMEngine;
import com.alfame.esb.bpm.api.BPMEnginePool;
import com.alfame.esb.bpm.api.BPMProcessInstance;
import org.junit.Assert;
import org.junit.Test;
import org.mule.runtime.api.lifecycle.Startable;

public class BPMDeploymentTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-deployment-config.xml";
    }

    @Test
    public void testUniqueDeploymentFlow() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        Startable startable = (Startable) engine;
        Assert.assertNotNull("Startable should not be NULL", startable);

        BPMProcessInstance firstProcessInstance = engine.processInstanceBuilder()
                .processDefinitionKey("testProcess").startProcessInstance();
        Assert.assertNotNull("First process instance should not be NULL", firstProcessInstance);

        startable.start();

        engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("New engine should not be NULL", engine);

        BPMProcessInstance secondProcessInstance = engine.processInstanceBuilder()
                .processDefinitionKey("testProcess").startProcessInstance();
        Assert.assertNotNull("Second process instance should not be NULL", firstProcessInstance);

        Assert.assertEquals("Process definition id must be the same after re-deployment attempt",
                firstProcessInstance.getProcessDefinitionId(), secondProcessInstance.getProcessDefinitionId());
    }

}
