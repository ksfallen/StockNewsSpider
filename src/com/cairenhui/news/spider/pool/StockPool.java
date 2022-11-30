package com.cairenhui.news.spider.pool;

import java.util.ArrayList;
import java.util.List;

import com.cairenhui.news.spider.model.NewsStockProfile;
import com.cairenhui.news.spider.service.NewsStockProfileService;

public class StockPool {
	private static StockPool instance = null;

	private NewsStockProfileService newsStockProfileService;

	private List<NewsStockProfile> list = new ArrayList<NewsStockProfile>();

	private StockPool() {

	}

	public static synchronized StockPool getInstance() {
		if (instance == null) {
			instance = new StockPool();
		}
		return instance;
	}

	public synchronized NewsStockProfile dequeue() {
		if (this.list.size() == 0) {
			return null;
		}
		return this.list.remove(0);
	}

	@SuppressWarnings("unchecked")
	public synchronized void enqueue() {
		if (this.list.size() > 0) {
			return;
		}
		this.list.addAll(this.newsStockProfileService.findAll());
	}

	public synchronized int getSize() {
		return this.list.size();
	}

	public NewsStockProfileService getNewsStockProfileService() {
		return newsStockProfileService;
	}

	public void setNewsStockProfileService(NewsStockProfileService newsStockProfileService) {
		this.newsStockProfileService = newsStockProfileService;
	}
}
