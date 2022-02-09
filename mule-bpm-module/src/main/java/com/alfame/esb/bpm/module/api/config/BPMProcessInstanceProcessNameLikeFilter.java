package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias("process-instance-name-like-filter")
public class BPMProcessInstanceProcessNameLikeFilter extends BPMProcessInstanceFilter {

    @Parameter
    private String nameLike;

    public String getNameLike() {
        return nameLike;
    }

}
