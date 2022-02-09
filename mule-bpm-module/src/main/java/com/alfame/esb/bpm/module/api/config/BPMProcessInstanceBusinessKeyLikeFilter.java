package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias("process-instance-business-key-like-filter")
public class BPMProcessInstanceBusinessKeyLikeFilter extends BPMProcessInstanceFilter {

    @Parameter
    private String businessKeyLike;

    public String getBusinessKeyLike() {
        return businessKeyLike;
    }

}
