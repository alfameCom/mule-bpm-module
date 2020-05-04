package com.alfame.esb.bpm.connector.internal;

import com.alfame.esb.bpm.api.*;
import com.alfame.esb.bpm.connector.internal.proxies.BPMProcessEngineEventProxy;
import com.alfame.esb.bpm.connector.internal.proxies.BPMProcessVariableEventProxy;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.event.FlowableProcessEngineEvent;
import org.flowable.variable.api.event.FlowableVariableEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMEngineEventSubscriptionBuilderImpl extends BPMEngineEventSubscriptionBuilder implements BPMEngineEventSubscription, FlowableEventListener {

    private static final Logger LOGGER = getLogger(BPMEngineEventSubscriptionBuilderImpl.class);

    private final BPMEngine engine;
    private final RuntimeService runtimeService;
    private CountDownLatch countDownLatch;
    private BPMEngineEventListener engineEventListener;
    private boolean subscribedForEvents = false;
    private List<BPMEngineEvent> cachedEvents = null;
    private ReentrantLock cacheLock = new ReentrantLock();

    public BPMEngineEventSubscriptionBuilderImpl(BPMEngine engine, RuntimeService runtimeService) {
        this.engine = engine;
        this.runtimeService = runtimeService;
    }

    @Override
    public BPMEngineEventSubscription subscribeForEvents() {
        try {
            this.cacheLock.lock();
            this.cachedEvents = new ArrayList<>();
            subscribeForEvents(null);
        } finally {
            this.cacheLock.unlock();
        }

        return this;
    }

    @Override
    public BPMEngineEventSubscription subscribeForEvents(BPMEngineEventListener engineEventListener) {
        if (this.subscribedForEvents != true) {
            this.engineEventListener = engineEventListener;
            this.runtimeService.addEventListener(this);
            this.subscribedForEvents = true;
        }

        return this;
    }

    @Override
    public void unsubscribeForEvents() {
        if (this.subscribedForEvents == true) {
            this.engineEventListener = null;
            this.runtimeService.removeEventListener(this);
            this.subscribedForEvents = false;
        }
    }

    @Override
    public List<BPMEngineEvent> waitAndUnsubscribeForEvents(int numberOfEvents, long timeout, TimeUnit timeUnit) throws InterruptedException {
        List<BPMEngineEvent> events = null;

        try {
            subscribeForEvents(null);

            try {
                this.cacheLock.lock();
                this.countDownLatch = new CountDownLatch(Math.max(numberOfEvents - this.cachedEvents.size(), 0));
            } finally {
                this.cacheLock.unlock();
            }

            LOGGER.debug("Awaiting {} events", countDownLatch.getCount());
            this.countDownLatch.await(timeout, timeUnit);
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
    public void onEvent(FlowableEvent flowableEvent) {
        if (this.subscribedForEvents == true) {
            BPMEngineEvent engineEvent = null;

            if (flowableEvent instanceof FlowableVariableEvent) {
                FlowableVariableEvent flowableVariableEvent = (FlowableVariableEvent) flowableEvent;
                if (flowableVariableEvent.getType().equals(FlowableEngineEventType.VARIABLE_CREATED)
                        || flowableVariableEvent.getType().equals(FlowableEngineEventType.VARIABLE_UPDATED)
                        || flowableVariableEvent.getType().equals(FlowableEngineEventType.VARIABLE_DELETED)) {
                    engineEvent = new BPMProcessVariableEventProxy(flowableVariableEvent);
                }
            } else if (flowableEvent instanceof FlowableProcessEngineEvent) {
                FlowableProcessEngineEvent flowableEngineEntityEvent = (FlowableProcessEngineEvent) flowableEvent;
                if (flowableEngineEntityEvent.getType().equals(FlowableEngineEventType.PROCESS_CREATED)
                        || flowableEngineEntityEvent.getType().equals(FlowableEngineEventType.PROCESS_COMPLETED)) {
                    engineEvent = new BPMProcessEngineEventProxy(flowableEngineEntityEvent);
                }
            }

            if(isSubscribedEvent(engineEvent)) {
                if (this.engineEventListener != null) {
                    this.engineEventListener.handleEngineEvent(engineEvent);
                }

                try {
                    this.cacheLock.lock();
                    if(this.cachedEvents != null) {
                        this.cachedEvents.add(engineEvent);
                        LOGGER.trace("Added event {} for process instance {}", engineEvent.getType(), engineEvent.getProcessInstanceId());

                        if (this.countDownLatch != null) {
                            this.countDownLatch.countDown();
                            LOGGER.debug("Received awaited event {}", engineEvent.getType());
                        }
                    }
                } finally {
                    this.cacheLock.unlock();
                }
            }
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }

    protected boolean isSubscribedEvent(BPMEngineEvent engineEvent) {
        boolean isSubscribedEvent = true;

        if (engineEvent == null) {
            isSubscribedEvent = false;
        } else {
            if (this.eventTypes != null
                    && !this.eventTypes.isEmpty()
                    && engineEvent.getType() != null
                    && !this.eventTypes.contains(engineEvent.getType())) {
                isSubscribedEvent = false;
            } else if (this.processDefinitionKeys != null
                    && !this.processDefinitionKeys.isEmpty()
                    && engineEvent.getProcessDefinitionKey() != null
                    && !this.processDefinitionKeys.contains(engineEvent.getProcessDefinitionKey())) {
                isSubscribedEvent = false;
            } else if (this.processInstanceIds != null
                    && !this.processInstanceIds.isEmpty()
                    && engineEvent.getProcessInstanceId() != null
                    && !this.processInstanceIds.contains(engineEvent.getProcessInstanceId())) {
                isSubscribedEvent = false;
            } else if (this.variableValues != null
                    && !this.variableValues.isEmpty()
                    && engineEvent.getVariableName() != null
                    && !this.variableValues.containsKey(engineEvent.getVariableName())) {
                isSubscribedEvent = false;
            } else if (this.variableValues != null
                    && !this.variableValues.isEmpty()
                    && engineEvent.getVariableName() != null
                    && this.variableValues.containsKey(engineEvent.getVariableName())
                    && this.variableValues.get(engineEvent.getVariableName()) != null) {
                if (!this.variableValues.get(engineEvent.getVariableName()).equals(engineEvent.getVariableValue())) {
                    isSubscribedEvent = false;
                }
            }
        }

        return isSubscribedEvent;
    }

    public BPMEngineEvent uniqueEventByEventType(BPMEngineEventType eventType) {
        BPMEngineEvent uniqueEvent = null;

        if (this.cachedEvents != null) {
            for (BPMEngineEvent event : this.cachedEvents) {
                if (event.getType().equals(eventType)) {
                    if (uniqueEvent != null) {
                        throw new java.lang.IndexOutOfBoundsException("Multiple events found with event type " + eventType.getValue());
                    }
                    uniqueEvent = event;
                }
            }
        }

        return uniqueEvent;
    }
}
