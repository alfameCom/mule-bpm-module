package com.alfame.esb.bpm.connector.internal.operations;

import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.metadata.resolving.OutputStaticTypeResolver;

import java.util.Optional;

public class BPMProcessVariableOutputMetadataResolver extends OutputStaticTypeResolver {

    private static Optional<MetadataType> loadMetadataType(String className) {
        return new JavaTypeLoader(ClassLoader.getSystemClassLoader()).load(className);
    }

    @Override
    public String getCategoryName() {
        return "BPM";
    }

    @Override
    public MetadataType getStaticMetadata() {
        Optional<MetadataType> payloadMetadaType = loadMetadataType("java.lang.Object");
        if (!payloadMetadaType.isPresent()) {
            return null;
        }
        return payloadMetadaType.get();
    }

}
