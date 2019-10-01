package com.alfame.esb.connectors.bpm.api.config;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias( "tenant" )
public class BPMTenant {
	
	@Parameter
	@Expression( NOT_SUPPORTED )
	private String tenantId;

	@Parameter
	@Optional
	@Expression( NOT_SUPPORTED )
	@Alias( "data-source" )
	private BPMDataSource dataSource;

	@Parameter
	@Optional
	@Expression( NOT_SUPPORTED )
	@Alias( "async-executor" )
	private BPMAsyncExecutor processEngineAsyncExecutor;
	
	public String getTenantId() {
		return tenantId;
	}
	
	public BPMDataSource getDataSource() {
		return this.dataSource;
	}

	public BPMAsyncExecutor getProcessEngineAsyncExecutor() {
		return processEngineAsyncExecutor;
	}

	public void setProcessEngineAsyncExecutor(BPMAsyncExecutor processEngineAsyncExecutor) {
		this.processEngineAsyncExecutor = processEngineAsyncExecutor;
	}

}
