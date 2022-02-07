package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias("process-instance-only-unfinished-filter")
public class BPMProcessInstanceUnfinishedFilter extends BPMProcessInstanceFilter {

    @Parameter
    @Optional(defaultValue = "false")
    private boolean unfinished;

    public boolean isUnfinished() {
        return unfinished;
    }
}
