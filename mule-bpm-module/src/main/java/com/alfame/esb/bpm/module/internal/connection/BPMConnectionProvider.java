package com.alfame.esb.bpm.module.internal.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.connectivity.NoConnectivityTest;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mule.runtime.api.connection.ConnectionValidationResult.success;
import static org.slf4j.LoggerFactory.getLogger;

@Alias("task-listener")
public class BPMConnectionProvider implements ConnectionProvider<BPMConnection>, NoConnectivityTest {

    private static final Logger LOGGER = getLogger(BPMConnectionProvider.class);

    private static Map<String, BPMConnection> connectionCache = new ConcurrentHashMap<>();

    @Override
    public BPMConnection connect() throws ConnectionException {
        BPMConnection connection = new BPMConnection(connectionCache);
        LOGGER.debug("Created connection {}", connection);
        return connection;
    }

    @Override
    public void disconnect(BPMConnection bpmConnection) {
        LOGGER.debug("Disconnecting connection {}", bpmConnection);
    }

    @Override
    public ConnectionValidationResult validate(BPMConnection bpmConnection) {
        return success();
    }

}
