package com.alfame.esb.bpm.utils.flowable;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ProcessDefinitionResources implements ApplicationContextAware {

	private static final Log logger = LogFactory.getLog( ProcessDefinitionResources.class );

	ApplicationContext applicationContext;
	
	List<Resource> resources = new LinkedList<Resource>();

	@Override
	public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ProcessDefinitionResources() {
		super();
	}

	public ProcessDefinitionResources( String ... locationPatterns ) {
		super();

		for ( String locationPattern : locationPatterns) {
			logger.info("Adding resources for location pattern: " + locationPattern);
			addClasspathResource(locationPattern);
		}
	}

	private void addClasspathResource( String locationPattern ) {
		try {
			logger.info("Loading resources for location pattern: " + locationPattern);
			Resource[] newResources = applicationContext.getResources( locationPattern );
			for ( Resource newResource : newResources ) {
				logger.info("Loading resource: " + locationPattern);
				this.resources.add(newResource);
				logger.info("Loaded resource: " + locationPattern);
			}
		} catch ( IOException exception ) {
			logger.warn("Could not load classpath resource: " + locationPattern);
		}
	}
	
	public Resource[] getResources() {
		logger.info("Getting resources");
		return ( Resource[] ) this.resources.toArray();
	}
}
