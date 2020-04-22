package com.alfame.esb.bpm.connector.internal.listener;

import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.metadata.resolving.AttributesStaticTypeResolver;

import com.alfame.esb.bpm.api.BPMTask;

public class BPMTaskListenerAttributesMetadataResolver extends AttributesStaticTypeResolver {

	private static MetadataType loadMetadataType( Class<?> classType ) {
		return new JavaTypeLoader( ClassLoader.getSystemClassLoader() ).load( classType );
	}

	@Override
	public String getCategoryName() {
		return "BPM";
	}

	@Override
	public MetadataType getStaticMetadata() {
		return loadMetadataType( BPMTask.class );
	}

}
