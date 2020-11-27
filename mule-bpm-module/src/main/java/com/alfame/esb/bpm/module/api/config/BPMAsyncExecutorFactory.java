package com.alfame.esb.bpm.module.api.config;

import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;

abstract public class BPMAsyncExecutorFactory {

    abstract public AsyncExecutor createAsyncExecutor();

}
