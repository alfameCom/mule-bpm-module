package com.alfame.esb.connectors.bpm.internal.operations;

import java.util.Optional;

import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

public class BPMProcessVariableMetadataResolver implements OutputTypeResolver< Object > {
	
	private static Optional<MetadataType> loadMetadataType( String className ) {
		return new JavaTypeLoader( ClassLoader.getSystemClassLoader() ).load( className );
	}

	@Override
	public String getCategoryName() {
		return "BPM";
	}

	@Override
	public MetadataType getOutputType( MetadataContext context, Object key )
			throws MetadataResolvingException, ConnectionException {
		Optional<MetadataType> payloadMetadaType = loadMetadataType( key.getClass().getCanonicalName() );
		if( !payloadMetadaType.isPresent() ) {
			return null;
		}
		return payloadMetadaType.get();
	}
	
}
