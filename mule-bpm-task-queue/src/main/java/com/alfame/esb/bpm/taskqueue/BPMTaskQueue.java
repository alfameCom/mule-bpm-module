package com.alfame.esb.bpm.taskqueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class BPMTaskQueue {

    private static final Log logger = LogFactory.getLog(BPMTaskQueue.class);

    private String queueName;
    private BlockingQueue<BPMTask> taskQueue = new LinkedTransferQueue<>();

    public BPMTaskQueue(String queueName) {
        this.queueName = queueName;
    }

    public boolean publish(BPMTask task) {
        logger.debug("Pushing to queue " + this.queueName);
        return taskQueue.offer(task);
    }

    public BPMTask pop(long timeout, TimeUnit unit) throws InterruptedException {
        logger.debug("Popping from queue " + this.queueName);
        return taskQueue.poll(timeout, unit);
    }

    public String getQueueName() {
        return this.queueName;
    }

}
