package com.alfame.esb.bpm.taskqueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class BPMTaskQueueFactory {

    private static final Log logger = LogFactory.getLog(BPMTaskQueueFactory.class);

    private static Map<String, BPMTaskQueue> instances = new ConcurrentHashMap<>();
    private static Semaphore semaphore = new Semaphore(1);

    public static BPMTaskQueue getInstance(String queueName) {
        BPMTaskQueue taskQueue = null;

        try {
            semaphore.acquire();

            if (instances.get(queueName) == null) {
                instances.put(queueName, new BPMTaskQueue(queueName));
            }

            taskQueue = instances.get(queueName);

            semaphore.release();
        } catch (InterruptedException e) {
            logger.info(e);
        }

        logger.trace("Returning instance for queueName " + queueName);
        return taskQueue;
    }

}
