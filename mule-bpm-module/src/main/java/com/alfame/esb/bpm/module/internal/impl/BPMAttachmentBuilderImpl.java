package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMAttachmentBuilder;
import com.alfame.esb.bpm.api.BPMAttachmentInstance;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Attachment;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMAttachmentBuilderImpl extends BPMAttachmentBuilder {

    private static final Logger LOGGER = getLogger(BPMAttachmentBuilderImpl.class);

    private final TaskService taskService;

    public BPMAttachmentBuilderImpl(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public BPMAttachmentInstance createAttachment() {
        Attachment attachment = null;

        if (this.processInstanceId == null && this.taskId == null) {
            throw new IllegalArgumentException("Either process instance id or task id required");
        }

        if (this.content != null && this.url != null) {
            throw new IllegalArgumentException("Only content or URL support");
        } else if (this.content != null) {
            attachment = this.taskService.createAttachment(this.type, this.taskId, this.processInstanceId, this.name, this.description, this.content);
        } else if (this.url != null) {
            attachment = this.taskService.createAttachment(this.type, this.taskId, this.processInstanceId, this.name, this.description, this.url);
        } else {
            throw new IllegalArgumentException("Either content or URL must be set");
        }

        LOGGER.info("Created attachment {} of type {} called {} for process {} and task {}", attachment.getId(), this.type, this.name, this.processInstanceId, this.taskId);

        return new BPMAttachmentInstanceProxy(attachment);
    }

}
