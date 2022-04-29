package com.alfame.esb.bpm.module.internal.operations;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.api.BPMProcessInstanceQuery;
import com.alfame.esb.bpm.api.BPMProcessInstanceQueryBuilder;
import com.alfame.esb.bpm.module.api.config.*;
import com.alfame.esb.bpm.module.internal.BPMExtension;
import com.alfame.esb.bpm.module.internal.connection.BPMConnection;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;

import java.sql.Timestamp;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessInstanceOperations {
    private static final Logger LOGGER = getLogger(BPMProcessInstanceOperations.class);

    @Alias("delete-process-instance")
    public void deleteProcessInstance(
            @Config BPMExtension config,
            @Connection BPMConnection connection,
            @DisplayName("Process instance Id") String processInstanceId,
            @Optional @DisplayName("Delete reason") String deleteReason) {
        
        config.deleteProcessInstance(processInstanceId, deleteReason);

        LOGGER.debug("Trying to delete process instance id {} with reason {}", processInstanceId, deleteReason);
    }

    @Alias("process-instance-query-builder")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMProcessInstanceQueryOutputMetadataResolver.class)
    public BPMProcessInstanceQuery buildProcessInstanceQuery(
            @Config BPMExtension engine,
            @Connection BPMConnection connection,
            @Optional @Alias("process-instance-filters") List<BPMProcessInstanceFilter> processInstanceFilters) {

        LOGGER.debug("Creating query for process instances");
        BPMProcessInstanceQueryBuilder processInstanceQueryBuilder = engine.processInstanceQueryBuilder();

        if (processInstanceFilters != null && !processInstanceFilters.isEmpty()) {
            for (BPMProcessInstanceFilter processInstanceFilter : processInstanceFilters) {
                if (processInstanceFilter instanceof BPMProcessInstanceIdFilter) {
                    BPMProcessInstanceIdFilter idFilter = (BPMProcessInstanceIdFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances id: {}", idFilter.getProcessInstanceId());
                    processInstanceQueryBuilder.processInstanceId(idFilter.getProcessInstanceId());
                } else if (processInstanceFilter instanceof BPMProcessInstanceBusinessKeyLikeFilter) {
                    BPMProcessInstanceBusinessKeyLikeFilter businessKeyLikeFilter = (BPMProcessInstanceBusinessKeyLikeFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances business keys like: {}", businessKeyLikeFilter.getBusinessKeyLike());
                    processInstanceQueryBuilder.uniqueBusinessKeyLike(businessKeyLikeFilter.getBusinessKeyLike());
                } else if (processInstanceFilter instanceof BPMProcessInstanceFinishedAfterFilter) {
                    BPMProcessInstanceFinishedAfterFilter finishedAfterFilter = (BPMProcessInstanceFinishedAfterFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances finished after: {}", finishedAfterFilter.getFinishedAfter());
                    processInstanceQueryBuilder.finishedAfter(Timestamp.valueOf(finishedAfterFilter.getFinishedAfter()));
                } else if (processInstanceFilter instanceof BPMProcessInstanceFinishedBeforeFilter) {
                    BPMProcessInstanceFinishedBeforeFilter finishedBeforeFilter = (BPMProcessInstanceFinishedBeforeFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances finished before: {}", finishedBeforeFilter.getFinishedBefore());
                    processInstanceQueryBuilder.finishedBefore(Timestamp.valueOf(finishedBeforeFilter.getFinishedBefore()));
                } else if (processInstanceFilter instanceof BPMProcessInstanceProcessDefinitionFilter) {
                    BPMProcessInstanceProcessDefinitionFilter definitionFilter = (BPMProcessInstanceProcessDefinitionFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances with definition key: {}", definitionFilter.getKey());
                    processInstanceQueryBuilder.processDefinitionKey(definitionFilter.getKey());
                } else if (processInstanceFilter instanceof BPMProcessInstanceProcessNameLikeFilter) {
                    BPMProcessInstanceProcessNameLikeFilter nameLikeFilter = (BPMProcessInstanceProcessNameLikeFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances with names likes: {}", nameLikeFilter.getNameLike());
                    processInstanceQueryBuilder.processInstanceNameLike(nameLikeFilter.getNameLike());
                } else if (processInstanceFilter instanceof BPMProcessInstanceStartedAfterFilter) {
                    BPMProcessInstanceStartedAfterFilter startedAfterFilter = (BPMProcessInstanceStartedAfterFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances started after: {}", startedAfterFilter.getStartedAfter());
                    processInstanceQueryBuilder.startedAfter(Timestamp.valueOf(startedAfterFilter.getStartedAfter()));
                } else if (processInstanceFilter instanceof BPMProcessInstanceStartedBeforeFilter) {
                    BPMProcessInstanceStartedBeforeFilter startedBeforeFilter = (BPMProcessInstanceStartedBeforeFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances started before: {}", startedBeforeFilter.getStartedBefore());
                    processInstanceQueryBuilder.startedBefore(Timestamp.valueOf(startedBeforeFilter.getStartedBefore()));
                } else if (processInstanceFilter instanceof BPMProcessInstanceTenantFilter) {
                    BPMProcessInstanceTenantFilter tenantFilter = (BPMProcessInstanceTenantFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances with tenant id: {}", tenantFilter.getTenantId());
                    processInstanceQueryBuilder.tenantId(tenantFilter.getTenantId());
                } else if (processInstanceFilter instanceof BPMProcessInstanceUnfinishedFilter) {
                    LOGGER.debug("Filtering unfinished instances");
                    processInstanceQueryBuilder.onlyUnfinished(true);
                } else if (processInstanceFilter instanceof BPMProcessInstanceFinishedFilter) {
                    LOGGER.debug("Filtering finished instances");
                    processInstanceQueryBuilder.onlyFinished(true);
                } else if (processInstanceFilter instanceof BPMProcessInstanceVariableLikeFilter) {
                    BPMProcessInstanceVariableLikeFilter variableLikeFilter = (BPMProcessInstanceVariableLikeFilter) processInstanceFilter;
                    if (variableLikeFilter.getValueLike() != null) {
                        LOGGER.debug("Filtering instances with variables like: {}: {}", variableLikeFilter.getVariableName(), variableLikeFilter.getValueLike());
                        processInstanceQueryBuilder.variableWithValueLike(variableLikeFilter.getVariableName(), variableLikeFilter.getValueLike());
                    } else {
                        LOGGER.debug("Filtering instances with variables: {}", variableLikeFilter.getVariableName());
                        processInstanceQueryBuilder.variable(variableLikeFilter.getVariableName());
                    }
                } else if (processInstanceFilter instanceof BPMProcessInstanceIncludeProcessVariables) {
                    LOGGER.debug("Including process variables");
                    processInstanceQueryBuilder.includeProcessVariables(true);
                } else {
                    throw new IllegalArgumentException("Unsupported filter");
                }
            }
        }

        return processInstanceQueryBuilder.buildProcessInstanceQuery();
    }

    @Alias("get-process-instances")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMProcessInstanceOutputMetadataResolver.class)
    public List<BPMProcessInstance> fetchProcessInstances(
            @Config BPMExtension engine,
            @Connection BPMConnection connection,
            @Alias("query") BPMProcessInstanceQuery processInstanceQuery,
            @Optional(defaultValue = "0") int firstResult,
            @Optional(defaultValue = "100") int maxResults) {

        LOGGER.debug("Fetching maximum of {} process instances starting from {}", maxResults, firstResult);

        return processInstanceQuery.instances(firstResult, maxResults);
    }

    @Alias("get-unique-process-instance")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMProcessInstanceOutputMetadataResolver.class)
    public BPMProcessInstance fetchUniqueProcessInstance(
            @Config BPMExtension engine,
            @Connection BPMConnection connection,
            @Alias("query") BPMProcessInstanceQuery processInstanceQuery) {

        LOGGER.debug("Fetching unique process instance");

        return processInstanceQuery.uniqueInstance();
    }

}
