package com.alfame.esb.bpm.utils.flowable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ProcessDefinitionResources implements ApplicationContextAware {

	private static final Log logger = LogFactory.getLog( ProcessDefinitionResources.class );

	private ApplicationContext applicationContext;

	@Autowired
	public ResourcePatternResolver resourcePatternResolver;

	private String pattern;
	private Resource[] resources;

	public ProcessDefinitionResources( String pattern ) {
		this.pattern = pattern;

		try {
			this.resources = resourcePatternResolver.getResources( this.pattern );
			logger.info( ">>>>> " + resources.length );
		} catch( IOException e ) {
			logger.error( e.getMessage() );
		}

	}

	public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public Resource[] getResources() {
		return this.resources;
	}

}
