package com.cairenhui.news.spider.pool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cairenhui.news.spider.model.NewsTitle;
import com.cairenhui.news.spider.service.NewsTitleService;

public class StockNewsTitlePool {
	private static StockNewsTitlePool stockNewsTitlePool = null;
	private Set<String> urlSet = new HashSet<String>();
	private boolean isInit = false;
	private int timeStamp = 0;
	
	@Autowired
	private NewsTitleService newsTitleService;
	
	private StockNewsTitlePool(){
		
	}
	
	public static StockNewsTitlePool getInstance(){
		if(null == stockNewsTitlePool){
			stockNewsTitlePool = new StockNewsTitlePool();
		}
		return stockNewsTitlePool;
	}
	
	public void init(){
		synchronized(urlSet){
			if(!isInit){
				loadTodayStockNewsTitle();
				isInit = true;
			}
		}
	}

	private void loadTodayStockNewsTitle() {
		List<NewsTitle> list = this.newsTitleService.findTodayStockNewsTitle();
		if(null != list && list.size() > 0){
			for(NewsTitle newsTitle : list){
				String url = newsTitle.getUrl();
				this.urlSet.add(url);
			}
		}
	}
	
	public boolean contains(String url) {
		synchronized(urlSet){
			return this.urlSet.contains(url);
		}
	}
	
	public void addUrl(String url){
		synchronized(urlSet){
			if(!urlSet.contains(url)){
				urlSet.add(url);
			}
		}
	}
	
	public void clear(){
		synchronized(urlSet){
			urlSet.clear();
		}
	}

	public NewsTitleService getNewsTitleService() {
		return newsTitleService;
	}

	public void setNewsTitleService(NewsTitleService newsTitleService) {
		this.newsTitleService = newsTitleService;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}
	
}
