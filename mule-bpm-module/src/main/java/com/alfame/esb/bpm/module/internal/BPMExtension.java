package com.alfame.esb.bpm.module.internal;

import com.alfame.esb.bpm.api.*;
import com.alfame.esb.bpm.module.api.config.*;
import com.alfame.esb.bpm.module.internal.connection.BPMConnectionProvider;
import com.alfame.esb.bpm.module.internal.impl.*;
import com.alfame.esb.bpm.module.internal.listener.BPMTaskListener;
import com.alfame.esb.bpm.module.internal.operations.BPMAttachmentOperations;
import com.alfame.esb.bpm.module.internal.operations.BPMEventSubscriptionOperations;
import com.alfame.esb.bpm.module.internal.operations.BPMProcessFactoryOperations;
import com.alfame.esb.bpm.module.internal.operations.BPMProcessVariableOperations;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.job.service.impl.asyncexecutor.multitenant.TenantAwareAsyncExecutor;
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
import java.io.InputStream;
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
@Operations({BPMProcessFactoryOperations.class, BPMProcessVariableOperations.class, BPMEventSubscriptionOperations.class, BPMAttachmentOperations.class})
@SubTypeMapping(baseType = BPMDefinition.class,
        subTypes = {BPMClasspathDefinition.class, BPMStreamDefinition.class})
@SubTypeMapping(baseType = BPMDataSource.class,
        subTypes = {BPMDataSourceReference.class, BPMGenericDataSource.class})
@SubTypeMapping(baseType = BPMAsyncExecutor.class,
        subTypes = {BPMDefaultAsyncExecutor.class})
@SubTypeMapping(baseType = BPMEventSubscriptionFilter.class,
        subTypes = {BPMEventSubscriptionProcessDefinitionFilter.class, BPMEventSubscriptionProcessInstanceFilter.class, BPMEventSubscriptionEventTypeFilter.class, BPMEventSubscriptionVariableFilter.class})
@SubTypeMapping(baseType = BPMAttachmentFilter.class,
        subTypes = {BPMAttachmentNameFilter.class})
@ExternalLib(name = "Flowable Engine", type = DEPENDENCY, coordinates = "org.flowable:flowable-engine:6.4.1", requiredClassName = "org.flowable.engine.RuntimeService")
@ExternalLib(name = "BPM Flowable Activity", type = DEPENDENCY, coordinates = "com.alfame.esb.bpm:mule-bpm-flowable-activity:2.1.2-SNAPSHOT", requiredClassName = "org.flowable.mule.MuleSendActivityBehavior")
@ExternalLib(name = "BPM Task Queue", type = DEPENDENCY, coordinates = "com.alfame.esb.bpm:mule-bpm-task-queue:2.1.2-SNAPSHOT", requiredClassName = "com.alfame.esb.bpm.taskqueue.BPMTaskQueueFactory")
@ExternalLib(name = "BPM API", type = DEPENDENCY, coordinates = "com.alfame.esb.bpm:mule-bpm-api:2.1.2-SNAPSHOT", requiredClassName = "com.alfame.esb.bpm.api.BPMEnginePool")
public class BPMExtension implements Initialisable, Startable, Stoppable, BPMEngine, TenantInfoHolder {

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
    @NullSafe(defaultImplementingType = com.alfame.esb.bpm.module.api.config.BPMGenericDataSource.class)
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
    private final Collection<String> registeredTenantIds = new ArrayList<>();
    private final ThreadLocal<String> currentTenantId = new ThreadLocal<>();

    @Override
    public void initialise() throws InitialisationException {
        this.processEngineConfiguration = new BPMMultiTenantSchemaConfiguration(this);

        this.processEngineConfiguration.setDisableIdmEngine(true);

        this.processEngineConfiguration.setEngineName(this.engineName);

        this.processEngineConfiguration.setDatabaseType(this.defaultDataSource.getType().getFlowableTypeValue());
        this.processEngineConfiguration.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        if(this.defaultTenantId != null) {
            this.registerTenant(null);
            this.registerTenant(this.defaultTenantId);
        } else {
            this.registerTenant(null);
        }
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

        LOGGER.info("{} has been started", this.name);
    }

