package com.alfame.esb.bpm.api;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class BPMAttachmentFinderTest {

	@Test
	public void processInstanceId() {
		BPMAttachmentFinder bpmAttachmentFinder = Mockito.mock(BPMAttachmentFinder.class, Mockito.CALLS_REAL_METHODS);
		bpmAttachmentFinder.processInstanceId("12345");
		Assert.assertEquals("12345", bpmAttachmentFinder.processInstanceId);
	}

	@Test
	public void taskId() {
		BPMAttachmentFinder bpmAttachmentFinder = Mockito.mock(BPMAttachmentFinder.class, Mockito.CALLS_REAL_METHODS);
		bpmAttachmentFinder.taskId("98765");
		Assert.assertEquals("98765", bpmAttachmentFinder.taskId);
	}

	@Test
	public void name() {
		BPMAttachmentFinder bpmAttachmentFinder = Mockito.mock(BPMAttachmentFinder.class, Mockito.CALLS_REAL_METHODS);
		bpmAttachmentFinder.name("attachment");
		Assert.assertEquals("attachment", bpmAttachmentFinder.name);
	}

}
