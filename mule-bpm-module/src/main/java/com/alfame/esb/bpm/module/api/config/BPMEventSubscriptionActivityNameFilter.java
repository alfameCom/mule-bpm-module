package com.alfame.esb.bpm.module.api.config;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Alias("activity-name-filter")
public class BPMEventSubscriptionActivityNameFilter extends BPMEventSubscriptionFilter {

    @Parameter
    private String activityName;

    public String getActivityName() {
        return activityName;
    }

}
