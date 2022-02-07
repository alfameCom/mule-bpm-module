package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.time.LocalDateTime;

@Alias("process-instance-ended-after-filter")
public class BPMProcessInstanceEndedAfterFilter extends BPMProcessInstanceFilter {

    @Parameter
    private LocalDateTime endedAfter;

    public LocalDateTime getEndedAfter() {
        return endedAfter;
    }

}
