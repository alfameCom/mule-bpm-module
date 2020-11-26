package com.alfame.esb.bpm.module.internal.impl;

import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

public class BPMStandaloneProcessEngineSchemaConfiguration extends StandaloneProcessEngineConfiguration {

    @Override
    public void initSchemaManagementCommand() {
        if (schemaManagementCmd == null) {
            if (usingRelationalDatabase && databaseSchemaUpdate != null) {
                this.schemaManagementCmd = new BPMSchemaOperationsProcessEngineBuild();
            }
        }
    }

}
