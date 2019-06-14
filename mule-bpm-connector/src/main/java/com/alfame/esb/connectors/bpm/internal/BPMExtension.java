package com.alfame.esb.connectors.bpm.internal;

import com.alfame.esb.connectors.bpm.internal.connection.BPMConnectionProvider;
import com.alfame.esb.connectors.bpm.internal.listener.BPMListener;
import com.alfame.esb.connectors.bpm.internal.processfactory.ProcessFactoryOperations;
import org.mule.runtime.api.meta.ExternalLibraryType;
import org.mule.runtime.extension.api.annotation.*;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

import static org.mule.runtime.api.meta.ExternalLibraryType.DEPENDENCY;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "bpm")
@Extension(name = "BPM")
@Sources( BPMListener.class )
@ConnectionProviders( BPMConnectionProvider.class )
@Operations( { ProcessFactoryOperations.class } )
@ExternalLib( name = "Flowable Engine", type = DEPENDENCY, coordinates = "org.flowable:flowable-engine:6.4.1", requiredClassName = "org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl")
public class BPMExtension {

}
