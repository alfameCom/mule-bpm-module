package com.alfame.esb.connectors.bpm.internal.connection;

import com.alfame.esb.bpm.activity.queue.api.BPMActivityResponseCallback;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMConnection {

	private static final Logger LOGGER = getLogger( BPMConnection.class );

	private BPMActivityResponseCallback responseCallback;

	public BPMConnection() {}

	public BPMActivityResponseCallback getResponseCallback() {
		return responseCallback;
	}

	public void setResponseCallback( BPMActivityResponseCallback responseCallback ) {
		this.responseCallback = responseCallback;
	}
}
