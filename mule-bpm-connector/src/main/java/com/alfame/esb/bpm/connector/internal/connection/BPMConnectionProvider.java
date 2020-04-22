package com.alfame.esb.bpm.connector.internal.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.connectivity.NoConnectivityTest;

import static org.mule.runtime.api.connection.ConnectionValidationResult.success;

@Alias( "task-listener" )
public class BPMConnectionProvider implements ConnectionProvider< BPMConnection >, NoConnectivityTest {

	@Override
	public BPMConnection connect() throws ConnectionException {
		return new BPMConnection();
	}

	@Override
	public void disconnect( BPMConnection bpmConnection ) {

	}

	@Override
	public ConnectionValidationResult validate( BPMConnection bpmConnection ) {
		return success();
	}

}
