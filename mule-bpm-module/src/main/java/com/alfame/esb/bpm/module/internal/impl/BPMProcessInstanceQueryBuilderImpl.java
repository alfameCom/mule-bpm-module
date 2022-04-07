package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMEngine;
import com.alfame.esb.bpm.api.BPMProcessInstanceQuery;
import com.alfame.esb.bpm.api.BPMProcessInstanceQueryBuilder;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.slf4j.Logger;

import java.util.Map;

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

        if (this.processDefinitionKey != null) {
            query.processDefinitionKey(this.processDefinitionKey);
            LOGGER.debug("Adding filter criteria for processDefinitionKey: {}", this.processDefinitionKey);
        }

        if (this.uniqueBusinessKeyLike != null) {
            query.processInstanceBusinessKeyLike(this.uniqueBusinessKeyLike);
            LOGGER.debug("Adding filter criteria for uniqueBusinessKeyLike: {}", this.uniqueBusinessKeyLike);
        }

        if (this.processInstanceNameLike != null) {
            query.processInstanceNameLike(this.processInstanceNameLike);
            LOGGER.debug("Adding filter criteria for processInstanceNameLike: {}", this.processInstanceNameLike);
        }

        if (this.tenantId != null) {
            query.processInstanceTenantId(this.tenantId);
            LOGGER.debug("Adding filter criteria for tenantId: {}", this.tenantId);
        }

        if (this.startedAfter != null) {
            query.startedAfter(this.startedAfter);
            LOGGER.debug("Adding filter criteria for startedAfter: {}", this.startedAfter);
        }

        if (this.startedBefore != null) {
            query.startedBefore(this.startedBefore);
            LOGGER.debug("Adding filter criteria for startedBefore: {}", this.startedBefore);
        }

        if (this.finishedAfter != null) {
            query.finishedAfter(this.finishedAfter);
            LOGGER.debug("Adding filter criteria for finishedAfter: {}", this.finishedAfter);
        }

        if (this.finishedBefore != null) {
            query.finishedBefore(this.finishedBefore);
            LOGGER.debug("Adding filter criteria for finishedBefore: {}", this.finishedBefore);
        }

        if (this.variablesLike != null) {
            for (Map.Entry<String, String> variableLike : this.variablesLike.entrySet()) {
                if (variableLike.getValue() != null) {
                    query.variableValueLike(variableLike.getKey(), variableLike.getValue());
                    LOGGER.debug("Adding filter criteria for variable {} with value like: {}",
                            variableLike.getKey(), variableLike.getValue());
                } else {
                    query.variableExists(variableLike.getKey());
                    LOGGER.debug("Adding filter criteria for variable {}", variableLike.getKey());
                }
            }
        }

        if (this.onlyFinished) {
            query.finished();
            LOGGER.debug("Adding filter criteria for finished instances only");
        }

        if (this.onlyUnfinished) {
            query.unfinished();
            LOGGER.debug("Adding filter criteria for unfinished instances only");
        }

        if (this.includeProcessVariables) {
            query.includeProcessVariables();
            LOGGER.debug("Including process variables");
        }

        return new BPMProcessInstanceQueryImpl(engine, query);
    }

}
