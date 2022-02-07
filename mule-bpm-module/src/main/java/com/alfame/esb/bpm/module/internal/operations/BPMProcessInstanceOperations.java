package com.alfame.esb.bpm.module.internal.operations;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import com.alfame.esb.bpm.module.internal.BPMExtension;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;

public class BPMProcessInstanceOperations {
    private static final Logger LOGGER = getLogger(BPMProcessInstanceOperations.class);

    @Alias("delete-process-instance")
    public void deleteProcessInstance(
            @Config BPMExtension config,
            @DisplayName("Process instance Id") String processInstanceId,
            @Optional @DisplayName("Delete reason") String deleteReason) throws IOException {
        
        config.deleteProcessInstance(processInstanceId, deleteReason);

        LOGGER.debug("Trying to delete process instance id {} with reason {}", processInstanceId, deleteReason);
    }
}
