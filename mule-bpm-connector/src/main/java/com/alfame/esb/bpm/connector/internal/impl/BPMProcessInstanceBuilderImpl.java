package com.alfame.esb.bpm.connector.internal.impl;

import com.alfame.esb.bpm.api.BPMEngine;
import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.api.BPMProcessInstanceBuilder;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.slf4j.Logger;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessInstanceBuilderImpl extends BPMProcessInstanceBuilder {

    private static final Logger LOGGER = getLogger(BPMProcessInstanceBuilderImpl.class);

    private final BPMEngine engine;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    public BPMProcessInstanceBuilderImpl(BPMEngine engine, RuntimeService runtimeService, HistoryService historyService) {
        this.engine = engine;
        this.runtimeService = runtimeService;
        this.historyService = historyService;
    }

    @Override
    public BPMProcessInstance startProcessInstance() {
        BPMProcessInstance builtProcessInstance = null;
        ProcessInstance processInstance = null;
        ProcessInstanceBuilder instanceBuilder = this.runtimeService.createProcessInstanceBuilder();
        long startTime = System.currentTimeMillis();

        LOGGER.info(">>>>> process definition {}: instance starting", this.processDefinitionKey);
        instanceBuilder = instanceBuilder.processDefinitionKey(this.processDefinitionKey);

        if (this.tenantId != null) {
            LOGGER.debug(">>>>> process definition {}: instance starting with tenant {}", this.processDefinitionKey, this.tenantId);
            instanceBuilder = instanceBuilder.tenantId(this.tenantId);
        } else {
            LOGGER.debug(">>>>> process definition {}: instance starting with tenant {}", this.processDefinitionKey, this.engine.getDefaultTenantId());
            instanceBuilder = instanceBuilder.tenantId(this.engine.getDefaultTenantId());
        }

        if (this.uniqueBusinessKey != null) {
            LOGGER.debug(">>>>> process definition {}: instance starting with business key {}", this.processDefinitionKey, this.uniqueBusinessKey);
            instanceBuilder = instanceBuilder.businessKey(this.uniqueBusinessKey);
        }

        if (this.processInstanceName != null) {
            LOGGER.debug(">>>>> process definition {}: instance starting with name {}", this.processDefinitionKey, this.processInstanceName);
            instanceBuilder = instanceBuilder.name(this.processInstanceName);
        }

        if (this.variables != null) {
            if (LOGGER.isDebugEnabled()) {
                for (Map.Entry<String, Object> variable : this.variables.entrySet()) {
                    LOGGER.debug(">>>>> process definition {}: instance starting with variable {} = {}", this.processDefinitionKey, variable.getKey(), variable.getValue());
                }
            }
            instanceBuilder = instanceBuilder.variables(this.variables);
        }

        try {
            processInstance = instanceBuilder.start();
            builtProcessInstance = new BPMProcessInstanceProxy(processInstance);
        } catch (Exception exception) {
            try {
                if (this.uniqueBusinessKey != null && this.returnCollidedInstance) {
                    HistoricProcessInstance historicProcessInstance =
                            this.historyService.createHistoricProcessInstanceQuery()
                                    .processInstanceBusinessKey(this.uniqueBusinessKey).singleResult();
                    if (historicProcessInstance != null) {
                        builtProcessInstance = new BPMProcessHistoricInstanceProxy(historicProcessInstance);
                    } else {
                        LOGGER.error("<<<<< process definition {}: instance {} caught exception {} in {} ms", this.processDefinitionKey, processInstance != null ? processInstance.getProcessInstanceId(): null, exception, System.currentTimeMillis() - startTime);
                        throw exception;
                    }
                } else {
                    LOGGER.error("<<<<< process definition {}: instance {} caught exception {} in {} ms", this.processDefinitionKey, processInstance != null ? processInstance.getProcessInstanceId(): null, exception, System.currentTimeMillis() - startTime);
                    throw exception;
                }
            } catch (Exception historyException) {
                LOGGER.error("<<<<< process definition {}: instance {} caught exception {} in {} ms", this.processDefinitionKey, processInstance != null ? processInstance.getProcessInstanceId(): null, historyException, System.currentTimeMillis() - startTime);
                throw historyException;
            }
        }

        LOGGER.info("<<<<< process definition {}: instance {} started in {} ms", this.processDefinitionKey, builtProcessInstance.getProcessInstanceId(), System.currentTimeMillis() - startTime);
        return builtProcessInstance;
    }

}
