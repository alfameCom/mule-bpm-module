package com.alfame.esb.bpm.connector;

import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.test.runner.ArtifactClassLoaderRunnerConfig;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@ArtifactClassLoaderRunnerConfig(
        applicationSharedRuntimeLibs = {"com.h2database:h2", "com.alfame.esb.bpm:mule-bpm-api", "com.alfame.esb.bpm:mule-bpm-task-queue"})
public abstract class BPMAbstractTestCase extends MuleArtifactFunctionalTestCase {

    protected static final Logger LOGGER = getLogger(BPMAbstractTestCase.class);

}
