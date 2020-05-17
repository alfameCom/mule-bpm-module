package com.alfame.esb.bpm.module.internal.impl;

import com.alfame.esb.bpm.api.BPMAttachmentInstance;
import org.flowable.engine.task.Attachment;

import java.util.Date;

public class BPMAttachmentInstanceProxy implements BPMAttachmentInstance {

    private final Attachment attachment;

    public BPMAttachmentInstanceProxy(Attachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public String getId() {
        return this.attachment.getId();
    }

    @Override
    public String getTaskId() {
        return this.attachment.getTaskId();
    }

    @Override
    public String getProcessInstanceId() {
        return this.attachment.getProcessInstanceId();
    }

    @Override
    public String getType() {
        return this.attachment.getType();
    }

    @Override
    public String getName() {
        return this.attachment.getName();
    }

    @Override
    public String getDescription() {
        return this.attachment.getDescription();
    }

    @Override
    public String getUrl() {
        return this.attachment.getUrl();
    }

    @Override
    public Date getTime() {
        return this.attachment.getTime();
    }
}
