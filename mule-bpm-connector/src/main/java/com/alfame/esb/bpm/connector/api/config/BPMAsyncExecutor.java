package com.alfame.esb.bpm.connector.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

@Alias("async-executor")
public class BPMAsyncExecutor {

    @Parameter
    @Placement(order = 1)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "2")
    private int minThreads;

    @Parameter
    @Placement(order = 2)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "10")
    private int maxThreads;

    @Parameter
    @Placement(order = 3)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "10000")
    private int defaultAsyncJobAcquireWaitTimeInMillis;

    @Parameter
    @Placement(order = 4)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "10000")
    private int defaultTimerJobAcquireWaitTimeInMillis;

    @Parameter
    @Placement(order = 5)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "1")
    private int maxAsyncJobsDuePerAcquisition;

    @Parameter
    @Placement(order = 6)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "1")
    private int maxTimerJobsPerAcquisition;

    @Parameter
    @Placement(order = 7)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "org.flowable.job.service.impl.asyncexecutor.multitenant.ExecutorPerTenantAsyncExecutor")
    private String className;

    public int getMinThreads() {
        return minThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getDefaultAsyncJobAcquireWaitTimeInMillis() {
        return defaultAsyncJobAcquireWaitTimeInMillis;
    }

    public int getDefaultTimerJobAcquireWaitTimeInMillis() {
        return defaultTimerJobAcquireWaitTimeInMillis;
    }

    public int getMaxAsyncJobsDuePerAcquisition() {
        return maxAsyncJobsDuePerAcquisition;
    }

    public int getMaxTimerJobsPerAcquisition() {
        return maxTimerJobsPerAcquisition;
    }

    public String getClassName() {
        return className;
    }

}
