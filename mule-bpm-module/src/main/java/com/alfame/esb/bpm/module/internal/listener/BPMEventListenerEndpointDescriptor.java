package com.alfame.esb.bpm.module.internal.listener;

import com.alfame.esb.bpm.module.api.config.BPMEventSubscriptionFilter;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class BPMEventListenerEndpointDescriptor {

    @Parameter
    @Alias("event-filters")
    @Optional
    private List<BPMEventSubscriptionFilter> eventSubscriptionFilters;

    @Parameter
    @Optional(defaultValue = "5")
    private int timeout;

    @Parameter
    @Optional(defaultValue = "SECONDS")
    private TimeUnit timeoutUnit = SECONDS;

    public int getTimeout() {
        return timeout;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    public long getQueueTimeoutInMillis() {
        return timeoutUnit.toMillis(timeout);
    }

    public List<BPMEventSubscriptionFilter> getEventSubscriptionFilters() {
        return this.eventSubscriptionFilters;
    }
}
