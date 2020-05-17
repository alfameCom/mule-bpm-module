package com.alfame.esb.bpm.module.api.param;

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