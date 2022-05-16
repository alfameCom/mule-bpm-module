package com.alfame.esb.bpm.module.unit;

import com.alfame.esb.bpm.api.BPMAttachmentInstance;
import com.alfame.esb.bpm.module.internal.impl.BPMAttachmentFinderImpl;
import com.alfame.esb.bpm.module.internal.impl.BPMAttachmentInstanceProxy;
import org.apache.commons.lang3.time.DateUtils;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.AttachmentEntityImpl;
import org.flowable.engine.task.Attachment;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BPMAttachmentFinderImplTest {

	private final String PROCESS_INSTANCE_ID = "12345";
	private final String TASK_ID = "98765";

	@Test
	public void attachments_givenProcessInstanceId_returnsProcessInstanceAttachments() {
		TaskService taskService = Mockito.mock(TaskService.class);

		BPMAttachmentFinderImpl bpmAttachmentFinder = new BPMAttachmentFinderImpl(taskService);
		bpmAttachmentFinder.processInstanceId(PROCESS_INSTANCE_ID);

		bpmAttachmentFinder.attachments();

		Mockito.verify(taskService).getProcessInstanceAttachments(PROCESS_INSTANCE_ID);
	}

	@Test
	public void attachments_givenTaskId_returnsTaskAttachments() {
		TaskService taskService = Mockito.mock(TaskService.class);

		BPMAttachmentFinderImpl bpmAttachmentFinder = new BPMAttachmentFinderImpl(taskService);
		bpmAttachmentFinder.taskId(TASK_ID);

		bpmAttachmentFinder.attachments();

		Mockito.verify(taskService).getTaskAttachments(TASK_ID);
	}

	@Test
	public void attachments_givenNoProcessInstanceIdOrTaskId_throwsIllegalArgumentException() {
		TaskService taskService = Mockito.mock(TaskService.class);

		BPMAttachmentFinderImpl bpmAttachmentFinder = new BPMAttachmentFinderImpl(taskService);

		try {
			bpmAttachmentFinder.attachments();
			Assert.fail();
		} catch (IllegalArgumentException illegalArgumentException) {
			Mockito.verifyZeroInteractions(taskService);
		}
	}

	@Test
	public void attachments_givenNoAttachments_returnEmptyList() {
		TaskService taskService = Mockito.mock(TaskService.class);

		BPMAttachmentFinderImpl bpmAttachmentFinder = new BPMAttachmentFinderImpl(taskService);
		bpmAttachmentFinder.processInstanceId(PROCESS_INSTANCE_ID);

		List<Attachment> attachments = new ArrayList<>();
		Mockito.when(taskService.getProcessInstanceAttachments(PROCESS_INSTANCE_ID)).thenReturn(attachments);

		Assert.assertEquals(Collections.emptyList(), bpmAttachmentFinder.attachments());

		Mockito.verify(taskService).getProcessInstanceAttachments(PROCESS_INSTANCE_ID);
	}

	@Test
	public void attachments_givenName_returnMatching() {
		TaskService taskService = Mockito.mock(TaskService.class);

		BPMAttachmentFinderImpl bpmAttachmentFinder = new BPMAttachmentFinderImpl(taskService);
		bpmAttachmentFinder.processInstanceId(PROCESS_INSTANCE_ID);
		bpmAttachmentFinder.name("jsonAttachment2");

		List<Attachment> attachments = new ArrayList<>();
		AttachmentEntityImpl attachment1 = createAttachment("5678", "json", "jsonAttachment1", PROCESS_INSTANCE_ID, null, new Date());
		AttachmentEntityImpl attachment2 = createAttachment("3456", "json", "jsonAttachment2", PROCESS_INSTANCE_ID, null, new Date());
		attachments.add(attachment1);
		attachments.add(attachment2);
		Mockito.when(taskService.getProcessInstanceAttachments(PROCESS_INSTANCE_ID)).thenReturn(attachments);

		List<BPMAttachmentInstance> asd = bpmAttachmentFinder.attachments();

		Assert.assertEquals(1, asd.size());

		MatcherAssert.assertThat(asd, Matchers.contains(
				Matchers.hasProperty("id", Matchers.is(attachment2.getId()))
		));

		Mockito.verify(taskService).getProcessInstanceAttachments(PROCESS_INSTANCE_ID);
	}

	@Test
	public void latestAttachment_givenNoAttachments_returnNull() {
		TaskService taskService = Mockito.mock(TaskService.class);

		BPMAttachmentFinderImpl bpmAttachmentFinder = new BPMAttachmentFinderImpl(taskService);
		bpmAttachmentFinder.processInstanceId(PROCESS_INSTANCE_ID);

		List<Attachment> attachments = new ArrayList<>();
		Mockito.when(taskService.getProcessInstanceAttachments(PROCESS_INSTANCE_ID)).thenReturn(attachments);

		Assert.assertNull(bpmAttachmentFinder.latestAttachment());

		Mockito.verify(taskService).getProcessInstanceAttachments(PROCESS_INSTANCE_ID);
	}

	@Test
	public void latestAttachment_givenOneAttachment_returnIt() {
		TaskService taskService = Mockito.mock(TaskService.class);

		BPMAttachmentFinderImpl bpmAttachmentFinder = new BPMAttachmentFinderImpl(taskService);
		bpmAttachmentFinder.processInstanceId(PROCESS_INSTANCE_ID);

		List<Attachment> attachments = new ArrayList<>();
		AttachmentEntityImpl attachment = createAttachment("1234", "json", "jsonAttachment", PROCESS_INSTANCE_ID, null, new Date());
		attachments.add(attachment);

		Mockito.when(taskService.getProcessInstanceAttachments(PROCESS_INSTANCE_ID)).thenReturn(attachments);

		BPMAttachmentInstance attachmentProxy = new BPMAttachmentInstanceProxy(attachment);

		Assert.assertEquals(attachmentProxy.getId(), bpmAttachmentFinder.latestAttachment().getId());

		Mockito.verify(taskService).getProcessInstanceAttachments(PROCESS_INSTANCE_ID);
	}

	@Test
	public void latestAttachment_givenMultipleAttachments_returnLatest() {
		TaskService taskService = Mockito.mock(TaskService.class);

		BPMAttachmentFinderImpl bpmAttachmentFinder = new BPMAttachmentFinderImpl(taskService);
		bpmAttachmentFinder.processInstanceId(PROCESS_INSTANCE_ID);

		List<Attachment> attachments = new ArrayList<>();
		AttachmentEntityImpl attachment1 = createAttachment("5678", "json", "jsonAttachment3", PROCESS_INSTANCE_ID, null, new Date());
		AttachmentEntityImpl attachment2 = createAttachment("3456", "json", "jsonAttachment2", PROCESS_INSTANCE_ID, null, DateUtils.addDays(new Date(), -1));
		AttachmentEntityImpl attachment3 = createAttachment("1234", "json", "jsonAttachment1", PROCESS_INSTANCE_ID, null, DateUtils.addDays(new Date(), -2));
		attachments.add(attachment3);
		attachments.add(attachment2);
		attachments.add(attachment1);

		Mockito.when(taskService.getProcessInstanceAttachments(PROCESS_INSTANCE_ID)).thenReturn(attachments);

		BPMAttachmentInstance attachment1Proxy = new BPMAttachmentInstanceProxy(attachment1);

		Assert.assertEquals(attachment1Proxy.getId(), bpmAttachmentFinder.latestAttachment().getId());

		Mockito.verify(taskService).getProcessInstanceAttachments(PROCESS_INSTANCE_ID);
	}

	private AttachmentEntityImpl createAttachment(String id, String type, String name, String processInstanceId, String taskId, Date time) {
		AttachmentEntityImpl attachment = new AttachmentEntityImpl();
		attachment.setId(id);
		attachment.setType(type);
		attachment.setName(name);
		attachment.setProcessInstanceId(processInstanceId);
		attachment.setTaskId(taskId);
		attachment.setTime(time);
		return attachment;
	}

}
