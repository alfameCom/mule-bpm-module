package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMEngine;
import com.alfame.esb.bpm.api.BPMProcessInstanceQuery;
import com.alfame.esb.bpm.api.BPMProcessInstanceQueryBuilder;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessInstanceQueryBuilderImpl extends BPMProcessInstanceQueryBuilder {

    private static final Logger LOGGER = getLogger(BPMProcessInstanceQueryBuilderImpl.class);

    private final BPMEngine engine;
    private final HistoryService historyService;

    public BPMProcessInstanceQueryBuilderImpl(BPMEngine engine, HistoryService historyService) {
        this.engine = engine;
        this.historyService = historyService;
    }

    @Override
    public BPMProcessInstanceQuery buildProcessInstanceQuery() {
        HistoricProcessInstanceQuery query = this.historyService.createHistoricProcessInstanceQuery();
        return new BPMProcessInstanceQueryImpl(engine, query);
    }

}
