package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.time.LocalDateTime;

@Alias("process-instance-started-after-filter")
public class BPMProcessInstanceStartedAfterFilter extends BPMProcessInstanceFilter {

    @Parameter
    private LocalDateTime startedAfter;

    public LocalDateTime getStartedAfter() {
        return startedAfter;
    }

}
