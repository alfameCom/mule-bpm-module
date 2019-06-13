package com.alfame.esb.connectors.bpm.internal;

import com.alfame.esb.connectors.bpm.internal.connection.BPMConnectionProvider;
import com.alfame.esb.connectors.bpm.internal.listener.BPMListener;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "bpm")
@Extension(name = "bpm")
@Sources( BPMListener.class)
@ConnectionProviders( BPMConnectionProvider.class )
public class BPMExtension {

}
