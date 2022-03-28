package com.alfame.esb.bpm.module.internal.impl;

import org.flowable.job.service.JobServiceConfiguration;

public class BPMTenantAwareJobServiceConfiguration extends JobServiceConfiguration {

    final String tenantId;

    public BPMTenantAwareJobServiceConfiguration(String engineName, String tenantId) {
        super(engineName);

        this.tenantId = tenantId;
    }

    @Override
    public void initDataManagers() {
        this.jobDataManager = new BPMTenantAwareJobDataManager(this, this.tenantId);
        this.timerJobDataManager = new BPMTenantAwareTimerJobDataManager(this, this.tenantId);

        super.initDataManagers();
    }
}
