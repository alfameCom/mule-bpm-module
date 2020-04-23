package com.alfame.esb.bpm.connector.internal.operations;

import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.metadata.resolving.OutputStaticTypeResolver;

import com.alfame.esb.bpm.api.BPMProcessInstance;

public class BPMProcessFactoryMetadataResolver extends OutputStaticTypeResolver {
	
	private static MetadataType loadMetadataType( Class<?> classType ) {
		return new JavaTypeLoader( ClassLoader.getSystemClassLoader() ).load( classType );
	}
	
	private static final MetadataType METADATA_TYPE = loadMetadataType( BPMProcessInstance.class );

	@Override
	public String getCategoryName() {
		return "BPM";
	}

	@Override
	public MetadataType getStaticMetadata() {
		return METADATA_TYPE;
	}
}
