package com.cairenhui.news.spider.thread;

import java.util.List;

import org.apache.log4j.Logger;

import com.cairenhui.news.spider.model.NewsTitle;
import com.cairenhui.news.spider.service.LcsService;
import com.cairenhui.news.spider.service.NewsTitleService;
import com.cairenhui.news.spider.util.ConfigUtil;

public class LCSThread implements Runnable {
	private LcsService lcsService;
	private NewsTitleService newsTitleService;
	
	static int step = Integer.parseInt(ConfigUtil.getValue("step"));
	static int start = 0;
	static int end = 0;
	Logger logger = Logger.getLogger("logger.lcs");
	static int sleepTime = Integer.parseInt(ConfigUtil.getValue("sleepTime")) * 1000;
	
	public LCSThread() {
		
	}

	@Override
	public void run() {
		while(true){
			try{
				start = lcsService.getStartIndex();
				end = newsTitleService.getEndIndex(start, step);
				
				if(!(start < 0) && end > start){
					List<NewsTitle> newsTitleList = newsTitleService.getNewsTitleList(start, end);
					logger.info("相似度检测开始, start = " + start + ", end = " + end + ", 共" + newsTitleList.size() + "条待检测");
					if(null != newsTitleList && newsTitleList.size() > 0){
						for(NewsTitle newsTitle : newsTitleList){
							String stockCode = newsTitle.getStockCode();
							String title = newsTitle.getTitle();
							String url = newsTitle.getUrl();
							
							boolean res = lcsService.hasSimilar(stockCode, title);
							if(res){
								logger.info("检测到重复标题, url = " + url + ", title = " + title);
								newsTitleService.deleteByUrl(url);
							}
						}
					}
					logger.info("相似度检测结束, start = " + start + ", end = " + end);
				}
				start = end;
				lcsService.saveStart(start);
			}catch(Exception e){
				logger.error("相似度检测错误", e);
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.error("thread sleep error", e);
			}
		}
	}

	public LcsService getLcsService() {
		return lcsService;
	}

	public void setLcsService(LcsService lcsService) {
		this.lcsService = lcsService;
	}

	public NewsTitleService getNewsTitleService() {
		return newsTitleService;
	}

	public void setNewsTitleService(NewsTitleService newsTitleService) {
		this.newsTitleService = newsTitleService;
	}
}
