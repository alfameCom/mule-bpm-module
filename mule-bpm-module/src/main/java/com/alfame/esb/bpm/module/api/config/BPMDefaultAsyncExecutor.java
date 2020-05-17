package com.alfame.esb.bpm.module.api.config;

import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.job.service.impl.asyncexecutor.DefaultAsyncJobExecutor;
import org.flowable.job.service.impl.asyncexecutor.multitenant.TenantAwareAsyncExecutor;
import org.flowable.job.service.impl.asyncexecutor.multitenant.TenantAwareAsyncExecutorFactory;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import java.lang.reflect.Constructor;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

@Alias("default-async-executor")
public class BPMDefaultAsyncExecutor extends BPMAsyncExecutor implements TenantAwareAsyncExecutorFactory {

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

    @Override
    public AsyncExecutor createAsyncExecutor(Initialisable initialisable, TenantInfoHolder tenantInfoHolder) throws InitialisationException {
        AsyncExecutor asyncExecutor = null;

        try {
            Class<?> asyncExecutorClass = Class.forName(this.getClassName());
            Constructor<?> asyncExecutorCostructor = null;
            try {
                asyncExecutorCostructor = asyncExecutorClass.getConstructor(TenantInfoHolder.class, TenantAwareAsyncExecutorFactory.class);
                asyncExecutor = (TenantAwareAsyncExecutor) asyncExecutorCostructor.newInstance(tenantInfoHolder, this);
            } catch (NoSuchMethodException factoriedException) {
                asyncExecutorCostructor = asyncExecutorClass.getConstructor(TenantInfoHolder.class);
                asyncExecutor = (TenantAwareAsyncExecutor) asyncExecutorCostructor.newInstance(tenantInfoHolder);
            }
        } catch (Exception e) {
            throw new InitialisationException(e, initialisable);
        }
        asyncExecutor.setDefaultAsyncJobAcquireWaitTimeInMillis(this.getDefaultAsyncJobAcquireWaitTimeInMillis());
        asyncExecutor.setDefaultTimerJobAcquireWaitTimeInMillis(this.getDefaultTimerJobAcquireWaitTimeInMillis());
        asyncExecutor.setMaxAsyncJobsDuePerAcquisition(this.getMaxAsyncJobsDuePerAcquisition());
        asyncExecutor.setMaxTimerJobsPerAcquisition(this.getMaxTimerJobsPerAcquisition());

        return asyncExecutor;
    }

    @Override
    public AsyncExecutor createAsyncExecutor(String tenantId) {
        DefaultAsyncJobExecutor tenantExecutor = null;

        tenantExecutor = new DefaultAsyncJobExecutor();
        tenantExecutor.setCorePoolSize(this.getMinThreads());
        tenantExecutor.setMaxPoolSize(this.getMaxThreads());

        return tenantExecutor;
    }

}
