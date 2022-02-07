package com.alfame.esb.bpm.module.internal.operations;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.module.api.config.*;
import com.alfame.esb.bpm.module.internal.BPMExtension;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessInstanceOperations {
    private static final Logger LOGGER = getLogger(BPMProcessInstanceOperations.class);

    @Alias("delete-process-instance")
    public void deleteProcessInstance(
            @Config BPMExtension config,
            @DisplayName("Process instance Id") String processInstanceId,
            @Optional @DisplayName("Delete reason") String deleteReason) throws IOException {
        
        config.deleteProcessInstance(processInstanceId, deleteReason);

        LOGGER.debug("Trying to delete process instance id {} with reason {}", processInstanceId, deleteReason);
    }


    @Alias("get-process-instances")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMProcessInstanceOutputMetadataResolver.class)
    public List<BPMProcessInstance> fetchProcessInstances(
            @Config BPMExtension engine,
            @Optional @Alias("process-instance-filters") List<BPMProcessInstanceFilter> processInstanceFilters) throws InterruptedException {

        LOGGER.debug("Fetching process instances");

        if (processInstanceFilters != null && processInstanceFilters.size() > 0) {
            for (BPMProcessInstanceFilter processInstanceFilter : processInstanceFilters) {
                if (processInstanceFilter instanceof BPMProcessInstanceBusinessKeyLikeFilter) {
                    BPMProcessInstanceBusinessKeyLikeFilter businessKeyLikeFilter = (BPMProcessInstanceBusinessKeyLikeFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances business keys like: {}", businessKeyLikeFilter.getBusinessKeyLike());
                } else if (processInstanceFilter instanceof BPMProcessInstanceEndedAfterFilter) {
                    BPMProcessInstanceEndedAfterFilter endedAfterFilter = (BPMProcessInstanceEndedAfterFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances ended after: {}", endedAfterFilter.getEndedAfter());
                } else if (processInstanceFilter instanceof BPMProcessInstanceEndedBeforeFilter) {
                    BPMProcessInstanceEndedBeforeFilter endedBeforeFilter = (BPMProcessInstanceEndedBeforeFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances before after: {}", endedBeforeFilter.getEndedBefore());
                } else if (processInstanceFilter instanceof BPMProcessInstanceProcessDefinitionFilter) {
                    BPMProcessInstanceProcessDefinitionFilter definitionFilter = (BPMProcessInstanceProcessDefinitionFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances with definition key: {}", definitionFilter.getKey());
                } else if (processInstanceFilter instanceof BPMProcessInstanceProcessNameLikeFilter) {
                    BPMProcessInstanceProcessNameLikeFilter nameLikeFilter = (BPMProcessInstanceProcessNameLikeFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances with names likes: {}", nameLikeFilter.getNameLike());
                } else if (processInstanceFilter instanceof BPMProcessInstanceStartedAfterFilter) {
                    BPMProcessInstanceStartedAfterFilter startedAfterFilter = (BPMProcessInstanceStartedAfterFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances started after: {}", startedAfterFilter.getStartedAfter());
                } else if (processInstanceFilter instanceof BPMProcessInstanceStartedBeforeFilter) {
                    BPMProcessInstanceStartedBeforeFilter startedBeforeFilter = (BPMProcessInstanceStartedBeforeFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances started before: {}", startedBeforeFilter.getStartedBefore());
                } else if (processInstanceFilter instanceof BPMProcessInstanceTenantFilter) {
                    BPMProcessInstanceTenantFilter tenantFilter = (BPMProcessInstanceTenantFilter) processInstanceFilter;
                    LOGGER.debug("Filtering instances with tenant id: {}", tenantFilter.getTenantId());
                } else if (processInstanceFilter instanceof BPMProcessInstanceUnfinishedFilter) {
                    LOGGER.debug("Filtering unfinished instances");
                } else if (processInstanceFilter instanceof BPMProcessInstanceFinishedFilter) {
                    LOGGER.debug("Filtering finished instances");
                } else if (processInstanceFilter instanceof BPMProcessInstanceVariableLikeFilter) {
                    BPMProcessInstanceVariableLikeFilter variableLikeFilter = (BPMProcessInstanceVariableLikeFilter) processInstanceFilter;
                    if (variableLikeFilter.getValueLike() != null) {
                        LOGGER.debug("Filtering instances with variables like: {}: {}", variableLikeFilter.getVariableName(), variableLikeFilter.getValueLike().getValue());
                    } else {
                        LOGGER.debug("Filtering instances with variables: {}", variableLikeFilter.getVariableName());
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported filter");
                }
            }
        }

        return null;
    }

}
