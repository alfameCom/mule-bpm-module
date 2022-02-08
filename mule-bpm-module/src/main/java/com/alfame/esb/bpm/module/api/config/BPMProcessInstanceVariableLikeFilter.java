package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.io.Serializable;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.api.meta.ExpressionSupport.SUPPORTED;

@Alias("process-instance-variable-like-filter")
public class BPMProcessInstanceVariableLikeFilter extends BPMProcessInstanceFilter {

    @Parameter
    @Expression(NOT_SUPPORTED)
    private String variableName;

    @Content
    @Parameter
    @Expression(SUPPORTED)
    @Optional
    private TypedValue<Serializable> valueLike;

    public String getVariableName() {
        return variableName;
    }

    public TypedValue<Serializable> getValueLike() {
        return valueLike;
    }

}
