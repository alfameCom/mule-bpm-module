package com.alfame.esb.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alfame.esb.bpm.api.BPMEngine;

@Lazy
@Component
public class TestComponent {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BPMEngine bpmEngine;
	
	public Object startProcessInstance() {
		Map< String, Object > variables = new HashMap< String, Object >();
		variables.put( "correct", "To be seen" );
		variables.put( "incorrect", "NOT to be seen" );
		
		return bpmEngine.processInstanceBuilder()
				.processDefinitionKey( "otherTestProcess" )
				.variables( variables )
				.startProcessInstance();
	}
	
	@Override
	public String toString() {
		logger.info("CONFIG " + bpmEngine);
		return "BPMConfig " + bpmEngine.getName();
	}
}
