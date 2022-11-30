package com.cairenhui.news.spider.task;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cairenhui.news.spider.pool.StockNewsTitlePool;
import com.cairenhui.news.spider.pool.StockPool;
import com.cairenhui.news.spider.service.LcsService;
import com.cairenhui.news.spider.service.NewsStockProfileService;
import com.cairenhui.news.spider.service.NewsTitleService;
import com.cairenhui.news.spider.thread.StockTitleCrawlerThread;
import com.cairenhui.news.spider.util.DateUtil;
import com.cairenhui.sns.dao.StockProfileDao;
import com.cairenhui.sns.service.StockProfileService;
import com.cairenhui.sns.service.TrieSearchService;

/**
 * 定时抓取个股新闻标题、url
 * @author chengyy
 *
 */
public class StockTitleTask implements InitializingBean {
	private static Logger logger = Logger.getLogger(StockTitleTask.class);
	private StockPool stockPool = StockPool.getInstance();
	private int threadCount = 20;
	private StockNewsTitlePool newsTitlePool = StockNewsTitlePool.getInstance();
	
	@Autowired
	private NewsStockProfileService newsStockProfileService;
	@Autowired
	private StockProfileService stockProfileService;
	@Autowired
	private NewsTitleService newsTitleService;
	@Autowired
	private LcsService lcsService;
	@Autowired
	private TrieSearchService trieSearchService;
	@Autowired
	private StockProfileDao stockProfileDao;
	
	public void process(){
		int currentTimeStamp = Integer.parseInt(DateUtil.formatDate(new Date(), "yyyyMMdd"));
		if(currentTimeStamp != newsTitlePool.getTimeStamp()){
			newsTitlePool.clear();
			newsTitlePool.setTimeStamp(currentTimeStamp);
		}
		if (this.stockPool.getSize() == 0) {
			this.stockPool.enqueue();
			logger.info("加载股票列表已完成, 共" + this.stockPool.getSize() + "条记录");
		}
		lcsService.saveCache();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(null == stockPool.getNewsStockProfileService()){
			stockPool.setNewsStockProfileService(newsStockProfileService);
		}
		if(null == newsTitlePool.getNewsTitleService()){
			newsTitlePool.setNewsTitleService(newsTitleService);
		}
		for (int i = 0; i < this.threadCount; i++) {
			StockTitleCrawlerThread crawler = new StockTitleCrawlerThread();
			crawler.setLcsService(lcsService);
			crawler.setNewsStockProfileService(newsStockProfileService);
			crawler.setNewsTitleService(newsTitleService);
			crawler.setStockProfileService(stockProfileService);
			crawler.setTrieSearchService(trieSearchService);
			crawler.setStockProfileDao(stockProfileDao);
			
			new Thread(crawler).start();
		}
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

}
