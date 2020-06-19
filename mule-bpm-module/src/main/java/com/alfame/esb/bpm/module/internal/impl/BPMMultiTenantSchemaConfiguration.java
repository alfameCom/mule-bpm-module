package com.alfame.esb.bpm.module.internal.impl;

import org.flowable.common.engine.api.FlowableWrongDbException;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.cfg.multitenant.TenantAwareDataSource;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.multitenant.ExecuteSchemaOperationCommand;
import org.flowable.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMMultiTenantSchemaConfiguration extends MultiSchemaMultiTenantProcessEngineConfiguration {

    private static final Logger LOGGER = getLogger(BPMMultiTenantSchemaConfiguration.class);

    public BPMMultiTenantSchemaConfiguration(TenantInfoHolder tenantInfoHolder) {
        super(tenantInfoHolder);
    }

    @Override
    protected void createTenantSchema(String tenantId) {
        TenantAwareDataSource dataSource = ((TenantAwareDataSource) super.getDataSource());
        if (dataSource != null) {
            DataSource tenantDataSource = dataSource.getDataSources().get(tenantId);
            if (tenantDataSource != null) {
                Connection connection = null;
                try {
                    connection = tenantDataSource.getConnection();
                    if (connection != null && connection.isValid(5)) {
                        String schema = connection.getSchema();
                        LOGGER.info("managing database schema {} on {}", schema, connection.getMetaData().getURL());
                        Flyway flyway = Flyway.configure()
                                .locations("db/mule-bpm-flowable/migrations")
                                .dataSource(tenantDataSource)
                                .schemas(schema)
                                .load();

                        if (flyway.info().current() == null) {
                            LOGGER.info("database not created or baselined");
                            super.createTenantSchema(tenantId);
                            flyway.baseline();
                        } else {
                            try {
                                tenantInfoHolder.setCurrentTenantId(tenantId);
                                getCommandExecutor().execute(getSchemaCommandConfig(),
                                        new ExecuteSchemaOperationCommand(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_FALSE));
                            } catch (FlowableWrongDbException flowableWrongDbException) {
                                LOGGER.info("flowable database needs an update: {}", flowableWrongDbException.getMessage());
                                super.createTenantSchema(tenantId);
                                flyway.baseline();
                            } finally {
                                tenantInfoHolder.clearCurrentTenantId();
                            }
                        }

                        flyway.migrate();
                    } else {
                        LOGGER.error("cannot establish database connection for tenant {}", tenantId);
                    }
                } catch (Exception exception) {
                    LOGGER.error("error on Flyway migration", exception);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException sqlException) {
                            LOGGER.error("error while closing database connection", sqlException);
                        }
                    }
                }
            } else {
                LOGGER.warn("no data source defined for tenant {}", tenantId);
            }
        } else {
            LOGGER.error("no data source defined for process engine");
        }
    }
}
