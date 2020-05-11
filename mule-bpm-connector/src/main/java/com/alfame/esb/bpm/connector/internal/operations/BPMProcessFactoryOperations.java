package com.alfame.esb.bpm.connector.internal.operations;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.connector.api.config.BPMProcessVariable;
import com.alfame.esb.bpm.connector.internal.BPMExtension;
import com.alfame.esb.bpm.connector.internal.BPMProcessInstanceBuilderImpl;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessFactoryOperations {

    private static final Logger LOGGER = getLogger(BPMProcessFactoryOperations.class);

    @Alias("process-factory")
    @MediaType(value = MediaType.ANY, strict = false)
    @OutputResolver(output = BPMProcessFactoryOutputMetadataResolver.class)
    public BPMProcessInstance processFactory(
            @ParameterGroup(name = "properties") BPMProcessFactoryProperties properties,
            @Config BPMExtension engine,
            @Optional @Alias("process-variables") List<BPMProcessVariable> processVariables) {

        BPMProcessInstance processInstance = null;

        BPMProcessInstanceBuilderImpl instanceBuilder = new BPMProcessInstanceBuilderImpl(
                engine, engine.getRuntimeService(), engine.getHistoryService());

        instanceBuilder
                .processDefinitionKey(properties.getProcessDefinitionKey())
                .tenantId(properties.getTenantId())
                .uniqueBusinessKey(properties.getUniqueBusinessKey())
                .processInstanceName(properties.getProcessName())
                .returnCollidedInstance(properties.getReturnCollidedInstance());

        if (processVariables != null) {
            for (BPMProcessVariable processVariable : processVariables) {
                TypedValue<Serializable> value = processVariable.getValue();
                if (value != null) {
                    instanceBuilder.variableWithValue(
                            processVariable.getVariableName(),
                            value.getValue());
                } else {
                    instanceBuilder.variable(
                            processVariable.getVariableName());
                }
            }
        }

        processInstance = instanceBuilder.startProcessInstance();

        LOGGER.debug("Started process instance " + processInstance.getProcessInstanceId());

        return processInstance;

    }

    @Alias("trigger-signal")
    @MediaType(value = MediaType.ANY, strict = false)
    public void triggerSignal(
            @Config BPMExtension engine,
            String processInstanceId,
            String signalName) {

        engine.triggerSignal(processInstanceId, signalName);

        LOGGER.debug("Signaled process instance " + processInstanceId + " with " + signalName);
    }
}
