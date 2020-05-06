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

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMEngineEventSubscriptionBuilderImpl extends BPMEngineEventSubscriptionBuilder implements FlowableEventListener, BPMEngineEventSubscriptionConnection {

    private static final Logger LOGGER = getLogger(BPMEngineEventSubscriptionBuilderImpl.class);

    private final BPMEngine engine;
    private final RuntimeService runtimeService;
    private BPMEngineEventListener engineEventListener;
    private boolean subscribedForEvents = false;
    private BPMEngineEventSubscriptionImpl eventSubscription;

    public BPMEngineEventSubscriptionBuilderImpl(BPMEngine engine, RuntimeService runtimeService) {
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
            this.eventSubscription = new BPMEngineEventSubscriptionImpl(this.engine, this);
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

                this.eventSubscription.cachedEvent(engineEvent);
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
            // Filter events, if filter of any single type has been set, and any of those are not matching the event
            if (!isSubscribedForEvent((List) this.eventTypes, engineEvent.getType())) {
                isSubscribedEvent = false;
            } else if (!isSubscribedForEvent((List) this.processDefinitionKeys, engineEvent.getProcessDefinitionKey())) {
                isSubscribedEvent = false;
            } else if (!isSubscribedForEvent((List) this.processInstanceIds, engineEvent.getProcessInstanceId())) {
                isSubscribedEvent = false;
            } else if (!isSubscribedForEvent((Map) this.variableValues, engineEvent.getVariableName(), engineEvent.getVariableValue())) {
                isSubscribedEvent = false;
            }
        }

        return isSubscribedEvent;
    }

    protected boolean isSubscribedForEvent(List<Object> subscriptionKeys, Object eventKey) {
        boolean isSubscribed = true;

        // Some filters of this type have been set?
        if (subscriptionKeys != null
                && !subscriptionKeys.isEmpty()
                && eventKey != null) {
            // Filter non-matching events
            isSubscribed = subscriptionKeys.contains(eventKey);
        }

        return isSubscribed;
    }

    protected boolean isSubscribedForEvent(Map<Object, Object> subscriptionMap, Object eventKey, Object eventValue) {
        boolean isSubscribed = true;

        // Some filters of this type have been set?
        if (subscriptionMap != null
                && !subscriptionMap.isEmpty()
                && eventKey != null) {
            // Filter non-matching events
            if (subscriptionMap.containsKey(eventKey)) {
                // Filter events with non-matching values
                if (subscriptionMap.get(eventKey) != null) {
                    isSubscribed = subscriptionMap.get(eventKey).equals(eventValue);
                }
            } else {
                isSubscribed = false;
            }
        }

        return isSubscribed;
    }

}
