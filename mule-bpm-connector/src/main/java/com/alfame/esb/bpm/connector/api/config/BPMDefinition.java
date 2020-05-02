package com.alfame.esb.bpm.connector.api.config;

import org.flowable.engine.repository.DeploymentBuilder;
import org.mule.runtime.extension.api.annotation.Extensible;

@Extensible
public abstract class BPMDefinition {

    public abstract String getType();

    public abstract String getResourceName();

    public abstract void addToDeploymentBuilder(DeploymentBuilder deploymentBuilder);

}
