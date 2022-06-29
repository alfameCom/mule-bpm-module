package com.alfame.esb.bpm.module;

import com.alfame.esb.bpm.api.*;
import org.junit.Assert;
import org.junit.Test;

public class BPMProcessInstanceDeleteTestCase extends BPMAbstractTestCase {

    @Override
    protected String getConfigFile() {
        return "test-process-instance-query-config.xml";
    }

    @Test
    public void testDeleteProcessInstance() throws Exception {
        BPMEngine engine = BPMEnginePool.getInstance("engineConfig");
        Assert.assertNotNull("Engine should not be NULL", engine);

        BPMProcessInstanceBuilder processInstanceBuilder = engine.processInstanceBuilder()
                .processDefinitionKey("signalSleeperProcess");
        Assert.assertNotNull("Process instance builder should not be NULL", processInstanceBuilder);

        BPMProcessInstance processInstance = processInstanceBuilder.startProcessInstance();
        Assert.assertNotNull("Returned process instance should not not be NULL", processInstance);

        BPMProcessInstanceQuery runningQuery = engine.processInstanceQueryBuilder()
                .processInstanceId(processInstance.getProcessInstanceId())
                .buildProcessInstanceQuery();
        processInstance = runningQuery.uniqueInstance();
        Assert.assertNotNull("Running instance must be found", processInstance);
        Assert.assertNull("Running instance must be actually running", processInstance.getEndTime());

        engine.deleteProcessInstance(processInstance.getProcessInstanceId(), "Test delete");

        processInstance = runningQuery.uniqueInstance();
        Assert.assertNotNull("Running instance should be shutdown and ended", processInstance.getEndTime());
    }

}
