package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import javax.sql.DataSource;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

@Alias("data-source-reference")
public class BPMDataSourceReference extends BPMDataSource {

    @Parameter
    @Placement(order = 2)
    @Expression(NOT_SUPPORTED)
    private DataSource dataSourceRef;

    public DataSource getDataSourceRef() {
        return dataSourceRef;
    }

    @Override
    public DataSource getDataSource() {
        return this.getDataSourceRef();
    }

}
