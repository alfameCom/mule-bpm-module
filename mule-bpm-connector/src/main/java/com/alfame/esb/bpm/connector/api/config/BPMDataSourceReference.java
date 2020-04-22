package com.alfame.esb.bpm.connector.api.config;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import javax.sql.DataSource;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

@Alias( "data-source-reference" )
public class BPMDataSourceReference extends BPMDataSource {
	
	@Parameter
	@Placement ( order = 2 )
	@Expression( NOT_SUPPORTED )
	private DataSource dataSourceRef;

	public DataSource getDataSourceRef() {
		return dataSourceRef;
	}

	@Override
	public DataSource getDataSource() {
		return this.getDataSourceRef();
	}
	
}
