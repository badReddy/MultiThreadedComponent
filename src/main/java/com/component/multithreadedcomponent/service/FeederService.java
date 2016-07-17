package com.component.multithreadedcomponent.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;


public class FeederService {

	static Logger logger = Logger.getLogger(FeederService.class);
	
	public Properties getProperties(){
		Properties properties = new Properties();
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
			if(null!=inputStream){
				properties.load(inputStream);
			}
			else{
				logger.error("ERROR - file config.properties is not found in classpath");
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}
	public static void main(String[] args) {

		FeederService feeder = new FeederService();
		Properties properties = new Properties();
		try {
			logger.info("Start...");
			properties = feeder.getProperties();
			String fetchCount = properties.getProperty("FETCH_COUNT");
			String threadCount = properties.getProperty("THREAD_COUNT");
			if(null!=fetchCount&&null!=threadCount&&!"".equals(fetchCount)&&!"".equals(threadCount)){
				feeder.doTask(Integer.parseInt(fetchCount),Integer.parseInt(threadCount));
			}
			else{
				logger.error("ERROR - Please configure Fetch Count and Thread Count");
				throw new Exception("FETCH_COUNT||THREAD_COUNT not found in config.properties");
			}
			logger.info("End...");
		} catch (Exception e) {
			logger.error("ERROR - Something bad happened"+ e.getMessage());
			System.exit(0);
		}
	}

	private void doTask(int FETCH_COUNT,int THREAD_COUNT) throws Exception{
		logger.info("Started feeder.doTask()");
		int taskCount = 0;
		try{
			int start = 0;
			int end = FETCH_COUNT;
			List<String> tasks = getAllTasks(start,end);
			if(null!=tasks && !tasks.isEmpty()){
				ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
				try{
					while(null!=tasks && !tasks.isEmpty()){
						ThreadExecutor threadExecutor = new ThreadExecutor();
						List<Future<Boolean>> resultList = threadExecutor.startTask(tasks,executorService);
						if(null==resultList || resultList.isEmpty()){
							Thread.sleep(500);
						}else{
							taskCount = taskCount+resultList.size();
						}
						start = end+1;
						end = end+100;
						tasks = getAllTasks(start,end);
					}
				}catch(Exception e){
					throw e;
				}
				finally{
					shutdownAndAwaitTermination(executorService);
				}
				if(taskCount< (end-start)){
					Thread.sleep(500);
				}
			}else{
				logger.info("Tasks to be performed are empty or no more tasks to perform.");
			}
			logger.info("feeder.doTask() completed succesfully.");
		}catch(Exception e){
			logger.error("ERROR - in feeder.doTask()");
			throw e;
		}
	}

	private void shutdownAndAwaitTermination(ExecutorService executorService) {
		executorService.shutdown();
		try{
			if(executorService.awaitTermination(60, TimeUnit.SECONDS)){
				executorService.shutdownNow();
				if(!executorService.awaitTermination(60, TimeUnit.SECONDS)){
					logger.error("ERROR - Pool did not terminate");
				}
			}
		}catch(InterruptedException ie){
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	private List<String> getAllTasks(int start, int end) throws Exception {
		List<String> tasks = null;
		List<String> taskList = new ArrayList<String>();
		for(int i=1;i<=1000;i++){
			taskList.add("task--> "+i);
		}
		try{
			if(end>1000){
				return null;
			}
			else{
				if(start<end){
					tasks = taskList.subList(start, end);
				}
				else{
				}
			}
		}catch(Exception e){
			logger.error("ERROR - While retrieving tasks");
			throw e;
		}
		return tasks;
	}

}
