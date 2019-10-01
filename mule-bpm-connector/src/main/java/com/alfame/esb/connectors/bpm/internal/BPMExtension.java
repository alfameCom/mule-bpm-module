package com.alfame.esb.connectors.bpm.internal;

import com.alfame.esb.connectors.bpm.api.config.BPMAsyncExecutor;
import com.alfame.esb.connectors.bpm.api.config.BPMDataSource;
import com.alfame.esb.connectors.bpm.api.config.BPMDefaultDataSource;
import com.alfame.esb.connectors.bpm.api.config.BPMTenant;
import com.alfame.esb.connectors.bpm.internal.connection.BPMConnectionProvider;
import com.alfame.esb.connectors.bpm.internal.listener.BPMListener;
import com.alfame.esb.connectors.bpm.internal.processfactory.ProcessFactoryOperations;
import com.zaxxer.hikari.HikariDataSource;

import org.mule.runtime.extension.api.annotation.*;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.api.meta.ExternalLibraryType.DEPENDENCY;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

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
@Xml( prefix = "bpm" )
@Extension( name = "BPM" )
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
	
	@RefName
	private String name;

	@Parameter
	@Expression( NOT_SUPPORTED )
	@Example( "com.alfame.esb" )
	@Placement( tab = "General", order = 1 )
	@DisplayName( "Default Tenant ID" )
	private String defaultTenantId;

	@Parameter
	@Expression( NOT_SUPPORTED )
	@Optional( defaultValue = "Mule" )
	@Alias( "engine-name" )
	@Placement( tab = "General", order = 2 )
	@DisplayName( "BPM Engine name" )
	private String engineName;

	@Parameter
	@Expression( NOT_SUPPORTED )
	@Alias( "default-data-source" )
	@Placement( tab = "General", order = 3 )
	@DisplayName( "Default data source" )
	private BPMDefaultDataSource defaultDataSource;

	@Parameter
	@Optional
	@Expression( NOT_SUPPORTED )
	@Alias( "default-async-executor" )
	@Placement( tab = "General", order = 4 )
	@DisplayName( "BPM Async executor" )
	private BPMAsyncExecutor defaultAsyncExecutor;
	
	@Parameter
	@Optional
	@Expression( NOT_SUPPORTED )
	@Alias( "tenants" )
	@Placement( tab = "Additional tenants", order = 1 )
	@DisplayName( "Additional tenants" )
	private List<BPMTenant> additionalTenants;

	private MultiSchemaMultiTenantProcessEngineConfiguration processEngineConfiguration;
	private ProcessEngine processEngine;
	private Collection<String> registeredTenantIds = new ArrayList<String>();
	private String currentTenantId;
	private ExecutorPerTenantAsyncExecutor asyncExecutor;
	
	@Override
	public void initialise() throws InitialisationException {
		this.processEngineConfiguration = new MultiSchemaMultiTenantProcessEngineConfiguration( this );
		
		this.processEngineConfiguration.setDisableIdmEngine( true );

		this.processEngineConfiguration.setEngineName( this.engineName );
		
		this.processEngineConfiguration.setDatabaseType( this.defaultDataSource.getType().getValue() );
		this.processEngineConfiguration.setDatabaseSchemaUpdate( ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE );

		this.registerTenant( this.defaultTenantId );
		if ( this.additionalTenants != null ) {
			for ( BPMTenant additionalTenant : this.additionalTenants ) {
				this.registerTenant( additionalTenant.getTenantId() );
			}
		}
		
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
			FileSystem fs = FileSystems.getDefault();

			final String processDirectoryRoot = "processes";
			final String pattern = "*.{bpmn20.xml,bpmn}";
			
			for ( String tenantId : this.registeredTenantIds ) {
				DeploymentBuilder deploymentBuilder = this.processEngine.getRepositoryService().createDeployment();

				final String subTenantId = tenantId.startsWith( this.defaultTenantId ) ? 
						tenantId.substring( this.defaultTenantId.length() ) : null;
				final String subDirectory = subTenantId.replaceAll( "\\.", fs.getSeparator() );

				final String processDirectory = ( subDirectory == null || subDirectory.isEmpty() ) ? 
						processDirectoryRoot : processDirectoryRoot + subDirectory;
				Path processDirectoryPath = Paths.get( ClassLoader.getSystemResource( processDirectory + fs.getSeparator() ).toURI() );
				
				final PathMatcher matcher = fs.getPathMatcher( "glob:" + pattern );

				ProcessDefinitionVisitor matcherVisitor = new ProcessDefinitionVisitor( deploymentBuilder ) {
				    @Override
				    public FileVisitResult visitFile( Path file, BasicFileAttributes attribs ) {
				        Path name = file.getFileName();
				        if ( matcher.matches( name ) ) {
				    			Path relativePath = processDirectoryPath.relativize( file );
				    			String absolutePath = processDirectory + 
				    					( relativePath.getParent() != null ? relativePath.getParent() : "" ) + 
				    					fs.getSeparator() + relativePath.getFileName();
				        		this.deploymentBuilder.addClasspathResource( absolutePath );
				        }
				        return FileVisitResult.CONTINUE;
				    }
				};
				Files.walkFileTree( processDirectoryPath, EnumSet.noneOf( FileVisitOption.class ), 1, matcherVisitor );

				final String absoluteTenantId = ( subTenantId == null || subTenantId.isEmpty() ) ? 
						this.defaultTenantId : this.defaultTenantId + subTenantId;
				deploymentBuilder.tenantId( absoluteTenantId ).deploy();
			}

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
		return this.registeredTenantIds;
	}

	@Override
	public String getCurrentTenantId() {
		return this.currentTenantId;
	}

	@Override
	public void setCurrentTenantId( String currentTenantId ) {
		this.currentTenantId = currentTenantId;
	}

	public RuntimeService getRuntimeService() {
		return this.processEngine.getRuntimeService();
	}

	public RepositoryService getRepositoryService() {
		return this.processEngine.getRepositoryService();
	}
	
	private DataSource buildDataSource( String tenantId ) {
		HikariDataSource dataSource = null;
		
		if ( this.defaultTenantId.equals( tenantId )) {
			dataSource = new HikariDataSource();
			dataSource.setDriverClassName( this.defaultDataSource.getDriverClassName() );
			dataSource.setJdbcUrl( this.defaultDataSource.getJdbcUrl() );
			dataSource.setUsername( this.defaultDataSource.getUsername() );
			dataSource.setPassword( this.defaultDataSource.getPassword() );
		} else {
			for ( BPMTenant tenant : this.additionalTenants ) {
				if ( this.defaultTenantId.equals( tenant.getTenantId() ) ) {
					if ( tenant.getDataSource() != null ) {
						dataSource = new HikariDataSource();
						dataSource.setDriverClassName( tenant.getDataSource().getDriverClassName() );
						dataSource.setJdbcUrl( tenant.getDataSource().getJdbcUrl() );
						dataSource.setUsername( tenant.getDataSource().getUsername() );
						dataSource.setPassword( tenant.getDataSource().getPassword() );
					}
					break;
				}
			}
		}
		
		if ( dataSource == null ) {
			dataSource = new HikariDataSource();
			dataSource.setDriverClassName( this.defaultDataSource.getDriverClassName() );
			dataSource.setJdbcUrl( this.defaultDataSource.getJdbcUrl() );
			dataSource.setUsername( this.defaultDataSource.getUsername() );
			dataSource.setPassword( this.defaultDataSource.getPassword() );
		}
		
		return dataSource;

	}
	
	private void registerTenant( String tenantId ) {
		this.registeredTenantIds.add( tenantId );
		this.processEngineConfiguration.registerTenant( tenantId, this.buildDataSource( tenantId ) );
	}

}
