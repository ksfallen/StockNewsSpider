package com.cairenhui.news.spider.service;

import org.springframework.stereotype.Component;

import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.page.PageRequest;

import com.cairenhui.news.spider.base.BaseService;
import com.cairenhui.news.spider.base.EntityDao;
import com.cairenhui.news.spider.dao.NewsContentDao;
import com.cairenhui.news.spider.model.NewsContent;

@Component
public class NewsContentService extends BaseService<NewsContent,java.lang.Long>{
	private NewsContentDao newsContentDao;
	
	public void setNewsContentDao(NewsContentDao dao) {
		this.newsContentDao = dao;
	}
	@SuppressWarnings("unchecked")
	public EntityDao getEntityDao() {
		return this.newsContentDao;
	}
	@SuppressWarnings("unchecked")
	public Page findByPageRequest(PageRequest pr) {
		return newsContentDao.findByPageRequest(pr);
	}
	
}
