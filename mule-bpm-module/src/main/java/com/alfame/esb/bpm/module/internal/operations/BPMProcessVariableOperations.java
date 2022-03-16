package com.alfame.esb.bpm.module.internal.operations;

import com.alfame.esb.bpm.api.BPMVariableAttributes;
import com.alfame.esb.bpm.api.BPMVariableInstance;
import com.alfame.esb.bpm.module.internal.BPMExtension;
import com.alfame.esb.bpm.module.internal.connection.BPMTaskConnection;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.operation.Result.Builder;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.slf4j.Logger;

import java.io.Serializable;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessVariableOperations {

    private static final Logger LOGGER = getLogger(BPMProcessVariableOperations.class);

    @Alias("get-variable")
    @MediaType(value = ANY, strict = false)
    @OutputResolver(output = BPMProcessVariableOutputMetadataResolver.class, attributes = BPMProcessVariableAttributesMetadataResolver.class)
    public Result<Object, BPMVariableAttributes> getVariable(
            @Config BPMExtension config,
            @Connection BPMTaskConnection connection,
            @Optional @DisplayName("Process instance id") String processInstanceId,
            @DisplayName("Variable name") String variableName,
            CorrelationInfo correlationInfo) {
        Builder<Object, BPMVariableAttributes> resultBuilder = Result.builder();

        BPMVariableInstance variableInstance = null;
        if (processInstanceId == null) {
            connection = connection.joinIfForked(correlationInfo);

            variableInstance = connection.getTask().getProcessInstance().getVariableInstance(variableName);

            if (variableInstance == null) {
                processInstanceId = connection.getTask().getProcessInstanceId();

                LOGGER.debug("Variable {} not found from cache for process {}, querying from database", variableName, processInstanceId);
                variableInstance = config.getVariableInstance(processInstanceId, variableName);
            }
        } else {
            variableInstance = config.getHistoricVariableInstance(processInstanceId, variableName);
        }

        if (variableInstance != null) {
            LOGGER.debug("Variable {} found for process {}", variableName, processInstanceId);

            resultBuilder.output(variableInstance.getValue());
            resultBuilder.attributes(variableInstance);
        } else {
            LOGGER.debug("Variable {} not found for process {}", variableName, processInstanceId);
        }

        return resultBuilder.build();
    }

    @Alias("set-variable")
    public void setVariable(
            @Config BPMExtension config,
            @Connection BPMTaskConnection connection,
            @DisplayName("Variable name") String variableName,
            @Alias("variable-content") @Content @Summary("Content for variable") TypedValue<Serializable> variableContent,
            CorrelationInfo correlationInfo) {
        connection = connection.joinIfForked(correlationInfo);

        connection.getVariablesToUpdate().put(variableName, variableContent.getValue());

        LOGGER.debug("Variable {} set to be updated for process {}", variableName, connection.getTask().getProcessInstanceId());
    }

    @Alias("remove-variable")
    @MediaType(value = ANY, strict = false)
    public void removeVariable(
            @Config BPMExtension config,
            @Connection BPMTaskConnection connection,
            @DisplayName("Variable name") String variableName,
            CorrelationInfo correlationInfo) {
        connection = connection.joinIfForked(correlationInfo);

        connection.getVariablesToRemove().add(variableName);

        LOGGER.debug("Variable {} set to be removed for process {}", variableName, connection.getTask().getProcessInstanceId());
    }

}
