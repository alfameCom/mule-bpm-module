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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.context.Context;
import org.flowable.common.engine.impl.scripting.ScriptingEngines;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.Flowable5Util;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    private static final Log logger = LogFactory.getLog(MuleSendActivityBehavior.class);

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
        long startTime = System.currentTimeMillis();

        logger.info(">>>>> " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": starting with timeout " + requestTimeoutValue + " ms");
        logger.trace(">>>>> " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": " + languageValue);
        logger.trace(">>>>> " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": " + payloadExpressionValue);
        logger.trace(">>>>> " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": " + execution.getTenantId());

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
                logger.trace(">>>>> " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": " + scriptingEngines);
                payload = scriptingEngines.evaluate(payloadExpressionValue, languageValue, execution);
            }
        }

        if (endpointUrlValue != null && !endpointUrlValue.isEmpty()) {
            BPMTaskQueue queue = BPMTaskQueueFactory.getInstance(endpointUrlValue);
            if (queue != null) {
                try {
                    MuleSendActivityTask task = new MuleSendActivityTask(payload, execution.getId(), execution);
                    logger.trace(">>>>> " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": " + endpointUrlValue);
                    queue.publish(task);
                    BPMTaskResponse response = task.waitForResponse(requestTimeoutValue, TimeUnit.MILLISECONDS);
                    if (response == null) {
                        throw new RuntimeException("No response from BPM Queue: " + endpointUrlValue);
                    }

                    logger.trace("<<<<< " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": " + response);
                    if (resultVariableValue != null && response.getValue() != null) {

                        for (Map.Entry<String, Object> variableToUpdate : response.getVariablesToUpdate().entrySet()) {
                            logger.debug("<<<<< " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": variable " + variableToUpdate.getKey() + "=" + variableToUpdate.getValue());
                            execution.setVariable(variableToUpdate.getKey(), variableToUpdate.getValue());
                        }

                        for (String variableToRemove : response.getVariablesToRemove()) {
                            logger.debug("<<<<< " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": removing variable " + variableToRemove);
                            execution.removeVariable(variableToRemove);
                        }

                        logger.trace("<<<<< " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": variable " + resultVariableValue + "=" + response.getValue());
                        execution.setVariable(resultVariableValue, response.getValue());
                    }
                    if (response.getThrowable() != null) {
                        logger.debug("<<<<< " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId(), response.getThrowable());
                        throw new RuntimeException(response.getThrowable());
                    }
                } catch (InterruptedException e) {
                    logger.debug("<<<<< " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId(), e);
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    logger.debug("<<<<< " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId(), e);
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    logger.debug("<<<<< " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId(), e);
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("Cannot find BPM Queue: " + endpointUrlValue);
            }

        } else {
            throw new RuntimeException("Missing endpoint URL, cannot queue Mule Task!");
        }

        if (isFlowable5Execution) {
            Flowable5Util.getFlowable5CompatibilityHandler().leaveExecution(execution);
        } else {
            this.leave(execution);
        }
        logger.info("<<<<< " + execution.getProcessDefinitionId() + " / " + execution.getCurrentActivityId() + ": " + execution.getProcessInstanceId() + ": done in " + (System.currentTimeMillis() - startTime) + " ms");
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
