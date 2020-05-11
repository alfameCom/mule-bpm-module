package com.alfame.esb.bpm.api;

import java.io.InputStream;

abstract public class BPMAttachmentBuilder {

    protected String processInstanceId;
    protected String taskId;
    protected String type;
    protected String name;
    protected String description;
    protected String url;
    protected InputStream content;

    public BPMAttachmentBuilder processInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public BPMAttachmentBuilder taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public BPMAttachmentBuilder type(String type) {
        this.type = type;
        return this;
    }

    public BPMAttachmentBuilder name(String name) {
        this.name = name;
        return this;
    }

    public BPMAttachmentBuilder description(String description) {
        this.description = description;
        return this;
    }

    public BPMAttachmentBuilder url(String url) {
        this.url = url;
        return this;
    }

    public BPMAttachmentBuilder content(InputStream content) {
        this.content = content;
        return this;
    }

    public abstract BPMAttachmentInstance createAttachment();

}
