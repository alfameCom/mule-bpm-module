package com.alfame.esb.connectors.bpm.internal.processfactory;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.model.MetadataFormat;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.api.model.ObjectType;
import org.mule.runtime.api.metadata.resolving.OutputStaticTypeResolver;

public class ProcessFactoryMetadataResolver extends OutputStaticTypeResolver {

	private static final ObjectType OBJECT_TYPE = BaseTypeBuilder.create( MetadataFormat.JAVA ).objectType().build();

	@Override
	public String getCategoryName() {
		return "BPM";
	}

	@Override
	public MetadataType getStaticMetadata() {
		return OBJECT_TYPE;
	}
}
