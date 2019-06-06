package com.alfame.esb.utils.flowable;

import org.flowable.engine.ProcessEngine;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.configurator.AutoDeploymentStrategy;

public class TenantAwareSpringProcessEngineConfiguration extends SpringProcessEngineConfiguration {

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
	
}
