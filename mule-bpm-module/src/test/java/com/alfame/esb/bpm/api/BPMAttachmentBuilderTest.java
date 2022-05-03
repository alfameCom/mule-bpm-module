package com.alfame.esb.bpm.api;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

import static org.junit.Assert.*;

public class BPMAttachmentBuilderTest {

	@Test
	public void processInstanceId() {
		BPMAttachmentBuilder bpmAttachmentBuilder = Mockito.mock(BPMAttachmentBuilder.class, Mockito.CALLS_REAL_METHODS);
		bpmAttachmentBuilder.processInstanceId("12345");
		Assert.assertEquals("12345", bpmAttachmentBuilder.processInstanceId);
	}

	@Test
	public void taskId() {
		BPMAttachmentBuilder bpmAttachmentBuilder = Mockito.mock(BPMAttachmentBuilder.class, Mockito.CALLS_REAL_METHODS);
		bpmAttachmentBuilder.taskId("98765");
		Assert.assertEquals("98765", bpmAttachmentBuilder.taskId);
	}

	@Test
	public void type() {
		BPMAttachmentBuilder bpmAttachmentBuilder = Mockito.mock(BPMAttachmentBuilder.class, Mockito.CALLS_REAL_METHODS);
		bpmAttachmentBuilder.type("json");
		Assert.assertEquals("json", bpmAttachmentBuilder.type);
	}

	@Test
	public void name() {
		BPMAttachmentBuilder bpmAttachmentBuilder = Mockito.mock(BPMAttachmentBuilder.class, Mockito.CALLS_REAL_METHODS);
		bpmAttachmentBuilder.name("attachment");
		Assert.assertEquals("attachment", bpmAttachmentBuilder.name);
	}

	@Test
	public void description() {
		BPMAttachmentBuilder bpmAttachmentBuilder = Mockito.mock(BPMAttachmentBuilder.class, Mockito.CALLS_REAL_METHODS);
		bpmAttachmentBuilder.description("json file containing data");
		Assert.assertEquals("json file containing data", bpmAttachmentBuilder.description);
	}

	@Test
	public void url() {
		BPMAttachmentBuilder bpmAttachmentBuilder = Mockito.mock(BPMAttachmentBuilder.class, Mockito.CALLS_REAL_METHODS);
		bpmAttachmentBuilder.url("file://attachment.json");
		Assert.assertEquals("file://attachment.json", bpmAttachmentBuilder.url);
	}

	@Test
	public void content() throws IOException {
		BPMAttachmentBuilder bpmAttachmentBuilder = Mockito.mock(BPMAttachmentBuilder.class, Mockito.CALLS_REAL_METHODS);
		bpmAttachmentBuilder.content(new ByteArrayInputStream("{\"agent\": \"bond\"}".getBytes()));

		StringWriter actual = new StringWriter();
		IOUtils.copy(bpmAttachmentBuilder.content, actual, StandardCharsets.UTF_8);

		assertEquals("{\"agent\": \"bond\"}", actual.toString());
	}
}