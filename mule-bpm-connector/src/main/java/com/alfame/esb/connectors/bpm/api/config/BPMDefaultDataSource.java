package com.alfame.esb.connectors.bpm.api.config;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import com.alfame.esb.connectors.bpm.api.param.BPMDatabaseType;

public class BPMDefaultDataSource extends BPMDataSource {
	
	@Expression( NOT_SUPPORTED )
	@Placement( order = 1)
	@Parameter
	public BPMDatabaseType type;

	public BPMDatabaseType getType() {
		return this.type;
	}

}