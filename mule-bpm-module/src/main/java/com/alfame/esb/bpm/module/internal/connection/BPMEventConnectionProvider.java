package com.alfame.esb.bpm.module.internal.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.connectivity.NoConnectivityTest;
import org.slf4j.Logger;

import static org.mule.runtime.api.connection.ConnectionValidationResult.success;
import static org.slf4j.LoggerFactory.getLogger;

@Alias("event-listener")
public class BPMEventConnectionProvider implements ConnectionProvider<BPMEventConnection>, NoConnectivityTest {

    private static final Logger LOGGER = getLogger(BPMEventConnectionProvider.class);

    @Override
    public BPMEventConnection connect() throws ConnectionException {
        BPMEventConnection connection = new BPMEventConnection();
        LOGGER.debug("BPMConnectionProvider created connection {}", connection);
        return connection;
    }

    @Override
    public void disconnect(BPMEventConnection bpmConnection) {
        LOGGER.debug("BPMConnectionProvider disconnecting connection {}", bpmConnection);
    }

    @Override
    public ConnectionValidationResult validate(BPMEventConnection bpmConnection) {
        return success();
    }

}
