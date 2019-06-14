package com.alfame.esb.connectors.bpm.internal.processfactory;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class ProcessFactoryProperty {

	@Parameter
	private String tenantId;

	@Parameter
	private String processDefinitionKey;

	@Parameter
	private String processName;

	@Parameter
	@Optional
	private String payloadAsAttachment;

	@Parameter
	@Optional
	private String returnExceptions;

	@Parameter
	@Optional
	private String uniqueBusinessKey;

	@Parameter
	@Optional
	private String attachmentName;

	@Parameter
	@Optional
	private String attachmentType;

	@Parameter
	@Optional
	private String attachmentDescription;

	public String getTenantId() {
		return tenantId;
	}

	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public String getProcessName() {
		return processName;
	}

	public boolean isPayloadAsAttachment() {
		return payloadAsAttachment != null && payloadAsAttachment.toLowerCase().trim().equals( "true" );
	}

	public boolean isReturnExceptions() {
		return returnExceptions != null && payloadAsAttachment.toLowerCase().trim().equals( "true" );
	}

	public String getUniqueBusinessKey() {
		return uniqueBusinessKey;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public String getAttachmentType() {
		return attachmentType;
	}

	public String getAttachmentDescription() {
		return attachmentDescription;
	}

}
