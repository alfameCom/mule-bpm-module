package com.alfame.esb.bpm.connector.internal.listener;

import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.io.Serializable;

public class BPMTaskListenerErrorResponseBuilder {

    @Parameter
    @Content(primary = true)
    @Optional(defaultValue = "false")
    private TypedValue<Serializable> value;

    public TypedValue<Serializable> getValue() {
        return value;
    }

}
