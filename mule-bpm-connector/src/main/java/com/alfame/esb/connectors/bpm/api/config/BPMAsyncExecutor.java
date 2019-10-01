package com.alfame.esb.connectors.bpm.api.config;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.ClassValue;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import com.alfame.esb.connectors.bpm.api.param.BPMDatabaseType;

@Alias( "async-executor" )
public class BPMAsyncExecutor {

	@Parameter
	@Placement( order = 1 )
	@Expression( NOT_SUPPORTED )
	@Optional( defaultValue = "5" )
	private int maxThreads;

	@Parameter
	@Placement( order = 2 )
	@Expression( NOT_SUPPORTED )
	@Optional( defaultValue = "org.flowable.job.service.impl.asyncexecutor.multitenant.ExecutorPerTenantAsyncExecutor" )
	private String className;

	public int getType() {
		return maxThreads;
	}

	public String getclassName() {
		return className;
	}

}
