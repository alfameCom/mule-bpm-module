package com.alfame.esb.bpm.utils.flowable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flowable.engine.ProcessEngine;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.configurator.AutoDeploymentStrategy;
import org.springframework.core.io.Resource;

public class TenantAwareSpringProcessEngineConfiguration extends SpringProcessEngineConfiguration {

	private static final Log logger = LogFactory.getLog( TenantAwareSpringProcessEngineConfiguration.class );

	protected String deploymentMode = "tenant-aware-default";

	public TenantAwareSpringProcessEngineConfiguration( TenantInfoHolder tenantInfoHolder ) {
		super();
		deploymentStrategies.add( new TenantAwareDefaultAutoDeploymentStrategy( tenantInfoHolder ) );
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
