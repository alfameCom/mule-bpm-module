package com.alfame.esb.bpm.module.api.config;

import com.alfame.esb.bpm.module.api.param.BPMEventType;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

@Alias("event-type-filter")
public class BPMEventSubscriptionEventTypeFilter extends BPMEventSubscriptionFilter {

    @Parameter
    @Expression(NOT_SUPPORTED)
    private BPMEventType eventType;

    public BPMEventType getEventType() {
        return eventType;
    }

}
