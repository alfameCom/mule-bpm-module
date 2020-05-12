package com.alfame.esb.bpm.connector.internal.operations;

import com.alfame.esb.bpm.api.BPMAttachmentAttributes;
import com.alfame.esb.bpm.api.BPMAttachmentFinder;
import com.alfame.esb.bpm.api.BPMAttachmentInstance;
import com.alfame.esb.bpm.connector.api.config.BPMAttachmentFilter;
import com.alfame.esb.bpm.connector.api.config.BPMAttachmentNameFilter;
import com.alfame.esb.bpm.connector.internal.BPMAttachmentFinderImpl;
import com.alfame.esb.bpm.connector.internal.BPMExtension;
import com.alfame.esb.bpm.connector.internal.connection.BPMConnection;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.*;
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
            @Connection BPMConnection connection,
            @Optional @Alias("attachment-filters") List<BPMAttachmentFilter> attachmentFilters) {
        if (connection == null || connection.getTask() == null) {
            throw new IllegalStateException("Get attachment operation must join to task listener transaction");
        }

        Result.Builder<InputStream, BPMAttachmentAttributes> resultBuilder = Result.builder();

        BPMAttachmentFinder attachmentFinder = new BPMAttachmentFinderImpl(config.getTaskService())
                .processInstanceId(connection.getTask().getProcessInstanceId());

        for (BPMAttachmentFilter attachmentFilter : attachmentFilters) {
            if (attachmentFilter instanceof BPMAttachmentNameFilter) {
                BPMAttachmentNameFilter attachmentNameFilter = (BPMAttachmentNameFilter) attachmentFilter;
                attachmentFinder.name(attachmentNameFilter.getAttachmentName());
            }
        }

        BPMAttachmentInstance attachmentInstance = attachmentFinder.latestAttachment();
        if (attachmentInstance != null) {
            LOGGER.debug("Attachment " + attachmentInstance.getId() + " found for process " + connection.getTask().getProcessInstanceId());

            resultBuilder.output(config.getAttachmentContent(attachmentInstance.getId()));
            resultBuilder.attributes(attachmentInstance);
        } else {
            LOGGER.debug("Attachment " + attachmentInstance.getId() + " not found for process " + connection.getTask().getProcessInstanceId());
        }

        return resultBuilder.build();
    }

}
