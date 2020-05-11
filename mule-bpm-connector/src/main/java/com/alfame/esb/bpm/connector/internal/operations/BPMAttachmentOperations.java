package com.alfame.esb.bpm.connector.internal.operations;

import com.alfame.esb.bpm.api.BPMAttachmentInstance;
import com.alfame.esb.bpm.connector.internal.BPMExtension;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.slf4j.Logger;

import java.io.InputStream;

import static org.slf4j.LoggerFactory.getLogger;

public class BPMAttachmentOperations {

    private static final Logger LOGGER = getLogger(BPMAttachmentOperations.class);

    @Alias("attachment-builder")
    @OutputResolver(output = BPMAttachmentOutputMetadataResolver.class)
    public BPMAttachmentInstance attachmentBuilder(
            @Alias("attachment-content") @Content @Summary("Content for attachment") InputStream attachmentContent,
            @ParameterGroup(name = "properties") BPMAttachmentProperties properties,
            @Config BPMExtension engine) {

        BPMAttachmentInstance attachmentInstance = null;

        attachmentInstance = engine.attachmentBuilder()
                .processInstanceId(properties.getProcessInstanceId())
                .taskId(properties.getTaskId())
                .type(properties.getType())
                .name(properties.getAttachmentName())
                .description(properties.getDescription())
                .url(properties.getUrl())
                .type(properties.getType())
                .content(attachmentContent)
                .createAttachment();
        LOGGER.debug("Created attachment " + attachmentInstance.getId());

        return attachmentInstance;
    }

}
