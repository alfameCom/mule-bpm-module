package com.alfame.esb.bpm.connector.internal;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;

public interface BPMEngineDetails {
	
	public String getName();
	public String getDefaultTenantId();
	public RuntimeService getRuntimeService();
	public HistoryService getHistoryService();
	public TaskService getTaskService();
	
}
