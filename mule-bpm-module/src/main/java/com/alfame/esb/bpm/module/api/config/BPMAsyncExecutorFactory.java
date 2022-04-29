package com.alfame.esb.bpm.module.api.config;

import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;

public abstract class BPMAsyncExecutorFactory {

    public abstract AsyncExecutor createAsyncExecutor();

}
