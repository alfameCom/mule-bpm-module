package com.alfame.esb.connectors.bpm.internal;

import com.alfame.esb.connectors.bpm.internal.connection.BPMConnectionProvider;
import com.alfame.esb.connectors.bpm.internal.listener.BPMListener;
import com.alfame.esb.connectors.bpm.internal.processfactory.ProcessFactoryOperations;
import com.zaxxer.hikari.HikariDataSource;

import org.mule.runtime.extension.api.annotation.*;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

import static org.mule.runtime.api.meta.ExternalLibraryType.DEPENDENCY;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import javax.sql.DataSource;

import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.job.service.impl.asyncexecutor.multitenant.ExecutorPerTenantAsyncExecutor;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;


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
@ExternalLib( name = "Flowable Mule 4", type = DEPENDENCY, coordinates = "org.flowable:flowable-mule4:6.4.1", requiredClassName = "org.flowable.mule.MuleSendActivityBehavior")
public class BPMExtension implements Initialisable, Startable, Stoppable, TenantInfoHolder {
	
	private class ProcessDefinitionVisitor extends SimpleFileVisitor<Path> {
		
		protected DeploymentBuilder deploymentBuilder;
		
		ProcessDefinitionVisitor( DeploymentBuilder deploymentBuilder ) {
			this.deploymentBuilder = deploymentBuilder;
		}
	}

	private MultiSchemaMultiTenantProcessEngineConfiguration processEngineConfiguration;
	private ProcessEngine processEngine;
	private Collection<String> tenantIds = new ArrayList<String>();
	private String currentTenantId;
	private DataSource dataSource;
	private ExecutorPerTenantAsyncExecutor asyncExecutor;
	
	@Override
	public void initialise() throws InitialisationException {
		this.processEngineConfiguration = new MultiSchemaMultiTenantProcessEngineConfiguration( this );
		
		this.processEngineConfiguration.setDisableIdmEngine( true );
		
		this.processEngineConfiguration.setEngineName( "Mule" );
		
		this.processEngineConfiguration.setDatabaseType( this.getDatabaseType() );
		this.processEngineConfiguration.setDatabaseSchemaUpdate( ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE );

		this.addTenant( "com.alfame.esb" );
		
		this.asyncExecutor = new ExecutorPerTenantAsyncExecutor( this );
		this.asyncExecutor.setDefaultAsyncJobAcquireWaitTimeInMillis( 50 );
		this.asyncExecutor.setDefaultTimerJobAcquireWaitTimeInMillis( 50 );
		this.asyncExecutor.setMaxAsyncJobsDuePerAcquisition( 10 );
		this.asyncExecutor.setMaxTimerJobsPerAcquisition( 10 );
		this.processEngineConfiguration.setAsyncExecutor( this.asyncExecutor );
		this.processEngineConfiguration.setAsyncExecutorActivate( false );
	}

	@Override
	public void start() throws MuleException {
		this.processEngine = this.processEngineConfiguration.buildProcessEngine();
		
		try {
			DeploymentBuilder deploymentBuilder = this.processEngine.getRepositoryService().createDeployment();
			
			final String processDirectoryPath = "processes/";
			Path processDirectory = Paths.get( ClassLoader.getSystemResource( processDirectoryPath ).toURI() );
			
			String pattern = "*.{bpmn20.xml,bpmn}";

			FileSystem fs = FileSystems.getDefault();
			final PathMatcher matcher = fs.getPathMatcher( "glob:" + pattern );

			ProcessDefinitionVisitor matcherVisitor = new ProcessDefinitionVisitor( deploymentBuilder ) {
			    @Override
			    public FileVisitResult visitFile( Path file, BasicFileAttributes attribs ) {
			        Path name = file.getFileName();
			        if ( matcher.matches( name ) ) {
			        		this.deploymentBuilder.addClasspathResource( processDirectoryPath + processDirectory.relativize(file) );
			        }
			        return FileVisitResult.CONTINUE;
			    }
			};
			Files.walkFileTree( processDirectory, matcherVisitor );
			
			deploymentBuilder.tenantId( "com.alfame.esb" ).deploy();
		} catch (Exception e) {
			new MuleRuntimeException( e );
		}
		
		this.asyncExecutor.start();
	}

	@Override
	public void stop() throws MuleException {
		this.asyncExecutor.shutdown();
		this.processEngine.close();
	}

	@Override
	public void clearCurrentTenantId() {
	}

	@Override
	public Collection<String> getAllTenants() {
		return this.tenantIds;
	}

	@Override
	public String getCurrentTenantId() {
		return this.currentTenantId;
	}

	@Override
	public void setCurrentTenantId(String currentTenantId) {
		this.currentTenantId = currentTenantId;
	}

	public RuntimeService getRuntimeService() {
		return this.processEngine.getRuntimeService();
	}

	public RepositoryService getRepositoryService() {
		return this.processEngine.getRepositoryService();
	}
	
	private DataSource getDataSource() {
		if ( this.dataSource == null ) {
			HikariDataSource dataSource = new HikariDataSource();
			dataSource.setDriverClassName( "org.h2.Driver" );
			dataSource.setUsername( "sa" );
			dataSource.setPassword( "sa" );
			dataSource.setJdbcUrl( "jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1" );
			
			this.dataSource = dataSource;
		}
		
		return this.dataSource;

	}
	
	private String getDatabaseType() {
		return "h2";
	}
	
	private void addTenant( String tenantId ) {
		this.tenantIds.add( tenantId );
		this.processEngineConfiguration.registerTenant( tenantId, this.getDataSource() );
	}

}
