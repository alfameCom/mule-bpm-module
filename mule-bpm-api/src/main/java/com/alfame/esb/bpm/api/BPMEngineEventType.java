package com.alfame.esb.bpm.api;

public enum BPMEngineEventType {

    PROCESS_INSTANCE_CREATED,
    PROCESS_INSTANCE_ENDED,
    VARIABLE_CREATED,
    VARIABLE_UPDATED,
    VARIABLE_REMOVED,
    UNKNOWN;

    public String getValue() {
        return this.toString();
    }

}