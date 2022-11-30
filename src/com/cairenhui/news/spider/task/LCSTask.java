package com.cairenhui.news.spider.task;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cairenhui.news.spider.pool.ThreadPool;
import com.cairenhui.news.spider.service.LcsService;
import com.cairenhui.news.spider.service.NewsTitleService;
import com.cairenhui.news.spider.thread.LCSThread;

/**
 * 检测重复标题定时任务
 * @author chengyy
 *
 */
public class LCSTask implements InitializingBean {
	@Autowired
	private LcsService lcsService;
	@Autowired
	private NewsTitleService newsTitleService;
	
	public void doWork(){
		lcsService.saveCache();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
//		ThreadPool threadPool = ThreadPool.getInstance();
//		
//		LCSThread t = new LCSThread();
//		t.setLcsService(lcsService);
//		t.setNewsTitleService(newsTitleService);
//		
//		Thread thread = new Thread(t);
//		thread.setName("相似度检测线程");
//		
//		threadPool.register(thread);
	}

}
