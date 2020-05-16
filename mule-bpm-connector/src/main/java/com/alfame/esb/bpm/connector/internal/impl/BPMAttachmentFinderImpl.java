package com.alfame.esb.bpm.connector.internal.impl;

import com.alfame.esb.bpm.api.BPMAttachmentFinder;
import com.alfame.esb.bpm.api.BPMAttachmentInstance;
import com.alfame.esb.bpm.connector.internal.impl.BPMAttachmentInstanceProxy;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Attachment;

import java.util.ArrayList;
import java.util.List;

public class BPMAttachmentFinderImpl extends BPMAttachmentFinder {

    private final TaskService taskService;

    public BPMAttachmentFinderImpl(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public List<BPMAttachmentInstance> attachments() {
        List<BPMAttachmentInstance> filteredAttachments = new ArrayList<>();
        List<Attachment> attachments = null;

        if (this.processInstanceId != null) {
            attachments = this.taskService.getProcessInstanceAttachments(this.processInstanceId);
        } else if (this.taskId != null) {
            attachments = this.taskService.getTaskAttachments(this.processInstanceId);
        } else {
            throw new IllegalArgumentException("Either process instance id or task id required");
        }

        for (Attachment attachment : attachments) {
            if (this.name != null) {
                if (attachment.getName().equals(this.name)) {
                    filteredAttachments.add(new BPMAttachmentInstanceProxy(attachment));
                }
            } else {
                filteredAttachments.add(new BPMAttachmentInstanceProxy(attachment));
            }

        }

        return filteredAttachments;
    }

    @Override
    public BPMAttachmentInstance latestAttachment() {
        List<BPMAttachmentInstance> filteredAttachments = attachments();
        BPMAttachmentInstance latestAttachment = null;

        for (BPMAttachmentInstance filteredAttachment : filteredAttachments) {
            if (latestAttachment != null && latestAttachment.getTime().before(filteredAttachment.getTime())) {
                latestAttachment = filteredAttachment;
            } else {
                latestAttachment = filteredAttachment;
            }
        }

        return latestAttachment;
    }

}
