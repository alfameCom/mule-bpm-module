package com.alfame.esb.connectors.bpm.internal.operations;

import org.flowable.engine.runtime.ProcessInstance;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.metadata.resolving.OutputStaticTypeResolver;

public class BPMProcessFactoryMetadataResolver extends OutputStaticTypeResolver {
	
	private static MetadataType loadMetadataType( Class<?> classType ) {
		return new JavaTypeLoader( ClassLoader.getSystemClassLoader() ).load( classType );
	}
	
	private static final MetadataType METADATA_TYPE = loadMetadataType( ProcessInstance.class );

	@Override
	public String getCategoryName() {
		return "BPM";
	}

	@Override
	public MetadataType getStaticMetadata() {
		return METADATA_TYPE;
	}
}
