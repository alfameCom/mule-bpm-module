package com.alfame.esb.test;

import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;

public class StartProcess {

	private static StartProcess instance = null;
	
	@Autowired
	private RuntimeService runtimeService;
	
	public StartProcess() {
		
		runtimeService.startProcessInstanceByKey( "testProcess" );
		
	}
	
	public static StartProcess getInstance() {
		if( instance == null )
			instance = new StartProcess();
		
		return instance;
	}
	
}
