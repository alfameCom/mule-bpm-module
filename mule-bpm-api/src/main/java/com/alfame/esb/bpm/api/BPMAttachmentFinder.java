package com.alfame.esb.bpm.api;

import java.util.List;

abstract public class BPMAttachmentFinder {

    protected String processInstanceId;
    protected String taskId;
    protected String name;

    public BPMAttachmentFinder processInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public BPMAttachmentFinder taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public BPMAttachmentFinder name(String name) {
        this.name = name;
        return this;
    }

    public abstract List<BPMAttachmentInstance> attachments();

    public abstract BPMAttachmentInstance latestAttachment();

}
