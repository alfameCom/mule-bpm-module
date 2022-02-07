package com.alfame.esb.bpm.module.internal.operations;

import com.alfame.esb.bpm.api.BPMProcessInstance;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

;

public class BPMProcessInstanceOutputMetadataResolver implements OutputTypeResolver<BPMProcessInstance> {

    private static MetadataType loadMetadataType(Class<?> classType) {
        return new JavaTypeLoader(ClassLoader.getSystemClassLoader()).load(classType);
    }

    @Override
    public MetadataType getOutputType(MetadataContext metadataContext, BPMProcessInstance processInstance) throws MetadataResolvingException, ConnectionException {
        return loadMetadataType(processInstance.getClass());
    }

    @Override
    public String getCategoryName() {
        return "BPM";
    }

    @Override
    public String getResolverName() {
        return "BPMProcessInstanceOutputMetadataResolver";
    }
}
