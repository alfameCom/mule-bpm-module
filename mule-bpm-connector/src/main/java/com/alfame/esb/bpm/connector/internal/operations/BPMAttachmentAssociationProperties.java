package com.alfame.esb.bpm.connector.internal.operations;

import org.mule.runtime.extension.api.annotation.param.ExclusiveOptionals;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@ExclusiveOptionals(isOneRequired = true)
public class BPMAttachmentAssociationProperties {

    @Parameter
    @Optional
    private String processInstanceId;

    @Parameter
    @Optional
    private String taskId;

    public String getProcessInstanceId() {
        return this.processInstanceId;
    }

    public String getTaskId() {
        return this.taskId;
    }

}
