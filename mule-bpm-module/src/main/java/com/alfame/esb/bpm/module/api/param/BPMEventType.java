package com.alfame.esb.bpm.module.api.param;

public enum BPMEventType {

    PROCESS_INSTANCE_CREATED,
    PROCESS_INSTANCE_ENDED,
    VARIABLE_CREATED,
    VARIABLE_UPDATED,
    VARIABLE_REMOVED,
    ACTIVITY_STARTED,
    ACTIVITY_FAILURE,
    ACTIVITY_COMPLETED,
    TASK_CREATED;

    public String getValue() {
        return this.toString();
    }

}