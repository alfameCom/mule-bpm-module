package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias("process-instance-include-process-variables")
public class BPMProcessInstanceIncludeProcessVariables extends BPMProcessInstanceFilter {

    @Parameter
    private String processInstanceId;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

}
