package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.time.LocalDateTime;

@Alias("process-instance-finished-after-filter")
public class BPMProcessInstanceFinishedAfterFilter extends BPMProcessInstanceFilter {

    @Parameter
    private LocalDateTime finishedAfter;

    public LocalDateTime getFinishedAfter() {
        return finishedAfter;
    }

}
