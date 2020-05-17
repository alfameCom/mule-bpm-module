package com.alfame.esb.bpm.module.internal.listener;

import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.io.Serializable;

public class BPMTaskListenerSuccessResponseBuilder {

    @Parameter
    @Content(primary = true)
    @Optional(defaultValue = "true")
    private TypedValue<Serializable> value;

    public TypedValue<Serializable> getValue() {
        return value;
    }

}
