package com.alfame.esb.bpm.connector.api.config;

import com.zaxxer.hikari.HikariDataSource;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import javax.sql.DataSource;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

@Alias("generic-data-source")
public class BPMGenericDataSource extends BPMDataSource {

    @Parameter
    @Placement(order = 2)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "org.h2.Driver")
    private String driverClassName;

    @Parameter
    @Placement(order = 3)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
    private String jdbcUrl;

    @Parameter
    @Placement(order = 4)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "sa")
    private String username;

    @Parameter
    @Placement(order = 5)
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "sa")
    @Password
    private String password;

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public DataSource getDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(this.getDriverClassName());
        dataSource.setJdbcUrl(this.getJdbcUrl());
        dataSource.setUsername(this.getUsername());
        dataSource.setPassword(this.getPassword());
        return dataSource;
    }

}
