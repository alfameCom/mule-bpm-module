package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.time.LocalDateTime;

@Alias("process-instance-finished-before-filter")
public class BPMProcessInstanceFinishedBeforeFilter extends BPMProcessInstanceFilter {

    @Parameter
    private LocalDateTime finishedBefore;

    public LocalDateTime getFinishedBefore() {
        return finishedBefore;
    }

}
