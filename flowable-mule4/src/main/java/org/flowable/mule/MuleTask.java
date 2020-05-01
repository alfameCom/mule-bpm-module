package org.flowable.mule;

import java.util.Optional;
import java.util.concurrent.*;

import com.alfame.esb.bpm.queue.BPMBaseTask;

import org.flowable.engine.delegate.DelegateExecution;

import static java.util.Optional.ofNullable;

public class MuleTask extends BPMBaseTask {

	private final Object payload;
	private final String correlationId;
	private final DelegateExecution execution;

	public MuleTask( Object payload, String correlationId, DelegateExecution execution ) {
		this.payload = payload;
		this.correlationId = correlationId;
		this.execution = execution;
	}

	@Override
	public Object getPayload() {
		return payload;
	}

	@Override
	public Optional< String > getCorrelationId() {
		return ofNullable( correlationId );
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
	public String getSuperExecutionId() {
		return this.execution.getSuperExecutionId();
	}

	@Override
	public String getTenantId() {
		return this.execution.getTenantId();
	}
	
}
