package com.alfame.esb.bpm.connector.api.config;

import com.alfame.esb.bpm.connector.api.param.BPMDatabaseType;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.Extensible;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import javax.sql.DataSource;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

@Extensible
public abstract class BPMDataSource {

    @Expression(NOT_SUPPORTED)
    @Placement(order = 1)
    @Parameter
    public BPMDatabaseType type;

    public BPMDatabaseType getType() {
        return this.type;
    }

    public abstract DataSource getDataSource();

}