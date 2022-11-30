package com.cairenhui.news.spider.pool;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cairenhui.news.spider.constant.Constant;
import com.cairenhui.news.spider.model.NewsTitle;
import com.cairenhui.news.spider.service.NewsTitleService;

public class NewsTitlePool {
	private static NewsTitlePool pool = null;
	private List<NewsTitle> list = new ArrayList<NewsTitle>();
	
	@Autowired
	private NewsTitleService newsTitleService;
	
	private NewsTitlePool(){
		
	}
	
	public static synchronized NewsTitlePool getInstance(){
		if(null == pool){
			pool = new NewsTitlePool();
		}
		return pool;
	}
	
	public synchronized NewsTitle dequeue() {
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
		this.list.addAll(this.newsTitleService.findNewsTitle(Constant.NEWS_UNDOWNLOAD, 500));
	}

	public synchronized int getSize() {
		return this.list.size();
	}

	public NewsTitleService getNewsTitleService() {
		return newsTitleService;
	}

	public void setNewsTitleService(NewsTitleService newsTitleService) {
		this.newsTitleService = newsTitleService;
	}
}
