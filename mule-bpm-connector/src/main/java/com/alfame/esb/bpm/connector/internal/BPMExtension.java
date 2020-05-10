package com.alfame.esb.bpm.connector.internal;

import com.alfame.esb.bpm.api.*;
import com.alfame.esb.bpm.connector.api.config.*;
import com.alfame.esb.bpm.connector.internal.connection.BPMConnectionProvider;
import com.alfame.esb.bpm.connector.internal.listener.BPMTaskListener;
import com.alfame.esb.bpm.connector.internal.operations.BPMEventSubscriptionOperations;
import com.alfame.esb.bpm.connector.internal.operations.BPMProcessFactoryOperations;
import com.alfame.esb.bpm.connector.internal.operations.BPMProcessVariableOperations;
import com.alfame.esb.bpm.connector.internal.proxies.BPMProcessHistoricVariableInstanceProxy;
import com.alfame.esb.bpm.connector.internal.proxies.BPMProcessVariableInstanceProxy;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.history.HistoricVariableInstanceQuery;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.extension.api.annotation.*;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.api.meta.ExternalLibraryType.DEPENDENCY;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "bpm")
@Extension(name = "BPM", vendor = "Alfame Systems")
@Sources(BPMTaskListener.class)
@ConnectionProviders(BPMConnectionProvider.class)
@Operations({BPMProcessFactoryOperations.class, BPMProcessVariableOperations.class, BPMEventSubscriptionOperations.class})
@SubTypeMapping(baseType = BPMDefinition.class,
        subTypes = {BPMClasspathDefinition.class, BPMStreamDefinition.class})
@SubTypeMapping(baseType = BPMDataSource.class,
        subTypes = {BPMDataSourceReference.class, BPMGenericDataSource.class})
@SubTypeMapping(baseType = BPMAsyncExecutor.class,
        subTypes = {BPMDefaultAsyncExecutor.class})
@SubTypeMapping(baseType = BPMEventSubscriptionFilter.class,
        subTypes = {BPMEventSubscriptionProcessDefinitionFilter.class, BPMEventSubscriptionProcessInstanceFilter.class, BPMEventSubscriptionEventTypeFilter.class, BPMEventSubscriptionVariableFilter.class})
@ExternalLib(name = "Flowable Engine", type = DEPENDENCY, coordinates = "org.flowable:flowable-engine:6.4.1", requiredClassName = "org.flowable.engine.RuntimeService")
@ExternalLib(name = "BPM Flowable Activity", type = DEPENDENCY, coordinates = "com.alfame.esb.bpm:mule-bpm-flowable-activity:2.1.1-SNAPSHOT", requiredClassName = "org.flowable.mule.MuleSendActivityBehavior")
@ExternalLib(name = "BPM Task Queue", type = DEPENDENCY, coordinates = "com.alfame.esb.bpm:mule-bpm-task-queue:2.1.1-SNAPSHOT", requiredClassName = "com.alfame.esb.bpm.taskqueue.BPMTaskQueueFactory")
@ExternalLib(name = "BPM API", type = DEPENDENCY, coordinates = "com.alfame.esb.bpm:mule-bpm-api:2.1.1-SNAPSHOT", requiredClassName = "com.alfame.esb.bpm.api.BPMEnginePool")
public class BPMExtension extends BPMEngine implements Initialisable, Startable, Stoppable, TenantInfoHolder {

    private static final Logger LOGGER = getLogger(BPMExtension.class);

    @RefName
    private String name;

    @Parameter
    @Expression(NOT_SUPPORTED)
    @Example("com.alfame.esb")
    @Placement(tab = "General", order = 1)
    @DisplayName("Default Tenant ID")
    private String defaultTenantId;

