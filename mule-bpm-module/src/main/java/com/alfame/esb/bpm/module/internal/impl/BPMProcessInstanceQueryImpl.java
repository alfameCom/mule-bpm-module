package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMEngine;
import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.api.BPMProcessInstanceQuery;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessInstanceQueryImpl implements BPMProcessInstanceQuery {

    private static final Logger LOGGER = getLogger(BPMProcessInstanceQueryImpl.class);

    private final BPMEngine engine;
    private final HistoricProcessInstanceQuery query;

    public BPMProcessInstanceQueryImpl(BPMEngine engine, HistoricProcessInstanceQuery query) {
        this.engine = engine;
        this.query = query;
    }


    @Override
    public List<BPMProcessInstance> instances(int firstResult, int maxResults) {
        List<BPMProcessInstance> instanceProxies = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        List<HistoricProcessInstance> instances = query.listPage(firstResult, maxResults);

        if (instances != null) {
            for (HistoricProcessInstance instance : instances) {
                BPMHistoricProcessInstanceProxy instanceProxy = new BPMHistoricProcessInstanceProxy(instance);
                instanceProxies.add(instanceProxy);
                LOGGER.debug("Found process instance: {}", instanceProxy);
            }
            LOGGER.info("Found {} process instances in {} ms", instances.size(), System.currentTimeMillis() - startTime);
        } else {
            LOGGER.info("Found {} process instances in {} ms", 0, System.currentTimeMillis() - startTime);
        }

        return instanceProxies;
    }

    @Override
    public BPMProcessInstance uniqueInstance() {
        BPMHistoricProcessInstanceProxy instanceProxy = null;

        long startTime = System.currentTimeMillis();
        HistoricProcessInstance instance = query.singleResult();

        if (instance != null) {
            instanceProxy =  new BPMHistoricProcessInstanceProxy(instance);
            LOGGER.debug("Found unique process instance in {} ms: {}", System.currentTimeMillis() - startTime, instanceProxy);
        }
        return instanceProxy;
    }
}
