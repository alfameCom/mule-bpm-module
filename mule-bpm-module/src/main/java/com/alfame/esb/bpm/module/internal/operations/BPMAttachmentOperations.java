package com.alfame.esb.bpm.module.internal.operations;

import com.alfame.esb.bpm.api.BPMAttachmentAttributes;
import com.alfame.esb.bpm.api.BPMAttachmentFinder;
import com.alfame.esb.bpm.api.BPMAttachmentInstance;
import com.alfame.esb.bpm.module.api.config.BPMAttachmentFilter;
import com.alfame.esb.bpm.module.api.config.BPMAttachmentNameFilter;
import com.alfame.esb.bpm.module.internal.impl.BPMAttachmentFinderImpl;
import com.alfame.esb.bpm.module.internal.BPMExtension;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.List;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.slf4j.LoggerFactory.getLogger;

public class BPMAttachmentOperations {

    private static final Logger LOGGER = getLogger(BPMAttachmentOperations.class);

    @Alias("create-attachment")
    @OutputResolver(output = BPMAttachmentOutputMetadataResolver.class)
    public BPMAttachmentInstance createAttachment(
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

    @Alias("get-latest-attachment")
    @MediaType(value = ANY, strict = false)
    @OutputResolver(attributes = BPMAttachmentAttributesMetadataResolver.class)
    public Result<InputStream, BPMAttachmentAttributes> getLatestAttachment(
            @Config BPMExtension config,
            @ParameterGroup(name = "Attachment association properties") BPMAttachmentAssociationProperties properties,
            @Optional @Alias("attachment-filters") List<BPMAttachmentFilter> attachmentFilters) {

        Result.Builder<InputStream, BPMAttachmentAttributes> resultBuilder = Result.builder();

        BPMAttachmentFinder attachmentFinder = new BPMAttachmentFinderImpl(config.getTaskService())
                .processInstanceId(properties.getProcessInstanceId())
                .taskId(properties.getTaskId());

        for (BPMAttachmentFilter attachmentFilter : attachmentFilters) {
            if (attachmentFilter instanceof BPMAttachmentNameFilter) {
                BPMAttachmentNameFilter attachmentNameFilter = (BPMAttachmentNameFilter) attachmentFilter;
                attachmentFinder.name(attachmentNameFilter.getAttachmentName());
            }
        }

        BPMAttachmentInstance attachmentInstance = attachmentFinder.latestAttachment();
        if (attachmentInstance != null) {
            LOGGER.debug("Attachment {} found for process instance {} and task {}",
                    attachmentInstance.getId(), properties.getProcessInstanceId(), properties.getTaskId());

            resultBuilder.output(config.getAttachmentContent(attachmentInstance.getId()));
            resultBuilder.attributes(attachmentInstance);
        } else {
            LOGGER.debug("No attachment(s) found for process instance {} and task {}",
                    properties.getProcessInstanceId(), properties.getTaskId());
        }

        return resultBuilder.build();
    }

    @Alias("remove-attachment")
    public void removeAttachment(
            @Config BPMExtension engine,
            @DisplayName("Attachment id") String attachmentId) {

        engine.removeAttachment(attachmentId);

        LOGGER.debug("Removed attachment {}", attachmentId);
    }

}
