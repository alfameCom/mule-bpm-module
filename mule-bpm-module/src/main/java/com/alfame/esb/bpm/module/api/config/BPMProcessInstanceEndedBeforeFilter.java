package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.time.LocalDateTime;

@Alias("process-instance-ended-before-filter")
public class BPMProcessInstanceEndedBeforeFilter extends BPMProcessInstanceFilter {

    @Parameter
    private LocalDateTime endedBefore;

    public LocalDateTime getEndedBefore() {
        return endedBefore;
    }

}
