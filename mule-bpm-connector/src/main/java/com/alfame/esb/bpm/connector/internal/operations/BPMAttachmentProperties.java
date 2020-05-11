package com.alfame.esb.bpm.connector.internal.operations;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class BPMAttachmentProperties extends BPMAttachmentAssociationProperties {

    @Parameter
    @Optional
    private String type;

    @Parameter
    @Optional
    private String attachmentName;

    @Parameter
    @Optional
    private String description;

    @Parameter
    @Optional
    private String url;

    public String getType() {
        return this.type;
    }

    public String getAttachmentName() {
        return this.attachmentName;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUrl() {
        return this.url;
    }

}
