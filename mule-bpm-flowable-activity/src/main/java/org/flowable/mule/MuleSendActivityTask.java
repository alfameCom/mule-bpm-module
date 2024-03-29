package org.flowable.mule;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import com.alfame.esb.bpm.taskqueue.BPMTask;
import org.flowable.common.engine.impl.context.Context;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.delegate.DelegateExecution;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class MuleSendActivityTask extends BPMTask {

    private final Object payload;
    private final String correlationId;
    private final DelegateExecution execution;
    private final CommandContext commandContext;

    public MuleSendActivityTask(Object payload, String correlationId, DelegateExecution execution, CommandContext commandContext) {
        this.payload = payload;
        this.correlationId = correlationId;
        this.execution = execution;
        this.commandContext = commandContext;
    }

    @Override
    public BPMProcessInstance getProcessInstance() {
        return new MuleDelegateExecutionProxy(this.execution);
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public Optional<String> getCorrelationId() {
        return ofNullable(correlationId);
    }

    @Override
    public void applyCommandContext() {
        Context.setCommandContext(this.commandContext);
    }

    @Override
    public String getActivityId() {
        return this.execution.getCurrentActivityId();
    }

    @Override
    public String getId() {
        return this.execution.getId();
    }

    @Override
    public String getParentId() {
        return this.execution.getParentId();
    }

    @Override
    public String getProcessInstanceId() {
        return this.execution.getProcessInstanceId();
    }

    @Override
    public String getProcessDefinitionId() { return this.execution.getProcessDefinitionId(); }

    @Override
    public String getProcessDefinitionKey() { return this.execution.getProcessDefinitionId().replaceFirst(":.*", ""); }

    @Override
    public String getSuperExecutionId() {
        return this.execution.getSuperExecutionId();
    }

    @Override
    public String getTenantId() {
        return this.execution.getTenantId();
    }

}