    @Override
    public void stop() throws MuleException {
        LOGGER.info("{} is going to shutdown", this.name);

        BPMEnginePool.unregisterInstance(this.name);

        if (this.processEngineConfiguration.getAsyncExecutor() != null) {
            this.processEngineConfiguration.getAsyncExecutor().shutdown();
        }
        this.processEngine.close();
    }

    @Override
    public void clearCurrentTenantId() {
        this.currentTenantId.remove();
    }

    @Override
    public Collection<String> getAllTenants() {
        return this.registeredTenantIds;
    }

    @Override
    public String getCurrentTenantId() {
        String tenantId = this.currentTenantId.get();
        return tenantId;
    }

    @Override
    public void setCurrentTenantId(String tenantId) {
        if(this.registeredTenantIds.contains(tenantId)) {
            this.currentTenantId.set(tenantId);
        } else {
            this.currentTenantId.remove();
        }
    }

    public AsyncExecutor getAsyncExecutor(String tenantId) {
        TenantAwareAsyncExecutor asyncExecutor =
                (TenantAwareAsyncExecutor) this.processEngineConfiguration.getAsyncExecutor();
        return asyncExecutor.getTenantAsyncExecutor(tenantId != null ? tenantId : this.defaultTenantId);
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

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDefaultTenantId() {
        return this.defaultTenantId;
    }

    @Override
    public BPMProcessInstanceBuilder processInstanceBuilder() {
        return new BPMProcessInstanceBuilderImpl(this, this.getRuntimeService(), this.getHistoryService());
    }

    @Override
    public BPMEngineEventSubscriptionBuilder eventSubscriptionBuilder() {
        return new BPMEventSubscriptionBuilderImpl(this, this.getRuntimeService());
    }

    @Override
    public BPMVariableInstance getVariableInstance(String executionId, String variableName) {
        BPMVariableInstanceProxy variableInstanceProxy = null;

        VariableInstance variableInstance = this.getRuntimeService().getVariableInstance(executionId, variableName);
        if (variableInstance != null) {
            variableInstanceProxy = new BPMVariableInstanceProxy(variableInstance);
        }
        return variableInstanceProxy;
    }

    @Override
    public BPMVariableInstance getHistoricVariableInstance(String executionId, String variableName) {
        BPMHistoricVariableInstanceProxy historicVariableInstanceProxy = null;
        HistoricVariableInstanceQuery variableQuery = this.getHistoryService().createHistoricVariableInstanceQuery()
                .executionId(executionId).variableName(variableName);

        HistoricVariableInstance historicVariableInstance = variableQuery.singleResult();
        if (historicVariableInstance != null) {
            historicVariableInstanceProxy = new BPMHistoricVariableInstanceProxy(historicVariableInstance);
        }

        return historicVariableInstanceProxy;
    }

    @Override
    public void setVariable(String executionId, String variableName, Object content) {
        this.getRuntimeService().setVariable(executionId, variableName, content);
    }

    @Override
    public BPMAttachmentBuilder attachmentBuilder() {
        return new BPMAttachmentBuilderImpl(this.getTaskService());
    }

    @Override
    public InputStream getAttachmentContent(String attachmentId) {
        return this.getTaskService().getAttachmentContent(attachmentId);
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

        if (this.defaultTenantId.equals(tenantId) || tenantId == null) {
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
        LOGGER.info("{} has registered tenant {}", this.name, tenantId);
    }

    private void deployDefinitions(Collection<BPMDefinition> definitions, String tenantId) {
        DeploymentBuilder deploymentBuilder = this.processEngine.getRepositoryService().createDeployment();

        if (definitions != null) {
            for (BPMDefinition definition : definitions) {
                try {
                    LOGGER.debug("{} adding {} resource {} for tenant {}", this.name, definition.getType(), definition.getResourceName(), tenantId);
                    definition.addToDeploymentBuilder(deploymentBuilder);
                    LOGGER.debug("{} added {} resource {} for tenant {}", this.name, definition.getType(), definition.getResourceName(), tenantId);
                } catch (FlowableIllegalArgumentException exception) {
                    LOGGER.warn("{} failed to add {} resource {} for tenant {}", this.name, definition.getType(), definition.getResourceName(), tenantId);
                }
            }
        }

        deploymentBuilder.tenantId(tenantId).deploy();
        LOGGER.debug("{} made deployment for tenant {}", this.name, tenantId);
    }

}
