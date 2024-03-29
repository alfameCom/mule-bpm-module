package com.alfame.esb.bpm.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BPMEnginePool {

    private BPMEnginePool() {}

    private static final Log logger = LogFactory.getLog(BPMEnginePool.class);

    private static Map<String, BPMEngine> engines = new ConcurrentHashMap<>();

    public static void registerInstance(String configName, BPMEngine engine) {
        logger.debug("Adding to pool " + configName);
        engines.put(configName, engine);
    }

    public static void unregisterInstance(String configName) {
        logger.debug("Removing from pool " + configName);
        engines.remove(configName);
    }

    public static BPMEngine getInstance(String configName) {
        logger.debug("Returning from pool " + configName);
        return engines.get(configName);
    }

}
