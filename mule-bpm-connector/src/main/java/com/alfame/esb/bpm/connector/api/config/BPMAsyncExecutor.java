package com.alfame.esb.bpm.connector.api.config;

import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;

abstract public class BPMAsyncExecutor {

    abstract public AsyncExecutor createAsyncExecutor(Initialisable initialisable, TenantInfoHolder tenantInfoHolder) throws InitialisationException;

}
