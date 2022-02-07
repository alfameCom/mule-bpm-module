package com.alfame.esb.bpm.module.internal.operations;

import com.alfame.esb.bpm.api.BPMEngineEventSubscription;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.metadata.resolving.OutputStaticTypeResolver;

public class BPMEventSubscriptionOutputMetadataResolver extends OutputStaticTypeResolver {

    private static MetadataType loadMetadataType(Class<?> classType) {
        return new JavaTypeLoader(ClassLoader.getSystemClassLoader()).load(classType);
    }

    private static final MetadataType METADATA_TYPE = loadMetadataType(BPMEngineEventSubscription.class);

    @Override
    public String getCategoryName() {
        return "BPM";
    }

    @Override
    public String getResolverName() {
        return "BPMEventSubscriptionOutputMetadataResolver";
    }

    @Override
    public MetadataType getStaticMetadata() {
        return METADATA_TYPE;
    }
}
