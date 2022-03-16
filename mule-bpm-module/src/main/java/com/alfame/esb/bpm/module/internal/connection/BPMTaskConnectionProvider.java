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
public class BPMTaskConnectionProvider implements ConnectionProvider<BPMTaskConnection>, NoConnectivityTest {

    private static final Logger LOGGER = getLogger(BPMTaskConnectionProvider.class);

    private static Map<String, BPMTaskConnection> connectionCache = new ConcurrentHashMap<>();

    @Override
    public BPMTaskConnection connect() throws ConnectionException {
        BPMTaskConnection connection = new BPMTaskConnection(connectionCache);
        LOGGER.debug("BPMTaskConnectionProvider created connection {}", connection);
        return connection;
    }

    @Override
    public void disconnect(BPMTaskConnection bpmConnection) {
        LOGGER.debug("BPMTaskConnectionProvider disconnecting connection {}", bpmConnection);
    }

    @Override
    public ConnectionValidationResult validate(BPMTaskConnection bpmConnection) {
        return success();
    }

}
