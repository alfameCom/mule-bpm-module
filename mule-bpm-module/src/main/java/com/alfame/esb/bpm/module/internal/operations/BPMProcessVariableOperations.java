package com.alfame.esb.bpm.module.internal.operations;

import com.alfame.esb.bpm.api.BPMVariableAttributes;
import com.alfame.esb.bpm.api.BPMVariableInstance;
import com.alfame.esb.bpm.module.internal.BPMExtension;
import com.alfame.esb.bpm.module.internal.connection.BPMConnection;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.operation.Result.Builder;
import org.slf4j.Logger;

import java.io.IOException;
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
            @Connection BPMConnection connection,
            @DisplayName("Variable name") String variableName) {
        if (connection == null || connection.getTask() == null) {
            throw new IllegalStateException("Variable operations must join to task listener transaction");
        }

        Builder<Object, BPMVariableAttributes> resultBuilder = Result.builder();

        BPMVariableInstance variableInstance = config.getVariableInstance(
                connection.getTask().getProcessInstanceId(), variableName);

        if (variableInstance != null) {
            LOGGER.debug("Variable " + variableName + " found for process " + connection.getTask().getProcessInstanceId());

            resultBuilder.output(variableInstance.getValue());
            resultBuilder.attributes(variableInstance);
        } else {
            LOGGER.debug("Variable " + variableName + " not found for process " + connection.getTask().getProcessInstanceId());
        }

        return resultBuilder.build();
    }

    @Alias("set-variable")
    public void setVariable(
            @Config BPMExtension config,
            @Connection BPMConnection connection,
            @DisplayName("Variable name") String variableName,
            @Alias("variable-content") @Content @Summary("Content for variable") TypedValue<Serializable> variableContent) throws IOException {
        if (connection == null || connection.getTask() == null) {
            throw new IllegalStateException("Variable operations must join to task listener transaction");
        }

        connection.getVariablesToUpdate().put(variableName, variableContent.getValue());

        LOGGER.debug("Variable " + variableName + " set to be updated for process " + connection.getTask().getProcessInstanceId());
    }

    @Alias("remove-variable")
    @MediaType(value = ANY, strict = false)
    public void removeVariable(
            @Config BPMExtension config,
            @Connection BPMConnection connection,
            @DisplayName("Variable name") String variableName) {
        if (connection == null || connection.getTask() == null) {
            throw new IllegalStateException("Variable operations must join to task listener transaction");
        }

        connection.getVariablesToRemove().add(variableName);

        LOGGER.debug("Variable " + variableName + " set to be removed for process " + connection.getTask().getProcessInstanceId());
    }

}
