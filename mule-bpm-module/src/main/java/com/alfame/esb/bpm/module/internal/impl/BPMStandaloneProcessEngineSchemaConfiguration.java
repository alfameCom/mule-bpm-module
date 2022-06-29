package com.alfame.esb.bpm.module.internal.impl;

import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.job.service.JobServiceConfiguration;

public class BPMStandaloneProcessEngineSchemaConfiguration extends StandaloneProcessEngineConfiguration {

    private static final String CUSTOM_MYBATIS_JOB_MAPPING_FILE = "db/mapping/mappings.xml";

    public BPMStandaloneProcessEngineSchemaConfiguration() {
        mybatisMappingFile = CUSTOM_MYBATIS_JOB_MAPPING_FILE;
    }

    @Override
    public void initSchemaManagementCommand() {
        if (schemaManagementCmd == null && usingRelationalDatabase && databaseSchemaUpdate != null) {
            this.schemaManagementCmd = new BPMSchemaOperationsProcessEngineBuild();
        }
    }

    @Override
    protected JobServiceConfiguration instantiateJobServiceConfiguration() {
        return new BPMTenantAwareJobServiceConfiguration(ScopeTypes.BPMN, this.getAsyncExecutorConfiguration().getTenantId());
    }

}
