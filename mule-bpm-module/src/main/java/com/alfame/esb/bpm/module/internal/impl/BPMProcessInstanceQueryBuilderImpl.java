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

        if (this.processInstanceId != null) {
            query.processInstanceId(this.processInstanceId);
            LOGGER.debug("Adding filter criteria for processInstanceId: {}", this.processInstanceId);
        }

        if (this.uniqueBusinessKeyLike != null) {
            query.processInstanceBusinessKeyLike(this.uniqueBusinessKeyLike);
            LOGGER.debug("Adding filter criteria for uniqueBusinessKeyLike: {}", this.uniqueBusinessKeyLike);
        }

        if (this.tenantId != null) {
            query.processInstanceTenantId(this.tenantId);
            LOGGER.debug("Adding filter criteria for tenantId: {}", this.tenantId);
        }

        return new BPMProcessInstanceQueryImpl(engine, query);
    }

}
