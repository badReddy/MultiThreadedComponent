package com.component.multithreadedcomponent.service;

import org.apache.log4j.Logger;

public class TaskProcessor {
	static Logger logger = Logger.getLogger(TaskProcessor.class);
	public boolean printSomething(String task){
		//TODO Processing logic goes here.
		//This method needn't be synchronized,
		//because we are implementing at Object level and not method level.
		logger.info(task);
		return true;
	}
}
