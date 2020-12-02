package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.*;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.event.FlowableProcessEngineEvent;
import org.flowable.variable.api.event.FlowableVariableEvent;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMEventSubscriptionBuilderImpl extends BPMEngineEventSubscriptionBuilder implements FlowableEventListener, BPMEventSubscriptionConnection {

    private static final Logger LOGGER = getLogger(BPMEventSubscriptionBuilderImpl.class);

    private final BPMEngine engine;
    private final RuntimeService runtimeService;
    private BPMEngineEventListener engineEventListener;
    private boolean subscribedForEvents = false;
    private BPMEventSubscriptionImpl eventSubscription;

    public BPMEventSubscriptionBuilderImpl(BPMEngine engine, RuntimeService runtimeService) {
        this.engine = engine;
        this.runtimeService = runtimeService;
    }

    @Override
    public void open() {
        subscribeForEvents(null);
    }

    @Override
    public void close() {
        unsubscribeForEvents();
    }

    @Override
    public BPMEngineEventSubscription subscribeForEvents() {
        return subscribeForEvents(null);
    }

    @Override
    public BPMEngineEventSubscription subscribeForEvents(BPMEngineEventListener engineEventListener) {
        if (this.subscribedForEvents != true) {
            this.engineEventListener = engineEventListener;
            this.runtimeService.addEventListener(this);
            this.eventSubscription = new BPMEventSubscriptionImpl(this.engine, this);
            this.subscribedForEvents = true;
        }

        return this.eventSubscription;
    }

    public void unsubscribeForEvents() {
        if (this.subscribedForEvents == true) {
            this.engineEventListener = null;
            this.runtimeService.removeEventListener(this);
            this.subscribedForEvents = false;
        }
    }

    @Override
    public void onEvent(FlowableEvent flowableEvent) {
        if (this.subscribedForEvents == true) {
            BPMEngineEvent engineEvent = null;


            if (flowableEvent instanceof FlowableVariableEvent) {
                FlowableVariableEvent flowableVariableEvent = (FlowableVariableEvent) flowableEvent;
                LOGGER.trace("Received variable event {} for instance {}", flowableVariableEvent.getType(), flowableVariableEvent.getProcessInstanceId());
                if (flowableVariableEvent.getType().equals(FlowableEngineEventType.VARIABLE_CREATED)
                        || flowableVariableEvent.getType().equals(FlowableEngineEventType.VARIABLE_UPDATED)
                        || flowableVariableEvent.getType().equals(FlowableEngineEventType.VARIABLE_DELETED)) {
                    engineEvent = new BPMVariableEventProxy(flowableVariableEvent);
                }
            } else if (flowableEvent instanceof FlowableProcessEngineEvent) {
                FlowableProcessEngineEvent flowableEngineEvent = (FlowableProcessEngineEvent) flowableEvent;
                LOGGER.trace("Received engine event {} for instance {}", flowableEngineEvent.getType(), flowableEngineEvent.getProcessInstanceId());
                if (flowableEngineEvent.getType().equals(FlowableEngineEventType.PROCESS_CREATED)
                        || flowableEngineEvent.getType().equals(FlowableEngineEventType.PROCESS_COMPLETED)) {
                    engineEvent = new BPMEventProxy(flowableEngineEvent);
                }
                if (flowableEngineEvent.getType().equals(FlowableEngineEventType.ACTIVITY_STARTED)
                        || flowableEngineEvent.getType().equals(FlowableEngineEventType.ACTIVITY_COMPLETED)) {
                    engineEvent = new BPMActivityEventProxy(flowableEngineEvent);
                }
            } else if (flowableEvent instanceof FlowableEntityEvent) {
                FlowableEntityEvent flowableEntityEvent = (FlowableEntityEvent) flowableEvent;
                LOGGER.trace("Received entity event {}", flowableEntityEvent.getType());
                if (flowableEntityEvent.getType().equals(FlowableEngineEventType.JOB_EXECUTION_FAILURE)) {
                    engineEvent = new BPMJobEntityEventProxy(flowableEntityEvent);
                }
            } else {
                LOGGER.trace("Received event {} of type {}", flowableEvent.getType(), flowableEvent.getClass().getCanonicalName());
            }

            if (isUnfilteredEvent(engineEvent)) {
                if (this.engineEventListener != null) {
                    this.engineEventListener.handleEngineEvent(engineEvent);
                }

                this.eventSubscription.cacheEvent(engineEvent);
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

}
