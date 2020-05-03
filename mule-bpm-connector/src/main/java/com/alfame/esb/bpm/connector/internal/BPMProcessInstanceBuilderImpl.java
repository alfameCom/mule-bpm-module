package com.alfame.esb.bpm.connector.internal;

import com.alfame.esb.bpm.api.BPMEngine;
import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.api.BPMProcessInstanceBuilder;
import com.alfame.esb.bpm.connector.internal.proxies.BPMProcessInstanceProxy;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessInstanceBuilderImpl extends BPMProcessInstanceBuilder {

    private static final Logger LOGGER = getLogger(BPMProcessInstanceBuilderImpl.class);

    private final BPMEngine engine;
    private final RuntimeService runtimeService;

    public BPMProcessInstanceBuilderImpl(BPMEngine engine, RuntimeService runtimeService) {
        this.engine = engine;
        this.runtimeService = runtimeService;
    }

    @Override
    public BPMProcessInstance startProcessInstance() {
        ProcessInstanceBuilder instanceBuilder = this.runtimeService.createProcessInstanceBuilder();

        LOGGER.debug("Starting process instance with definition key: " + this.processDefinitionKey);
        instanceBuilder = instanceBuilder.processDefinitionKey(this.processDefinitionKey);

        if (this.tenantId != null) {
            instanceBuilder = instanceBuilder.tenantId(this.tenantId);
        } else {
            instanceBuilder = instanceBuilder.tenantId(this.engine.getDefaultTenantId());
        }

        if (this.uniqueBusinessKey != null) {
            instanceBuilder = instanceBuilder.businessKey(this.tenantId);
        }

        if (this.processInstanceName != null) {
            instanceBuilder = instanceBuilder.name(this.processInstanceName);
        }

        if (this.variables != null) {
            instanceBuilder = instanceBuilder.variables(this.variables);
        }

        return new BPMProcessInstanceProxy(instanceBuilder.start());
    }

}
