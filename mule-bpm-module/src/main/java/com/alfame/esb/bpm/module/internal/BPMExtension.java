package com.alfame.esb.bpm.module.internal;

import com.alfame.esb.bpm.api.*;
import com.alfame.esb.bpm.module.api.config.*;
import com.alfame.esb.bpm.module.internal.connection.BPMConnectionProvider;
import com.alfame.esb.bpm.module.internal.impl.*;
import com.alfame.esb.bpm.module.internal.listener.BPMEventListener;
import com.alfame.esb.bpm.module.internal.listener.BPMTaskListener;
import com.alfame.esb.bpm.module.internal.operations.*;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.engine.FormEngine;
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

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.api.meta.ExternalLibraryType.DEPENDENCY;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "bpm")
@Extension(name = "BPM", vendor = "Alfame Systems")
@Sources({BPMTaskListener.class, BPMEventListener.class})
@ConnectionProviders(BPMConnectionProvider.class)
@Operations({BPMProcessFactoryOperations.class, BPMProcessVariableOperations.class, BPMEventSubscriptionOperations.class, BPMAttachmentOperations.class, BPMProcessInstanceOperations.class})
@SubTypeMapping(baseType = BPMDefinition.class,
        subTypes = {BPMClasspathDefinition.class, BPMStreamDefinition.class})
@SubTypeMapping(baseType = BPMDataSource.class,
        subTypes = {BPMDataSourceReference.class, BPMGenericDataSource.class})
@SubTypeMapping(baseType = BPMAsyncExecutorFactory.class,
        subTypes = {BPMDefaultAsyncExecutorFactory.class})
@SubTypeMapping(baseType = BPMEventSubscriptionFilter.class,
        subTypes = {BPMEventSubscriptionProcessDefinitionFilter.class, BPMEventSubscriptionProcessInstanceFilter.class, BPMEventSubscriptionActivityNameFilter.class, BPMEventSubscriptionEventTypeFilter.class, BPMEventSubscriptionVariableFilter.class})
@SubTypeMapping(baseType = BPMProcessInstanceFilter.class,
        subTypes = {BPMProcessInstanceIdFilter.class, BPMProcessInstanceProcessDefinitionFilter.class, BPMProcessInstanceBusinessKeyLikeFilter.class, BPMProcessInstanceProcessNameLikeFilter.class, BPMProcessInstanceTenantFilter.class, BPMProcessInstanceVariableLikeFilter.class, BPMProcessInstanceStartedAfterFilter.class, BPMProcessInstanceStartedBeforeFilter.class, BPMProcessInstanceFinishedAfterFilter.class, BPMProcessInstanceFinishedBeforeFilter.class, BPMProcessInstanceUnfinishedFilter.class, BPMProcessInstanceFinishedFilter.class, BPMProcessInstanceIncludeProcessVariables.class})
@SubTypeMapping(baseType = BPMAttachmentFilter.class,
        subTypes = {BPMAttachmentNameFilter.class})
@ExternalLib(name = "Flowable Engine", type = DEPENDENCY, coordinates = "org.flowable:flowable-engine:6.6.0", requiredClassName = "org.flowable.engine.RuntimeService")
@ExternalLib(name = "Flowable Form Engine", type = DEPENDENCY, coordinates = "org.flowable:flowable-form-engine:6.6.0", requiredClassName = "org.flowable.form.engine.FormEngine")
@ExternalLib(name = "Flowable Form Engine Configurator", type = DEPENDENCY, coordinates = "org.flowable:flowable-form-engine-configurator:6.6.0", requiredClassName = "org.flowable.form.engine.configurator.FormEngineConfigurator")
@ExternalLib(name = "BPM Flowable Activity", type = DEPENDENCY, coordinates = "com.alfame.esb.bpm:mule-bpm-flowable-activity:${project.version}", requiredClassName = "org.flowable.mule.MuleSendActivityBehavior")
@ExternalLib(name = "BPM Task Queue", type = DEPENDENCY, coordinates = "com.alfame.esb.bpm:mule-bpm-task-queue:${project.version}", requiredClassName = "com.alfame.esb.bpm.taskqueue.BPMTaskQueueFactory")
@ExternalLib(name = "BPM API", type = DEPENDENCY, coordinates = "com.alfame.esb.bpm:mule-bpm-api:${project.version}", requiredClassName = "com.alfame.esb.bpm.api.BPMEnginePool")
public class BPMExtension implements Initialisable, Startable, Stoppable, BPMEngine {

