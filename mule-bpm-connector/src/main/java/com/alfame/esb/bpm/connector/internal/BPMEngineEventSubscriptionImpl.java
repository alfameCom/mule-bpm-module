package com.alfame.esb.bpm.connector.internal;

import com.alfame.esb.bpm.api.BPMEngine;
import com.alfame.esb.bpm.api.BPMEngineEvent;
import com.alfame.esb.bpm.api.BPMEngineEventFinder;
import com.alfame.esb.bpm.api.BPMEngineEventSubscription;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMEngineEventSubscriptionImpl implements BPMEngineEventSubscription {

    private static final Logger LOGGER = getLogger(BPMEngineEventSubscriptionImpl.class);

    private final BPMEngine engine;
    private final BPMEngineEventSubscriptionConnection connection;
    private CountDownLatch countDownLatch;
    private List<BPMEngineEvent> cachedEvents = new ArrayList<>();
    private ReentrantLock cacheLock = new ReentrantLock();

    public BPMEngineEventSubscriptionImpl(BPMEngine engine, BPMEngineEventSubscriptionConnection connection) {
        this.engine = engine;
        this.connection = connection;
    }

    @Override
    public void unsubscribeForEvents() {
        this.connection.close();
    }

    @Override
    public List<BPMEngineEvent> waitAndUnsubscribeForEvents(int numberOfEvents, long timeout, TimeUnit timeUnit) throws InterruptedException {
        List<BPMEngineEvent> events = null;

        try {
            this.connection.open();

            try {
                this.cacheLock.lock();
                this.countDownLatch = new CountDownLatch(Math.max(numberOfEvents - this.cachedEvents.size(), 0));
            } finally {
                this.cacheLock.unlock();
            }

            LOGGER.debug("Awaiting {} events", countDownLatch.getCount());
            if (this.countDownLatch.await(timeout, timeUnit) != true) {
                LOGGER.warn("Waiting of {} events timed out, after receiving {} events in {} ms",
                        numberOfEvents, numberOfEvents - countDownLatch.getCount(), TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
            }
        } catch (InterruptedException exception) {
            LOGGER.warn("Waiting of {} events was interrupted, after receiving {} events in {} ms",
                    numberOfEvents, numberOfEvents - countDownLatch.getCount(), TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
        } finally {
            unsubscribeForEvents();

            try {
                this.cacheLock.lock();
                events = this.cachedEvents;
            } finally {
                this.cacheLock.unlock();
            }
        }

        return events;
    }

    @Override
    public BPMEngineEventFinder eventFinder() {
        return new BPMEngineEventSubscriptionEventFinder(this.cachedEvents);
    }

    void cacheEvent(BPMEngineEvent engineEvent) {
        try {
            this.cacheLock.lock();
            if (this.cachedEvents != null) {
                this.cachedEvents.add(engineEvent);
                LOGGER.trace("Added event {} for process instance {}", engineEvent.getEventType(), engineEvent.getProcessInstanceId());

                if (this.countDownLatch != null) {
                    this.countDownLatch.countDown();
                    LOGGER.debug("Received awaited event {} for process instance {}", engineEvent.getEventType(), engineEvent.getProcessInstanceId());
                }
            }
        } finally {
            this.cacheLock.unlock();
        }
    }

}
