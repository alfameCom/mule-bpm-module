package com.alfame.esb.connectors.bpm.internal;

import com.alfame.esb.connectors.bpm.api.config.BPMAsyncExecutor;
import com.alfame.esb.connectors.bpm.api.config.BPMClasspathDefinition;
import com.alfame.esb.connectors.bpm.api.config.BPMDataSource;
import com.alfame.esb.connectors.bpm.api.config.BPMDataSourceReference;
import com.alfame.esb.connectors.bpm.api.config.BPMDefinition;
import com.alfame.esb.connectors.bpm.api.config.BPMGenericDataSource;
import com.alfame.esb.connectors.bpm.api.config.BPMStreamDefinition;
import com.alfame.esb.connectors.bpm.api.config.BPMTenant;
import com.alfame.esb.connectors.bpm.internal.connection.BPMConnectionProvider;
import com.alfame.esb.connectors.bpm.internal.listener.BPMListener;
import com.alfame.esb.connectors.bpm.internal.processfactory.ProcessFactoryOperations;
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
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.job.service.impl.asyncexecutor.multitenant.ExecutorPerTenantAsyncExecutor;
import org.mule.runtime.api.exception.MuleException;
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
@SubTypeMapping( baseType = BPMDefinition.class, 
				subTypes = { BPMClasspathDefinition.class, BPMStreamDefinition.class } )
@SubTypeMapping( baseType = BPMDataSource.class, 
				subTypes = { BPMDataSourceReference.class, BPMGenericDataSource.class } )
@ExternalLib( name = "Flowable Engine", type = DEPENDENCY, coordinates = "org.flowable:flowable-engine:6.4.1", requiredClassName = "org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl")
@ExternalLib( name = "Flowable Mule 4", type = DEPENDENCY, coordinates = "org.flowable:flowable-mule4:6.4.1", requiredClassName = "org.flowable.mule.MuleSendActivityBehavior")
public class BPMExtension implements Initialisable, Startable, Stoppable, TenantInfoHolder {

	private static final Logger LOGGER = getLogger( BPMExtension.class );
	
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
	@Optional
	@Placement( tab = "General", order = 3 )
	@DisplayName( "Default data source" )
	private BPMDataSource defaultDataSource;

	@Parameter
	@Optional
	@Expression( NOT_SUPPORTED )
	@Alias( "definitions" )
	@Placement( tab = "General", order = 4 )
	@DisplayName( "Process definitions" )
	private List<BPMDefinition> defaultDefinitions;

	@Parameter
	@Optional
	@Expression( NOT_SUPPORTED )
	@Alias( "default-async-executor" )
	@Placement( tab = "General", order = 5 )
	@DisplayName( "BPM Async executor" )
	private BPMAsyncExecutor defaultAsyncExecutor;

	@Parameter
	@Optional
	@Expression( NOT_SUPPORTED )
	@Alias( "additional-tenants" )
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
		
		this.deployDefinitions( this.defaultDefinitions, this.defaultTenantId );
		if ( this.additionalTenants != null ) {
			for ( BPMTenant additionalTenant : this.additionalTenants ) {
				this.deployDefinitions( additionalTenant.getDefinitions(), additionalTenant.getTenantId() );
			}
		}
		
		this.asyncExecutor.start();
		
		LOGGER.info( this.name + " has been started");
	}

	@Override
	public void stop() throws MuleException {
		LOGGER.info( this.name + " is going to shutdown");
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
		DataSource dataSource = null;
		
		if ( this.defaultTenantId.equals( tenantId )) {
			dataSource = this.defaultDataSource.getDataSource();
		} else {
			for ( BPMTenant tenant : this.additionalTenants ) {
				if ( this.defaultTenantId.equals( tenant.getTenantId() ) ) {
					if ( tenant.getDataSource() != null ) {
						dataSource = this.defaultDataSource.getDataSource();
					}
					break;
				}
			}
		}
		
		if ( dataSource == null ) {
			dataSource = this.defaultDataSource.getDataSource();
		}

		return dataSource;

	}
	
	private void registerTenant( String tenantId ) {
		this.registeredTenantIds.add( tenantId );
		this.processEngineConfiguration.registerTenant( tenantId, this.buildDataSource( tenantId ) );
		LOGGER.info( this.name + " has registered tenant " + tenantId );
	}
	
	private void deployDefinitions( Collection<BPMDefinition> definitions, String tenantId ) {
		DeploymentBuilder deploymentBuilder = this.processEngine.getRepositoryService().createDeployment();

		if ( definitions != null ) {
			for ( BPMDefinition definition : definitions ) {
				try {
					LOGGER.debug( this.name + " adding " + definition.getType() + " resource " + definition.getResourceName() + " for tenant " + tenantId );
					definition.addToDeploymentBuilder( deploymentBuilder );
					LOGGER.debug( this.name + " added " + definition.getType() + " resource " + definition.getResourceName() + " for tenant " + tenantId );
				} catch ( FlowableIllegalArgumentException exception ) {
					LOGGER.warn( this.name + " failed to add " + definition.getType() + " resource " + definition.getResourceName() + " for tenant " + tenantId );
				}
			}
		}
		
		deploymentBuilder.tenantId( tenantId ).deploy();
		LOGGER.debug( this.name + " made deployment for tenant " + tenantId );
	}

}
