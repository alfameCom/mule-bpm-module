package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

@Alias("process-instance-filter")
public class BPMEventSubscriptionProcessInstanceFilter extends BPMEventSubscriptionFilter {

    @Parameter
    @Expression(NOT_SUPPORTED)
    private String processInstanceId;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

}
