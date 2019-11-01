package com.alfame.esb.connectors.bpm.internal.listener;

import org.flowable.engine.runtime.Execution;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

public class BPMTaskListenerMetadataResolver implements OutputTypeResolver {
	
	private static MetadataType loadMetadataType( Class<?> classType ) {
		return new JavaTypeLoader( ClassLoader.getSystemClassLoader() ).load( classType );
	}
	
	private static final MetadataType METADATA_TYPE = loadMetadataType( Execution.class );

	@Override
	public String getCategoryName() {
		return "BPM";
	}

	@Override
	public MetadataType getOutputType( MetadataContext context, Object key )
			throws MetadataResolvingException, ConnectionException {
		return METADATA_TYPE;
	}
}
