package com.alfame.esb.bpm.module.internal.impl;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.flowable.common.engine.impl.Page;
import org.flowable.job.service.JobServiceConfiguration;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntity;
import org.flowable.job.service.impl.persistence.entity.data.impl.MybatisTimerJobDataManager;
import org.slf4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMTenantAwareTimerJobDataManager extends MybatisTimerJobDataManager {

    private static final Logger LOGGER = getLogger(BPMTenantAwareTimerJobDataManager.class);

    final String tenantId;

    public BPMTenantAwareTimerJobDataManager(JobServiceConfiguration jobServiceConfiguration, String tenantId) {
        super(jobServiceConfiguration);

        this.tenantId = tenantId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TimerJobEntity> findExpiredJobs(List<String> enabledCategories, Page page) {
        LOGGER.debug("Finding expired timer jobs for tenant {}", tenantId);

        Map<String, Object> params = getParams(enabledCategories);

        List<TimerJobEntity> timerJobs = getDbSqlSession().selectList("selectTenantAwareExpiredTimerJobs", params, page);
        if (LOGGER.isDebugEnabled() && timerJobs != null) {
            for (TimerJobEntity timerJob : timerJobs) {
                LOGGER.debug("Found expired timer job for tenant {}: {}", tenantId, ReflectionToStringBuilder.toString(timerJob));
            }
        }

        return timerJobs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TimerJobEntity> findJobsToExecute(List<String> enabledCategories, Page page) {
        LOGGER.debug("Finding timer jobs for tenant {}", tenantId);

        Map<String, Object> params = getParams(enabledCategories);

        List<TimerJobEntity> timerJobs = getDbSqlSession().selectList("selectTenantAwareTimerJobsToExecute", params, page);
        if (LOGGER.isDebugEnabled() && timerJobs != null) {
            for (TimerJobEntity timerJob : timerJobs) {
                LOGGER.debug("Found timer job for tenant {}: {}", tenantId, ReflectionToStringBuilder.toString(timerJob));
            }
        }

        return timerJobs;
    }

private Map<String, Object> getParams(List<String> enabledCategories) {
    Map<String, Object> params = new HashMap<>();

    params.put("jobExecutionScope", jobServiceConfiguration.getJobExecutionScope());

    Date now = jobServiceConfiguration.getClock().getCurrentTime();
    params.put("now", now);

    if (enabledCategories != null && !enabledCategories.isEmpty()) {
        params.put("enabledCategories", enabledCategories);
    }

    params.put("tenantId", tenantId);

    return params;
}

}
