package com.cairenhui.news.spider.dao;

import org.springframework.stereotype.Component;

import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.page.PageRequest;

import com.cairenhui.news.spider.base.BaseDao;
import com.cairenhui.news.spider.model.NewsStockProfile;

@SuppressWarnings("unchecked")
@Component
public class NewsStockProfileDao extends BaseDao<NewsStockProfile,java.lang.Integer>{
	
	@SuppressWarnings("unchecked")
	public Class getEntityClass() {
		return NewsStockProfile.class;
	}
	
	public void saveOrUpdate(NewsStockProfile entity) {
		if(entity.getId() == null || entity.getId()==0)save(entity); else update(entity);
	}
	
	public Page findByPageRequest(PageRequest pageRequest) {
		return pageQuery("NewsStockProfile.pageSelect",pageRequest);
	}
	

}