    @Parameter
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "Mule")
    @Alias("engine-name")
    @Placement(tab = "General", order = 2)
    @DisplayName("BPM Engine name")
    private String engineName;

    @Parameter
    @Expression(NOT_SUPPORTED)
    @Optional
    @NullSafe(defaultImplementingType = com.alfame.esb.bpm.connector.api.config.BPMGenericDataSource.class)
    @Placement(tab = "General", order = 3)
    @DisplayName("Default data source")
    private BPMDataSource defaultDataSource;

    @Parameter
    @Optional
    @Expression(NOT_SUPPORTED)
    @Alias("definitions")
    @Placement(tab = "General", order = 4)
    @DisplayName("Process definitions")
    private List<BPMDefinition> defaultDefinitions;

    @Parameter
    @Optional
    @NullSafe(defaultImplementingType = BPMDefaultAsyncExecutor.class)
    @Expression(NOT_SUPPORTED)
    @Alias("async-executor")
    @Placement(tab = "General", order = 5)
    @DisplayName("BPM Async executor")
    private BPMAsyncExecutor asyncExecutor;

    @Parameter
    @Optional
    @Expression(NOT_SUPPORTED)
    @Alias("additional-tenants")
    @Placement(tab = "Additional tenants", order = 1)
    @DisplayName("Additional tenants")
    private List<BPMTenant> additionalTenants;

    private MultiSchemaMultiTenantProcessEngineConfiguration processEngineConfiguration;
    private ProcessEngine processEngine;
    private Collection<String> registeredTenantIds = new ArrayList<>();
    private String currentTenantId;

    @Override
    public void initialise() throws InitialisationException {
        this.processEngineConfiguration = new MultiSchemaMultiTenantProcessEngineConfiguration(this);

        this.processEngineConfiguration.setDisableIdmEngine(true);

        this.processEngineConfiguration.setEngineName(this.engineName);

        this.processEngineConfiguration.setDatabaseType(this.defaultDataSource.getType().getValue());
        this.processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        this.registerTenant(this.defaultTenantId);
        if (this.additionalTenants != null) {
            for (BPMTenant additionalTenant : this.additionalTenants) {
                this.registerTenant(additionalTenant.getTenantId());
            }
        }

        if (this.asyncExecutor != null) {
            AsyncExecutor asyncExecutor = this.asyncExecutor.createAsyncExecutor(this, this);
            this.processEngineConfiguration.setAsyncExecutor(asyncExecutor);
        }
        this.processEngineConfiguration.setAsyncExecutorActivate(false);
    }

    @Override
    public void start() throws MuleException {
        this.processEngine = this.processEngineConfiguration.buildProcessEngine();

        this.deployDefinitions(this.defaultDefinitions, this.defaultTenantId);
        if (this.additionalTenants != null) {
            for (BPMTenant additionalTenant : this.additionalTenants) {
                this.deployDefinitions(additionalTenant.getDefinitions(), additionalTenant.getTenantId());
            }
        }

        if (this.processEngineConfiguration.getAsyncExecutor() != null) {
            this.processEngineConfiguration.getAsyncExecutor().start();
        }

        BPMEnginePool.registerInstance(this.name, this);

        LOGGER.info(this.name + " has been started");
    }

    @Override
    public void stop() throws MuleException {
        LOGGER.info(this.name + " is going to shutdown");

        BPMEnginePool.unregisterInstance(this.name);

        if (this.processEngineConfiguration.getAsyncExecutor() != null) {
            this.processEngineConfiguration.getAsyncExecutor().shutdown();
        }
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
    public void setCurrentTenantId(String currentTenantId) {
        this.currentTenantId = currentTenantId;
    }

    public AsyncExecutor getAsyncExecutor(String tenantId) {
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

    public BPMProcessInstanceBuilder processInstanceBuilder() {
        return new BPMProcessInstanceBuilderImpl(this, this.getRuntimeService());
    }

    public BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder() {
        return new BPMEngineEventSubscriptionBuilderImpl(this, this.getRuntimeService());
    }

    public BPMVariableInstance getVariableInstance(String executionId, String variableName) {
        BPMProcessVariableInstanceProxy variableInstanceProxy = null;

        VariableInstance variableInstance = this.getRuntimeService().getVariableInstance(executionId, variableName);
        if (variableInstance != null) {
            variableInstanceProxy = new BPMProcessVariableInstanceProxy(variableInstance);
        }
        return variableInstanceProxy;
    }

    public BPMVariableInstance getHistoricVariableInstance(String executionId, String variableName) {
        BPMProcessHistoricVariableInstanceProxy historicVariableInstanceProxy = null;
        HistoricVariableInstanceQuery variableQuery = this.getHistoryService().createHistoricVariableInstanceQuery()
                .executionId(executionId).variableName(variableName);

        HistoricVariableInstance historicVariableInstance = variableQuery.singleResult();
        if (historicVariableInstance != null) {
            historicVariableInstanceProxy = new BPMProcessHistoricVariableInstanceProxy(historicVariableInstance);
        }

        return historicVariableInstanceProxy;
    }

    public void setVariable(String executionId, String variableName, Object content) {
        this.getRuntimeService().setVariable(executionId, variableName, content);
    }

    public void triggerSignal(String processInstanceId, String signalName) {
        Execution execution = this.getRuntimeService().createExecutionQuery()
                .signalEventSubscriptionName(signalName).processInstanceId(processInstanceId)
                .singleResult();
        if (execution != null) {
            this.getRuntimeService().trigger(execution.getId());
        } else {
            throw new IllegalArgumentException("No such signal listener");
        }
    }

    private DataSource buildDataSource(String tenantId) {
        DataSource dataSource = null;

        if (this.defaultTenantId.equals(tenantId)) {
            dataSource = this.defaultDataSource.getDataSource();
        } else {
            for (BPMTenant tenant : this.additionalTenants) {
                if (this.defaultTenantId.equals(tenant.getTenantId())) {
                    if (tenant.getDataSource() != null) {
                        dataSource = this.defaultDataSource.getDataSource();
                    }
                    break;
                }
            }
        }

        if (dataSource == null) {
            dataSource = this.defaultDataSource.getDataSource();
        }

        return dataSource;

    }

    private void registerTenant(String tenantId) {
        this.registeredTenantIds.add(tenantId);
        this.processEngineConfiguration.registerTenant(tenantId, this.buildDataSource(tenantId));
        LOGGER.info(this.name + " has registered tenant " + tenantId);
    }

    private void deployDefinitions(Collection<BPMDefinition> definitions, String tenantId) {
        DeploymentBuilder deploymentBuilder = this.processEngine.getRepositoryService().createDeployment();

        if (definitions != null) {
            for (BPMDefinition definition : definitions) {
                try {
                    LOGGER.debug(this.name + " adding " + definition.getType() + " resource " + definition.getResourceName() + " for tenant " + tenantId);
                    definition.addToDeploymentBuilder(deploymentBuilder);
                    LOGGER.debug(this.name + " added " + definition.getType() + " resource " + definition.getResourceName() + " for tenant " + tenantId);
                } catch (FlowableIllegalArgumentException exception) {
                    LOGGER.warn(this.name + " failed to add " + definition.getType() + " resource " + definition.getResourceName() + " for tenant " + tenantId);
                }
            }
        }

        deploymentBuilder.tenantId(tenantId).deploy();
        LOGGER.debug(this.name + " made deployment for tenant " + tenantId);
    }

}
