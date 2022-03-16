package com.alfame.esb.bpm.module.internal.listener;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class BPMEventListenerEndpointDescriptor {

    @Parameter
    @Optional(defaultValue = "5")
    private int timeout;

    @Parameter
    @Optional(defaultValue = "SECONDS")
    private TimeUnit timeoutUnit = SECONDS;

    public BPMEventListenerEndpointDescriptor() {
    }

    public int getTimeout() {
        return timeout;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    public long getQueueTimeoutInMillis() {
        return timeoutUnit.toMillis(timeout);
    }

}
