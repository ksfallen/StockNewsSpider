package com.cairenhui.news.spider.task;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cairenhui.news.spider.pool.NewsTitlePool;
import com.cairenhui.news.spider.service.NewsContentService;
import com.cairenhui.news.spider.service.NewsTitleService;
import com.cairenhui.news.spider.thread.ContentCrawlerThread;

/**
 * 资讯内容下载任务(与个股无关)
 * @author chengyy
 *
 */
public class ContentTask implements InitializingBean {
	private static Logger logger = Logger.getLogger(ContentTask.class);
	private NewsTitlePool newsTitlePool = NewsTitlePool.getInstance();
	private int threadCount;
	
	@Autowired
	private NewsTitleService newsTitleService;
	@Autowired
	private NewsContentService newsContentService;
	
	public void process(){
		if(this.newsTitlePool.getSize() == 0){
			this.newsTitlePool.enqueue();
			logger.info("加载标题列表完成，共" + this.newsTitlePool.getSize() + "条记录");
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(this.threadCount == 0){
			this.threadCount = 15;
		}
		if(null == this.newsTitlePool.getNewsTitleService()){
			newsTitlePool.setNewsTitleService(this.newsTitleService);
		}
		for(int i=0; i<this.threadCount; i++){
			ContentCrawlerThread runnable = new ContentCrawlerThread();
			runnable.setNewsContentService(newsContentService);
			runnable.setNewsTitleService(newsTitleService);
			
			Thread thread = new Thread(runnable);
			thread.start();
		}
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public NewsTitleService getNewsTitleService() {
		return newsTitleService;
	}

	public void setNewsTitleService(NewsTitleService newsTitleService) {
		this.newsTitleService = newsTitleService;
	}

	public NewsContentService getNewsContentService() {
		return newsContentService;
	}

	public void setNewsContentService(NewsContentService newsContentService) {
		this.newsContentService = newsContentService;
	}
	
	
}
