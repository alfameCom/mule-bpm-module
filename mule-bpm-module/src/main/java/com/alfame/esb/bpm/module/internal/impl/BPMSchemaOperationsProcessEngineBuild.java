package com.alfame.esb.bpm.module.internal.impl;

import org.apache.ibatis.session.SqlSession;
import org.flowable.common.engine.api.FlowableWrongDbException;
import org.flowable.common.engine.impl.db.DbSqlSession;
import org.flowable.common.engine.impl.db.SchemaManager;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.SchemaOperationsProcessEngineBuild;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.flywaydb.core.internal.jdbc.JdbcUtils.closeConnection;

public class BPMSchemaOperationsProcessEngineBuild extends SchemaOperationsProcessEngineBuild {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMSchemaOperationsProcessEngineBuild.class);

    @Override
    public Void execute(CommandContext commandContext) {
        Void returnValue = null;

        String databaseSchemaUpdate = CommandContextUtil.getProcessEngineConfiguration().getDatabaseSchemaUpdate();

        if (!ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE.equals(databaseSchemaUpdate)) {
            SchemaManager schemaManager = CommandContextUtil.getProcessEngineConfiguration(commandContext).getSchemaManager();
            DataSource dataSource = CommandContextUtil.getProcessEngineConfiguration().getDataSource();

            if (dataSource != null) {
                Connection connection = null;
                try {
                    connection = dataSource.getConnection();
                    if (connection != null && connection.isValid(5)) {
                        String schema = connection.getSchema();
                        LOGGER.info("managing database schema {} on {}", schema, connection.getMetaData().getURL());
                        Flyway flyway = Flyway.configure()
                                .locations("db/mule-bpm-flowable/migrations")
                                .dataSource(dataSource)
                                .schemas(schema)
                                .load();

                        if (flyway.info().current() == null) {
                            LOGGER.info("database not created or baselined");
                            returnValue = super.execute(commandContext);
                            // Ensure database session has been committed prior to Flyaway operations
                            commitDbSqlContext(commandContext);
                            flyway.baseline();
                        } else {
                            try {
                                schemaManager.schemaCheckVersion();
                            } catch (FlowableWrongDbException flowableWrongDbException) {
                                LOGGER.info("flowable database needs an update: {}", flowableWrongDbException.getMessage());
                                returnValue = super.execute(commandContext);
                                // Ensure database session has been committed prior to Flyaway operations
                                commitDbSqlContext(commandContext);
                                flyway.baseline();
                            } finally {
                            }
                        }

                        flyway.migrate();
                    } else {
                        LOGGER.error("cannot establish database connection");
                    }
                } catch (Exception exception) {
                    LOGGER.error("error on Flyway migration", exception);
                } finally {
                    closeConnection(connection);
                }
            } else {
                LOGGER.warn("no data source defined");
            }
        }

        return returnValue;
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException sqlException) {
                LOGGER.error("error while closing database connection", sqlException);
            }
        }
    }

    private void createConnection() {
    }

    protected void commitDbSqlContext(CommandContext commandContext) throws SQLException {
        DbSqlSession dbSqlSession = commandContext.getSession(DbSqlSession.class);
        if (dbSqlSession != null) {
            SqlSession sqlSession = dbSqlSession.getSqlSession();
            if (sqlSession != null) {
                Connection sqlConnection = sqlSession.getConnection();
                if (sqlConnection != null) {
                    sqlConnection.commit();
                }
            }
        }
    }
}