    private static final Logger LOGGER = getLogger(BPMExtension.class);

    @RefName
    private String name;

    @Parameter
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "default")
    @Example("com.alfame.esb")
    @Placement(tab = "General", order = 1)
    @DisplayName("Tenant ID")
    private String tenantId;

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
    @DisplayName("Data source")
    private BPMDataSource dataSource;

    @Parameter
    @Expression(NOT_SUPPORTED)
    @Optional
    @Placement(tab = "General", order = 4)
    @DisplayName("Unique name for process definition deployments")
    private String uniqueDeploymentName;

    @Parameter
    @Expression(NOT_SUPPORTED)
    @Optional(defaultValue = "false")
    @Placement(tab = "General", order = 5)
    @DisplayName("Filter duplicate process definition deployments")
    private boolean duplicateDeploymentFiltering;

    @Parameter
    @Optional
    @Expression(NOT_SUPPORTED)
    @Alias("definitions")
    @Placement(tab = "General", order = 6)
    @DisplayName("Process definitions")
    private List<BPMDefinition> definitions;

    @Parameter
    @Optional
    @NullSafe(defaultImplementingType = BPMDefaultAsyncExecutorFactory.class)
    @Expression(NOT_SUPPORTED)
    @Alias("async-executor-factory")
    @Placement(tab = "General", order = 7)
    @DisplayName("Async executor factory")
    private BPMAsyncExecutorFactory asyncExecutorFactory;

    private BPMStandaloneProcessEngineSchemaConfiguration processEngineConfiguration;
    private ProcessEngine processEngine;

    private BPMStandaloneFormEngineConfigurator formEngineConfigurator;
    private FormEngine formEngine;

    @Override
    public void initialise() throws InitialisationException {
        this.processEngineConfiguration = new BPMStandaloneProcessEngineSchemaConfiguration();

        this.processEngineConfiguration.setEngineName(this.engineName);

        this.processEngineConfiguration.setDefaultTenantValue(this.tenantId);

        this.processEngineConfiguration.setDatabaseType(this.dataSource.getType().getFlowableTypeValue());
        this.processEngineConfiguration.setDataSource(this.dataSource.getDataSource());
        this.processEngineConfiguration.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        this.formEngineConfigurator = new BPMStandaloneFormEngineConfigurator();
        this.processEngineConfiguration.addConfigurator(this.formEngineConfigurator);

        this.processEngineConfiguration.setAsyncExecutorActivate(false);
        if (this.asyncExecutorFactory instanceof BPMDefaultAsyncExecutorFactory) {
            BPMDefaultAsyncExecutorFactory defaultAsyncExecutorFactory = (BPMDefaultAsyncExecutorFactory) this.asyncExecutorFactory;
            this.processEngineConfiguration.setAsyncExecutorTenantId(this.tenantId);
            this.processEngineConfiguration.setAsyncExecutorNumberOfRetries(1);
            this.processEngineConfiguration.setAsyncExecutorCorePoolSize(defaultAsyncExecutorFactory.getMinThreads());
            this.processEngineConfiguration.setAsyncExecutorMaxPoolSize(defaultAsyncExecutorFactory.getMaxThreads());
            this.processEngineConfiguration.setAsyncExecutorDefaultAsyncJobAcquireWaitTime(defaultAsyncExecutorFactory.getDefaultAsyncJobAcquireWaitTimeInMillis());
            this.processEngineConfiguration.setAsyncExecutorDefaultTimerJobAcquireWaitTime(defaultAsyncExecutorFactory.getDefaultTimerJobAcquireWaitTimeInMillis());
            this.processEngineConfiguration.setAsyncExecutorMaxAsyncJobsDuePerAcquisition(defaultAsyncExecutorFactory.getMaxAsyncJobsDuePerAcquisition());
            this.processEngineConfiguration.setAsyncExecutorMaxTimerJobsPerAcquisition(defaultAsyncExecutorFactory.getMaxTimerJobsPerAcquisition());
            this.processEngineConfiguration.setAsyncFailedJobWaitTime(defaultAsyncExecutorFactory.getAsyncFailedJobWaitTimeInSeconds());
            this.processEngineConfiguration.setAsyncExecutor(this.asyncExecutorFactory.createAsyncExecutor());
        } else if (this.asyncExecutorFactory != null) {
            this.processEngineConfiguration.setAsyncExecutor(this.asyncExecutorFactory.createAsyncExecutor());
        }

    }

    @Override
    public void start() throws MuleException {
        this.processEngine = this.processEngineConfiguration.buildProcessEngine();

        this.formEngine = this.formEngineConfigurator.getFormEngine();

        this.deployDefinitions(this.definitions, this.tenantId);

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

    public AsyncExecutor getAsyncExecutor(String tenantId) {
        return this.processEngineConfiguration.getAsyncExecutor();
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
    public String getTenantId() {
        return this.tenantId;
    }

    @Override
    public BPMProcessInstanceBuilder processInstanceBuilder() {
        return new BPMProcessInstanceBuilderImpl(this, this.getRuntimeService(), this.getHistoryService());
    }

    @Override
    public BPMProcessInstanceQueryBuilder processInstanceQueryBuilder() {
        return new BPMProcessInstanceQueryBuilderImpl(this, this.getHistoryService());
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

    @Override
    public void removeAttachment(String attachmentId) {
        this.getTaskService().deleteAttachment(attachmentId);
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

    private void deployDefinitions(Collection<BPMDefinition> definitions, String tenantId) {
        DeploymentBuilder deploymentBuilder = this.processEngine.getRepositoryService().createDeployment();

        if (definitions != null) {
            for (BPMDefinition definition : definitions) {
                try {
                    LOGGER.debug("{} adding {} process definition resource {} for tenant {}", this.name, definition.getType(), definition.getResourceName(), tenantId);
                    definition.addToDeploymentBuilder(deploymentBuilder);
                    LOGGER.debug("{} added {} process definition resource {} for tenant {}", this.name, definition.getType(), definition.getResourceName(), tenantId);
                } catch (FlowableIllegalArgumentException exception) {
                    LOGGER.warn("{} failed to add {} process definition resource {} for tenant {}", this.name, definition.getType(), definition.getResourceName(), tenantId);
                }
            }
        }

        Deployment deployment = null;
        if (this.uniqueDeploymentName != null && !this.uniqueDeploymentName.isEmpty() && this.duplicateDeploymentFiltering) {
            deployment = deploymentBuilder.tenantId(tenantId).name(this.uniqueDeploymentName).enableDuplicateFiltering().deploy();
        } else {
            deployment = deploymentBuilder.tenantId(tenantId).deploy();
        }
        if (deployment != null && deployment.isNew()) {
            if (this.uniqueDeploymentName != null && !this.uniqueDeploymentName.isEmpty()) {
                LOGGER.info("{} made process definition deployment called {} with id {} for tenant {}", this.name, this.uniqueDeploymentName, deployment.getId(), tenantId);
            } else {
                LOGGER.info("{} made process definition deployment with id {} for tenant {}", this.name, deployment.getId(), tenantId);
            }
        }
    }

    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason) {
        try { 
            LOGGER.debug("Deleting process with id: {}, with reason: {}", processInstanceId, deleteReason);
            this.getRuntimeService().deleteProcessInstance(processInstanceId, deleteReason);
        } catch(FlowableObjectNotFoundException exception) {
            LOGGER.error("Failed to delete process with id {}", processInstanceId);
            throw exception;
        } 
    }

    @Override
    public void completeTask(String taskId, String formKey, String outcome, Map<String, Object> variables) {
        FormDefinition formDefinition = this.formEngine.getFormRepositoryService().createFormDefinitionQuery().formDefinitionKey(formKey).singleResult();
        LOGGER.info("Completing task with id {}, form key {} and form definition id {}", taskId, formKey, formDefinition.getId());
        this.processEngine.getTaskService().completeTaskWithForm(taskId, formDefinition.getId(), outcome, variables);
    }
}
