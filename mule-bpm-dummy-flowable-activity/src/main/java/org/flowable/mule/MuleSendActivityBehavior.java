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

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.slf4j.Logger;

import java.util.concurrent.RejectedExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jani Haglund
 */
public class MuleSendActivityBehavior extends AbstractBpmnActivityBehavior {

    private static final Logger LOGGER = getLogger(MuleSendActivityBehavior.class);

    private static final long serialVersionUID = 1L;


    @Override
    public void execute(DelegateExecution execution) {
        String processDefinitionKey = execution.getProcessDefinitionId().replaceFirst(":.*", "");
        String processInstanceId = execution.getProcessInstanceId();
        String currentActivityId = execution.getCurrentActivityId();

        LOGGER.warn(">>>>> process definition {}: instance {}: activity {}: entered dummy activity",
                processDefinitionKey, processInstanceId, currentActivityId);
        throw new RejectedExecutionException("Dummy implementation cannot execute activities");
    }


}
