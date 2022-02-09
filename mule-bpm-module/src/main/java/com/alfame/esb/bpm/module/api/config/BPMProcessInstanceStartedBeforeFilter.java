package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.time.LocalDateTime;

@Alias("process-instance-started-before-filter")
public class BPMProcessInstanceStartedBeforeFilter extends BPMProcessInstanceFilter {

    @Parameter
    private LocalDateTime startedBefore;

    public LocalDateTime getStartedBefore() {
        return startedBefore;
    }

}
