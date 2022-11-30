package com.cairenhui.news.spider.service;

import org.springframework.stereotype.Component;

import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.page.PageRequest;

import com.cairenhui.news.spider.base.BaseService;
import com.cairenhui.news.spider.base.EntityDao;
import com.cairenhui.news.spider.dao.NewsStockProfileDao;
import com.cairenhui.news.spider.model.NewsStockProfile;

@Component
public class NewsStockProfileService extends BaseService<NewsStockProfile,java.lang.Integer>{
	private NewsStockProfileDao newsStockProfileDao;
	/**增加setXXXX()方法,spring就可以通过autowire自动设置对象属性*/
	public void setNewsStockProfileDao(NewsStockProfileDao dao) {
		this.newsStockProfileDao = dao;
	}
	@SuppressWarnings("unchecked")
	public EntityDao getEntityDao() {
		return this.newsStockProfileDao;
	}
	@SuppressWarnings("unchecked")
	public Page findByPageRequest(PageRequest pr) {
		return newsStockProfileDao.findByPageRequest(pr);
	}
	
}
