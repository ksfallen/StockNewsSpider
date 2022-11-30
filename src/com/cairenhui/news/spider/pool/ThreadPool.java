package com.cairenhui.news.spider.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class ThreadPool {
	private Logger logger = Logger.getLogger(ThreadPool.class);
	private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20,
												 60, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
	private static ThreadPool instance = new ThreadPool();
	
	public static ThreadPool getInstance(){
		if(null == instance){
			instance = new ThreadPool();
		}
		return instance;
	}
	public synchronized void register(Thread thread) {
		logger.info("注册线程: " + thread.getName());
		executor.submit(thread);
		logger.info("正在运行线程: " + executor.getActiveCount());
	}
}
