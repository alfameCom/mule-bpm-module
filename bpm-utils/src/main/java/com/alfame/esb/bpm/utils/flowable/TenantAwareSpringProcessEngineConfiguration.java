package com.alfame.esb.bpm.utils.flowable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flowable.engine.ProcessEngine;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.configurator.AutoDeploymentStrategy;

public class TenantAwareSpringProcessEngineConfiguration extends SpringProcessEngineConfiguration {

	private static final Log logger = LogFactory.getLog( TenantAwareSpringProcessEngineConfiguration.class );

	protected String deploymentMode = "tenant-aware-default";
	private TenantInfoHolder tenantInfoHolder;
	
	public TenantInfoHolder getTenantInfoHolder() {
		return tenantInfoHolder;
	}

	public void setTenantInfoHolder(TenantInfoHolder tenantInfoHolder) {
		this.tenantInfoHolder = tenantInfoHolder;
	}

	public TenantAwareSpringProcessEngineConfiguration() {
		super();
		deploymentStrategies.add( new TenantAwareDefaultAutoDeploymentStrategy( this.tenantInfoHolder ) );
	}

	@Override	
	protected void autoDeployResources(ProcessEngine processEngine) {
		logger.info( ">>>>> " + deploymentMode );
        if (deploymentResources != null && deploymentResources.length > 0) {
            final AutoDeploymentStrategy strategy = getAutoDeploymentStrategy(deploymentMode);
			logger.info( ">>>>>" + deploymentName );
            strategy.deployResources(deploymentName, deploymentResources, processEngine.getRepositoryService());
        }
    }
	
}
