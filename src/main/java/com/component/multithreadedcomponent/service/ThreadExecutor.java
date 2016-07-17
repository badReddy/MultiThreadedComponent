package com.component.multithreadedcomponent.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;


public class ThreadExecutor {
	
	static Logger logger = Logger.getLogger(ThreadExecutor.class);

	public List<Future<Boolean>> startTask(List<String> tasks, ExecutorService executorService) throws Exception {
		List<Future<Boolean>> resultList = null;
		try{
			if(null==tasks || tasks.isEmpty()){
				throw new Exception("Tasks are empty");
			}
			List<Callable<Boolean>> bucketToCollect = Collections.synchronizedList(new ArrayList<Callable<Boolean>>());
			for(final String task : tasks){
				bucketToCollect.add(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception{
						Boolean flag = false;
						try{
							TaskProcessor processor = new TaskProcessor();
							flag = processor.printSomething(task);
						}catch(Exception e){
							logger.error("ERROR - While adding to bucketToCollect"+e.getMessage());
							throw e;
						}
						return flag;
					}
				});
				resultList = executorService.invokeAll(bucketToCollect);
			}
		}catch(Exception e){
			logger.error("ERROR - in startTask()--> "+e.getMessage());
			throw e;
		}
		return resultList;
	}

}
