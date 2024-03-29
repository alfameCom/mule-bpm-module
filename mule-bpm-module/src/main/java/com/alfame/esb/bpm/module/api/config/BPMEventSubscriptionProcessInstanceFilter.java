package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias("process-instance-filter")
public class BPMEventSubscriptionProcessInstanceFilter extends BPMEventSubscriptionFilter {

    @Parameter
    private String processInstanceId;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

}
