package com.alfame.esb.bpm.module.internal.listener;

import com.alfame.esb.bpm.api.BPMTaskInstance;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.metadata.resolving.AttributesStaticTypeResolver;

public class BPMTaskListenerAttributesMetadataResolver extends AttributesStaticTypeResolver {

    private static MetadataType loadMetadataType(Class<?> classType) {
        return new JavaTypeLoader(ClassLoader.getSystemClassLoader()).load(classType);
    }

    @Override
    public String getCategoryName() {
        return "BPM";
    }

    @Override
    public MetadataType getStaticMetadata() {
        return loadMetadataType(BPMTaskInstance.class);
    }

}
