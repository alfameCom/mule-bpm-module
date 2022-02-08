package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

@Alias("process-instance-definition-filter")
public class BPMProcessInstanceProcessDefinitionFilter extends BPMProcessInstanceFilter {

    @Parameter
    @Expression(NOT_SUPPORTED)
    private String key;

    public String getKey() {
        return key;
    }

}
