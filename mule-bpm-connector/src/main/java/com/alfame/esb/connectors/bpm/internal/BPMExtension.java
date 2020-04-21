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
import com.alfame.esb.connectors.bpm.internal.listener.BPMTaskListener;
import com.alfame.esb.connectors.bpm.internal.operations.BPMProcessFactoryOperations;
import com.alfame.esb.connectors.bpm.internal.operations.BPMProcessVariableOperations;
import com.alfame.esb.bpm.api.BPMEngine;
import com.alfame.esb.bpm.api.BPMEnginePool;
import com.alfame.esb.bpm.api.BPMProcessBuilder;

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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.job.service.impl.asyncexecutor.DefaultAsyncJobExecutor;
import org.flowable.job.service.impl.asyncexecutor.multitenant.TenantAwareAsyncExecutor;
import org.flowable.job.service.impl.asyncexecutor.multitenant.TenantAwareAsyncExecutorFactory;
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
@Extension( name = "BPM", vendor = "Alfame Systems" )
@Sources( BPMTaskListener.class )
@Export( classes = { org.flowable.engine.runtime.Execution.class } )
@ConnectionProviders( BPMConnectionProvider.class )
@Operations( { BPMProcessFactoryOperations.class, BPMProcessVariableOperations.class } )
@SubTypeMapping( baseType = BPMDefinition.class, 
				subTypes = { BPMClasspathDefinition.class, BPMStreamDefinition.class } )
@SubTypeMapping( baseType = BPMDataSource.class, 
				subTypes = { BPMDataSourceReference.class, BPMGenericDataSource.class } )
@ExternalLib( name = "Flowable Engine", type = DEPENDENCY, coordinates = "org.flowable:flowable-engine:6.4.1", requiredClassName = "org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl" )
@ExternalLib( name = "Flowable Mule 4", type = DEPENDENCY, coordinates = "org.flowable:flowable-mule4:6.4.1", requiredClassName = "org.flowable.mule.MuleSendActivityBehavior" )
@ExternalLib( name = "BPM Activity Queue", type = DEPENDENCY, coordinates = "com.alfame.esb:bpm-queue:2.0.0-SNAPSHOT", requiredClassName = "com.alfame.esb.bpm.activity.queue.api.BPMActivityQueueFactory" )
@ExternalLib( name = "BPM Java API", type = DEPENDENCY, coordinates = "com.alfame.esb:bpm-java-api:2.0.0-SNAPSHOT", requiredClassName = "com.alfame.esb.bpm.api.BPMEnginePool" )
public class BPMExtension extends BPMEngine implements BPMEngineDetails, Initialisable, Startable, Stoppable, TenantInfoHolder, TenantAwareAsyncExecutorFactory {

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
	private TenantAwareAsyncExecutor asyncExecutor;
	
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
		
		if ( this.defaultAsyncExecutor != null ) {
			try {
				Class<?> asyncExecutorClass = Class.forName( this.defaultAsyncExecutor.getClassName() );
				Constructor<?> asyncExecutorCostructor = null;
				try {
					asyncExecutorCostructor = asyncExecutorClass.getConstructor( TenantInfoHolder.class, TenantAwareAsyncExecutorFactory.class );
					this.asyncExecutor = (TenantAwareAsyncExecutor) asyncExecutorCostructor.newInstance( this, this );
				} catch ( NoSuchMethodException factoriedException ) {
					asyncExecutorCostructor = asyncExecutorClass.getConstructor( TenantInfoHolder.class );
					this.asyncExecutor = (TenantAwareAsyncExecutor) asyncExecutorCostructor.newInstance( this );
				}
			} catch( Exception e ) {
				throw new InitialisationException( e, this );
			}
			this.asyncExecutor.setDefaultAsyncJobAcquireWaitTimeInMillis( this.defaultAsyncExecutor.getDefaultAsyncJobAcquireWaitTimeInMillis() );
			this.asyncExecutor.setDefaultTimerJobAcquireWaitTimeInMillis( this.defaultAsyncExecutor.getDefaultTimerJobAcquireWaitTimeInMillis() );
			this.asyncExecutor.setMaxAsyncJobsDuePerAcquisition( this.defaultAsyncExecutor.getMaxAsyncJobsDuePerAcquisition() );
			this.asyncExecutor.setMaxTimerJobsPerAcquisition( this.defaultAsyncExecutor.getMaxTimerJobsPerAcquisition() );
			this.processEngineConfiguration.setAsyncExecutor( this.asyncExecutor );
		}
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
		
		if ( this.asyncExecutor != null ) {
			this.asyncExecutor.start();
		}
		
		BPMEnginePool.register( this.name, this );
		
		LOGGER.info( this.name + " has been started");
	}

	@Override
	public void stop() throws MuleException {
		LOGGER.info( this.name + " is going to shutdown");
		
		BPMEnginePool.unregister( this.name );
		
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

	@Override
	public AsyncExecutor createAsyncExecutor( String tenantId ) {
		DefaultAsyncJobExecutor tenantExecutor = null;

		if ( this.defaultAsyncExecutor != null ) {
			tenantExecutor = new DefaultAsyncJobExecutor();
			tenantExecutor.setCorePoolSize( this.defaultAsyncExecutor.getMinThreads() );
			tenantExecutor.setMaxPoolSize( this.defaultAsyncExecutor.getMaxThreads() );
		}
		
		return tenantExecutor;
	}
	
	public AsyncExecutor getAsyncExecutor( String tenantId ) {
		return this.processEngineConfiguration.getAsyncExecutor();
	}
	
	public String getName() {
		return this.name;
	}

	public String getDefaultTenantId() {
		return this.defaultTenantId;
	}

	public RuntimeService getRuntimeService() {
		return this.processEngine.getRuntimeService();
	}

	public HistoryService getHistoryService() {
		return this.processEngine.getHistoryService();
	}

	public TaskService getTaskService() {
		return this.processEngine.getTaskService();
	}
	
	public BPMProcessBuilder processInstanceBuilder() {
		return new BPMProcessBuilderImpl( this );
	}

	public Object getVariableInstance( String executionId, String variableName ) {
		return this.getRuntimeService().getVariableInstance( executionId, variableName );
	}

	public void setVariable( String executionId, String variableName, Object content ) {
		this.getRuntimeService().setVariable( executionId, variableName, content );
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
