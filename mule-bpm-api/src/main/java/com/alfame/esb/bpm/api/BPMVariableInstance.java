package com.alfame.esb.bpm.api;

public interface BPMVariableInstance extends BPMVariableAttributes {

    /**
     * @return the content id of the variable
     */
    Object getValue();

}
