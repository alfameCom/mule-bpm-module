package com.alfame.esb.bpm.connector.internal.operations;

import com.alfame.esb.bpm.api.BPMEngineEvent;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

;

public class BPMEventSubscriptionEventsOutputMetadataResolver implements OutputTypeResolver<BPMEngineEvent> {

    private static MetadataType loadMetadataType(Class<?> classType) {
        return new JavaTypeLoader(ClassLoader.getSystemClassLoader()).load(classType);
    }

    @Override
    public MetadataType getOutputType(MetadataContext metadataContext, BPMEngineEvent bpmEngineEvent) throws MetadataResolvingException, ConnectionException {
        return loadMetadataType(bpmEngineEvent.getClass());
    }

    @Override
    public String getCategoryName() {
        return "BPM";
    }
}
