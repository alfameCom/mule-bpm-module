package com.alfame.esb.bpm.api;

public enum BPMEngineEventType {

    PROCESS_INSTANCE_CREATED,
    PROCESS_INSTANCE_ENDED,
    VARIABLE_CREATED,
    VARIABLE_UPDATED,
    VARIABLE_REMOVED,
    ACTIVITY_STARTED,
    ACTIVITY_FAILURE,
    ACTIVITY_COMPLETED,
    ENGINE_STARTED,
    ENGINE_STOPPED,
    TASK_CREATED,
    TASK_COMPLETED,
    UNKNOWN;

    public String getValue() {
        return this.toString();
    }

}