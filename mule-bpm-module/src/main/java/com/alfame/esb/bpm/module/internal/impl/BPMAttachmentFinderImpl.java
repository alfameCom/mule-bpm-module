package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMAttachmentFinder;
import com.alfame.esb.bpm.api.BPMAttachmentInstance;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Attachment;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMAttachmentFinderImpl extends BPMAttachmentFinder {

    private static final Logger LOGGER = getLogger(BPMAttachmentFinderImpl.class);

    private final TaskService taskService;

    public BPMAttachmentFinderImpl(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public List<BPMAttachmentInstance> attachments() {
        List<BPMAttachmentInstance> filteredAttachments = new ArrayList<>();
        List<Attachment> attachments = null;

        if (this.processInstanceId != null) {
            LOGGER.debug("Finding attachments for process {}", this.processInstanceId);
            attachments = this.taskService.getProcessInstanceAttachments(this.processInstanceId);
        } else if (this.taskId != null) {
            LOGGER.debug("Finding attachments for task {}", this.taskId);
            attachments = this.taskService.getTaskAttachments(this.taskId);
        } else {
            throw new IllegalArgumentException("Either process instance id or task id required");
        }

        if (attachments != null) {
            for (Attachment attachment : attachments) {
                if (this.name != null) {
                    if (attachment.getName().equals(this.name)) {
                        filteredAttachments.add(new BPMAttachmentInstanceProxy(attachment));
                        LOGGER.debug("Found attachment {} of type {} called {} for process {} and task {}",
                                attachment.getId(), attachment.getType(), attachment.getName(), attachment.getProcessInstanceId(), attachment.getTaskId());
                    }
                } else {
                    filteredAttachments.add(new BPMAttachmentInstanceProxy(attachment));
                    LOGGER.debug("Found attachment {} of type {} called {} for process {} and task {}",
                            attachment.getId(), attachment.getType(), attachment.getName(), attachment.getProcessInstanceId(), attachment.getTaskId());
                }

            }
        }

        return filteredAttachments;
    }

    @Override
    public BPMAttachmentInstance latestAttachment() {
        List<BPMAttachmentInstance> filteredAttachments = attachments();
        BPMAttachmentInstance latestAttachment = null;

        if (filteredAttachments != null) {
            for (BPMAttachmentInstance filteredAttachment : filteredAttachments) {
                if (latestAttachment == null || latestAttachment.getTime().before(filteredAttachment.getTime())) {
                    latestAttachment = filteredAttachment;
                }
            }
        }

        return latestAttachment;
    }

}
