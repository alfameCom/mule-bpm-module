package com.alfame.esb.bpm.api;

import java.util.Date;

public interface BPMAttachmentAttributes {

    /**
     * @return unique id for this attachment.
     */
    String getId();

    /**
     * @return reference to the task to which this attachment is associated.
     */
    String getTaskId();

    /**
     * @return reference to the process instance to which this attachment is associated.
     */
    String getProcessInstanceId();

    /**
     * @return indication of the type of content that this attachment refers to. Can be mime type or any other indication.
     */
    String getType();

    /**
     * @return user defined short (max 255 chars) name for this attachment.
     */
    String getName();

    /**
     * @return long (max 255 chars) explanation what this attachment is about in context of the task and/or process instance it's linked to.
     */
    String getDescription();

    /**
     * @return the remote URL in case this is remote content.
     */
    String getUrl();

    /**
     * @return timestamp when this attachment was created.
     */
    Date getTime();
}
