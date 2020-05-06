package com.alfame.esb.bpm.connector.api.param;

public enum BPMEventType {

    processInstanceCreated,
    processInstanceEnded,
    variableCreated,
    variableUpdated,
    variableRemoved;

    public String getValue() {
        return this.toString();
    }

}