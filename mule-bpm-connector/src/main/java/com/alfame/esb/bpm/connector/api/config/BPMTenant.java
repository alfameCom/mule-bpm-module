package com.alfame.esb.bpm.connector.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.List;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

@Alias("tenant")
public class BPMTenant {

    @Parameter
    @Expression(NOT_SUPPORTED)
    private String tenantId;

    @Parameter
    @Expression(NOT_SUPPORTED)
    @Optional
    private BPMDataSource dataSource;

    @Parameter
    @Optional
    @Expression(NOT_SUPPORTED)
    @Alias("definitions")
    private List<BPMDefinition> definitions;

    public String getTenantId() {
        return tenantId;
    }

    public BPMDataSource getDataSource() {
        return this.dataSource;
    }

    public List<BPMDefinition> getDefinitions() {
        return this.definitions;
    }

}
