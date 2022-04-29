package com.alfame.esb.bpm.module.api.config;

import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;

abstract public class BPMAsyncExecutorFactory {

    abstract public AsyncExecutor createAsyncExecutor();

}
