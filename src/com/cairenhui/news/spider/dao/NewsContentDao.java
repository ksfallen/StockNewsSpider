package com.cairenhui.news.spider.dao;

import org.springframework.stereotype.Component;

import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.page.PageRequest;

import com.cairenhui.news.spider.base.BaseDao;
import com.cairenhui.news.spider.model.NewsContent;


@SuppressWarnings("unchecked")
@Component
public class NewsContentDao extends BaseDao<NewsContent,java.lang.Long>{
	
	@SuppressWarnings("unchecked")
	public Class getEntityClass() {
		return NewsContent.class;
	}
	
	public void saveOrUpdate(NewsContent entity) {
		if(entity.getId() == null || entity.getId()==0)save(entity); else update(entity);
	}
	
	public Page findByPageRequest(PageRequest pageRequest) {
		return pageQuery("NewsContent.pageSelect",pageRequest);
	}
	

}
