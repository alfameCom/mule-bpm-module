package com.alfame.esb.bpm.connector.internal.operations;

import com.alfame.esb.bpm.api.BPMAttachmentAttributes;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.metadata.resolving.AttributesStaticTypeResolver;

public class BPMAttachmentAttributesMetadataResolver extends AttributesStaticTypeResolver {

    private static MetadataType loadMetadataType(Class<?> classType) {
        return new JavaTypeLoader(ClassLoader.getSystemClassLoader()).load(classType);
    }

    private static final MetadataType METADATA_TYPE = loadMetadataType(BPMAttachmentAttributes.class);

    @Override
    public String getCategoryName() {
        return "BPM";
    }

    @Override
    public MetadataType getStaticMetadata() {
        return METADATA_TYPE;
    }
}
