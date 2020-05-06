package com.alfame.esb.bpm.connector.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

@Alias("process-definition-filter")
public class BPMEventSubscriptionProcessDefinitionFilter extends BPMEventSubscriptionFilter {

    @Parameter
    @Expression(NOT_SUPPORTED)
    private String key;

    public String getKey() {
        return key;
    }

}
