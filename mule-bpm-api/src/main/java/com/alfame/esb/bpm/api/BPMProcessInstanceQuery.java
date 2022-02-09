package com.alfame.esb.bpm.api;

import java.util.List;

public interface BPMProcessInstanceQuery {

    List<BPMProcessInstance> instances(int firstResult, int maxResults);

    BPMProcessInstance uniqueInstance();

}