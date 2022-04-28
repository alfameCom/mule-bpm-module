package com.alfame.esb.bpm.module.internal.impl;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.flowable.common.engine.impl.Page;
import org.flowable.job.service.JobServiceConfiguration;
import org.flowable.job.service.impl.persistence.entity.JobEntity;
import org.flowable.job.service.impl.persistence.entity.data.impl.MybatisJobDataManager;
import org.slf4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMTenantAwareJobDataManager extends MybatisJobDataManager {

    private static final Logger LOGGER = getLogger(BPMTenantAwareJobDataManager.class);

    final String tenantId;

    public BPMTenantAwareJobDataManager(JobServiceConfiguration jobServiceConfiguration, String tenantId) {
        super(jobServiceConfiguration);

        this.tenantId = tenantId;
    }

    @Override
    public List<JobEntity> findJobsToExecute(List<String> enabledCategories, Page page) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("jobExecutionScope", jobServiceConfiguration.getJobExecutionScope());

        if (enabledCategories != null && enabledCategories.size() > 0) {
            params.put("enabledCategories", enabledCategories);
        }

        params.put("tenantId", tenantId);
        LOGGER.debug("Finding jobs for tenant {}", tenantId);

        List<JobEntity> jobs = getDbSqlSession().selectList("selectTenantAwareJobsToExecute", params, page);
        if (LOGGER.isDebugEnabled() && jobs != null) {
            for (JobEntity job : jobs) {
                LOGGER.debug("Found job for tenant {}: {}", tenantId, ReflectionToStringBuilder.toString(job));
            }
        }

        return jobs;
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<JobEntity> findExpiredJobs(List<String> enabledCategories, Page page) {
        Map<String, Object> params = new HashMap<>();
        params.put("jobExecutionScope", jobServiceConfiguration.getJobExecutionScope());
        Date now = jobServiceConfiguration.getClock().getCurrentTime();
        params.put("now", now);
        Date maxTimeout = new Date(now.getTime() - jobServiceConfiguration.getAsyncExecutorResetExpiredJobsMaxTimeout());
        params.put("maxTimeout", maxTimeout);

        if (enabledCategories != null && enabledCategories.size() > 0) {
            params.put("enabledCategories", enabledCategories);
        }

        params.put("tenantId", tenantId);
        LOGGER.debug("Finding expired jobs for tenant {} with max timeout {}", tenantId, maxTimeout);

        List<JobEntity> jobs = getDbSqlSession().selectList("selectTenantAwareExpiredJobs", params, page);
        if (LOGGER.isDebugEnabled() && jobs != null) {
            for (JobEntity job : jobs) {
                LOGGER.debug("Found expired job for tenant {}: {}", tenantId, ReflectionToStringBuilder.toString(job));
            }
        }

        return jobs;
    }

}
