package com.alfame.esb.bpm.examples;

import com.alfame.esb.bpm.module.api.config.BPMAsyncExecutorFactory;

import org.flowable.job.api.JobInfo;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.job.service.impl.asyncexecutor.DefaultAsyncJobExecutor;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class LoggingAsyncExecutorFactory extends BPMAsyncExecutorFactory {
    private static final Logger LOGGER = getLogger(LoggingAsyncExecutorFactory.class);
    @Override
    public AsyncExecutor createAsyncExecutor() {
        return new LoggingAsyncExecutor();
    }
    
    protected class LoggingAsyncExecutor extends DefaultAsyncJobExecutor {

		@Override
		protected boolean executeAsyncJob(JobInfo jobInfo, Runnable runnable) {
			LOGGER.info("Executing {}",jobInfo.getId());
			return super.executeAsyncJob(jobInfo, runnable);
		}
    	
    }

}
