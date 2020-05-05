/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.mule;

import com.alfame.esb.bpm.taskqueue.BPMTaskQueue;
import com.alfame.esb.bpm.taskqueue.BPMTaskQueueFactory;
import com.alfame.esb.bpm.taskqueue.BPMTaskResponse;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.context.Context;
import org.flowable.common.engine.impl.scripting.ScriptingEngines;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.Flowable5Util;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <?xml version="1.0" encoding="UTF-8"?>
 * <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:flowable="http://flowable.org/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" typeLanguage="http://www.w3.org/2001/XMLSchema" expressionLanguage="http://www.w3.org/1999/XPath" targetNamespace="http://www.flowable.org/processdef">
 * <process id="exampleProcess" name="ExampleProcess" isExecutable="true">
 * ...
 * <serviceTask id="exampleMuleTask" name="Test Mule Task" flowable:async="true" flowable:exclusive="false" flowable:type="mule">
 * <extensionElements>
 * <!-- Mandatory -->
 * <flowable:field name="endpointUrl">
 * <flowable:string><![CDATA[bpm://example.task]]></flowable:string>
 * </flowable:field>
 * <!-- Defaults javascript -->
 * <flowable:field name="language">
 * <flowable:string><![CDATA[javascript]]></flowable:string>
 * </flowable:field>
 * <!-- Optional -->
 * <flowable:field name="payloadExpression">
 * <flowable:string><![CDATA[execution]]></flowable:string>
 * </flowable:field>
 * <!-- Optional -->
 * <flowable:field name="resultVariable">
 * <flowable:string><![CDATA[result]]></flowable:string>
 * </flowable:field>
 * </extensionElements>
 * </serviceTask>
 * ...
 * </definitions>
 *
 * @author Esteban Robles Luna
 * @author Jani Haglund
 * @author Toni Harju
 */
public class MuleSendActivityBehavior extends AbstractBpmnActivityBehavior {

    private static final Logger LOGGER = getLogger(MuleSendActivityBehavior.class);

    private static final long serialVersionUID = 1L;

    private Expression endpointUrl;
    private Expression language;
    private Expression payloadExpression;
    private Expression resultVariable;
    private Expression requestTimeout;

    @Override
    public void execute(DelegateExecution execution) {
        String endpointUrlValue = this.getStringFromField(this.endpointUrl, execution);
        String languageValue = this.getStringFromField(this.language, execution, "javascript");
        String payloadExpressionValue = this.getStringFromField(this.payloadExpression, execution);
        String resultVariableValue = this.getStringFromField(this.resultVariable, execution);
        long requestTimeoutValue = Long.parseLong(this.getStringFromField(this.requestTimeout, execution, "300000"));
        String processDefinitionKey = execution.getProcessDefinitionId().replaceFirst(":.*", "");
        String processInstanceId = execution.getProcessInstanceId();
        String currentActivityId = execution.getCurrentActivityId();
        long startTime = System.currentTimeMillis();

        LOGGER.info(">>>>> process definition {}: instance {}: activity {}: execution starting with time out {} ms", processDefinitionKey, processInstanceId, currentActivityId, requestTimeoutValue);
        LOGGER.trace(">>>>> process definition {}: instance {}: activity {}: execution with language {}", processDefinitionKey, processInstanceId, currentActivityId, languageValue);
        LOGGER.trace(">>>>> process definition {}: instance {}: activity {}: execution with payload expression {}", processDefinitionKey, processInstanceId, currentActivityId, payloadExpressionValue);
        LOGGER.trace(">>>>> process definition {}: instance {}: activity {}: execution with tenant {}", processDefinitionKey, processInstanceId, currentActivityId, execution.getTenantId());

        boolean isFlowable5Execution = false;
        Object attributes = execution;
        Object payload = null;
        if (payloadExpressionValue != null && !payloadExpressionValue.isEmpty()) {
            if ((Context.getCommandContext() != null && Flowable5Util.isFlowable5ProcessDefinitionId(Context.getCommandContext(), execution.getProcessDefinitionId())) ||
                    (Context.getCommandContext() == null && Flowable5Util.getFlowable5CompatibilityHandler() != null)) {
                payload = Flowable5Util.getFlowable5CompatibilityHandler().getScriptingEngineValue(payloadExpressionValue, languageValue, execution);
                isFlowable5Execution = true;

            } else {
                ScriptingEngines scriptingEngines = CommandContextUtil.getProcessEngineConfiguration().getScriptingEngines();
                LOGGER.trace(">>>>> process definition {}: instance {}: activity {}: execution with scripting engined {}", processDefinitionKey, processInstanceId, currentActivityId, scriptingEngines);
                payload = scriptingEngines.evaluate(payloadExpressionValue, languageValue, execution);
            }
        }

        if (endpointUrlValue != null && !endpointUrlValue.isEmpty()) {
            BPMTaskQueue queue = BPMTaskQueueFactory.getInstance(endpointUrlValue);
            if (queue != null) {
                try {
                    MuleSendActivityTask task = new MuleSendActivityTask(payload, execution.getId(), execution);
                    LOGGER.trace(">>>>> process definition {}: instance {}: activity {}: execution being published to task queue {}", processDefinitionKey, processInstanceId, currentActivityId, endpointUrlValue);
                    queue.publish(task);
                    BPMTaskResponse response = task.waitForResponse(requestTimeoutValue, TimeUnit.MILLISECONDS);
                    if (response == null) {
                        LOGGER.error("<<<<< process definition {}: instance {}: activity {}: execution ended without response in {} ms", processDefinitionKey, processInstanceId, currentActivityId, System.currentTimeMillis() - startTime);
                        throw new RuntimeException("No response from BPM Queue: " + endpointUrlValue);
                    }
                    if (response.getError() != null) {
                        LOGGER.error("<<<<< process definition {}: instance {}: activity {}: execution ended with exception {} in {} ms", processDefinitionKey, processInstanceId, currentActivityId, response.getError(), System.currentTimeMillis() - startTime);
                        throw new RuntimeException(response.getError());
                    }
                    LOGGER.trace("<<<<< process definition {}: instance {}: activity {}: execution ended with response {}", processDefinitionKey, processInstanceId, currentActivityId, response);

                    if (response.getVariablesToUpdate() != null) {
                        for (Map.Entry<String, Object> variableToUpdate : response.getVariablesToUpdate().entrySet()) {
                            LOGGER.debug("<<<<< process definition {}: instance {}: activity {}: execution setting variable {} = {}", processDefinitionKey, processInstanceId, currentActivityId, variableToUpdate.getKey(), variableToUpdate.getValue());
                            execution.setVariable(variableToUpdate.getKey(), variableToUpdate.getValue());
                        }
                    }

                    if (response.getVariablesToRemove() != null) {
                        for (String variableToRemove : response.getVariablesToRemove()) {
                            LOGGER.debug("<<<<< process definition {}: instance {}: activity {}: execution removing variable {}", processDefinitionKey, processInstanceId, currentActivityId, variableToRemove);
                            execution.removeVariable(variableToRemove);
                        }
                    }

                    if (resultVariableValue != null && response.getValue() != null) {
                        LOGGER.debug("<<<<< process definition {}: instance {}: activity {}: execution setting variable {} = {}", processDefinitionKey, processInstanceId, currentActivityId, resultVariableValue, response.getValue());
                        execution.setVariable(resultVariableValue, response.getValue());
                    }
                } catch (InterruptedException exception) {
                    LOGGER.warn("<<<<< process definition {}: instance {}: activity {}: execution was interrupted by exception {} in {} ms", processDefinitionKey, processInstanceId, currentActivityId, exception, System.currentTimeMillis() - startTime);
                    throw new RuntimeException(exception);
                } catch (ExecutionException exception) {
                    LOGGER.error("<<<<< process definition {}: instance {}: activity {}: execution caught exception {} in {} ms", processDefinitionKey, processInstanceId, currentActivityId, exception, System.currentTimeMillis() - startTime);
                    throw new RuntimeException(exception);
                } catch (TimeoutException exception) {
                    LOGGER.warn("<<<<< process definition {}: instance {}: activity {}: execution timed out with exception {} in {} ms", processDefinitionKey, processInstanceId, currentActivityId, exception, System.currentTimeMillis() - startTime);
                    throw new RuntimeException(exception);
                }
            } else {
                LOGGER.error("<<<<< process definition {}: instance {}: activity {}: execution cannot find task queue {} in {} ms", processDefinitionKey, processInstanceId, currentActivityId, endpointUrlValue, System.currentTimeMillis() - startTime);
                throw new RuntimeException("Cannot find BPM Queue: " + endpointUrlValue);
            }

        } else {
            LOGGER.error("<<<<< process definition {}: instance {}: activity {}: execution cannot find task queue URL in {} ms", processDefinitionKey, processInstanceId, currentActivityId, endpointUrlValue, System.currentTimeMillis() - startTime);
            throw new RuntimeException("Missing endpoint URL, cannot queue Mule Task!");
        }

        if (isFlowable5Execution) {
            Flowable5Util.getFlowable5CompatibilityHandler().leaveExecution(execution);
        } else {
            this.leave(execution);
        }
        LOGGER.info("<<<<< process definition {}: instance {}: activity {}: executed in {} ms", processDefinitionKey, processInstanceId, currentActivityId, System.currentTimeMillis() - startTime);
    }

    protected String getStringFromField(Expression expression, DelegateExecution execution) {
        return getStringFromField(expression, execution, null);
    }

    protected String getStringFromField(Expression expression, DelegateExecution execution, String defaultValue) {
        if (expression != null) {
            Object value = expression.getValue(execution);
            if (value != null) {
                return value.toString();
            }
        }
        return defaultValue;
    }

    public Expression getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(Expression endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public Expression getPayloadExpression() {
        return payloadExpression;
    }

    public void setPayloadExpression(Expression payloadExpression) {
        this.payloadExpression = payloadExpression;
    }

    public Expression getResultVariable() {
        return resultVariable;
    }

    public void setResultVariable(Expression resultVariable) {
        this.resultVariable = resultVariable;
    }

    public Expression getLanguage() {
        return language;
    }

    public void setLanguage(Expression language) {
        this.language = language;
    }

    public Expression getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Expression requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

}
