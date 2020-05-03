package com.alfame.esb.bpm.connector.internal;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.api.BPMProcessInstanceBuilder;
import com.alfame.esb.bpm.connector.internal.proxies.BPMProcessInstanceProxy;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMProcessInstanceBuilderImpl extends BPMProcessInstanceBuilder {

    private static final Logger LOGGER = getLogger(BPMProcessInstanceBuilderImpl.class);

    private BPMEngineDetails engineDetails;

    public BPMProcessInstanceBuilderImpl(BPMEngineDetails engineDetails) {
        this.engineDetails = engineDetails;
    }

    @Override
    public BPMProcessInstance startProcessInstance() {
        ProcessInstanceBuilder instanceBuilder = this.engineDetails.getRuntimeService().createProcessInstanceBuilder();

        LOGGER.debug("Starting process instance with definition key: " + this.processDefinitionKey);
        instanceBuilder = instanceBuilder.processDefinitionKey(this.processDefinitionKey);

        if (this.tenantId != null) {
            instanceBuilder = instanceBuilder.tenantId(this.tenantId);
        } else {
            instanceBuilder = instanceBuilder.tenantId(this.engineDetails.getDefaultTenantId());
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
