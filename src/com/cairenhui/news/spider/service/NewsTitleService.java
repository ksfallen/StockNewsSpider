package com.cairenhui.news.spider.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.page.PageRequest;

import com.cairenhui.news.spider.base.BaseService;
import com.cairenhui.news.spider.base.EntityDao;
import com.cairenhui.news.spider.constant.Constant;
import com.cairenhui.news.spider.dao.NewsTitleDao;
import com.cairenhui.news.spider.model.NewsTitle;
import com.cairenhui.news.spider.util.DateUtil;

@Component
public class NewsTitleService extends BaseService<NewsTitle,java.lang.Long>{
	private NewsTitleDao newsTitleDao;
	
	public void setNewsTitleDao(NewsTitleDao dao) {
		this.newsTitleDao = dao;
	}
	
	@SuppressWarnings("unchecked")
	public EntityDao getEntityDao() {
		return this.newsTitleDao;
	}
	
	@SuppressWarnings("unchecked")
	public Page findByPageRequest(PageRequest pr) {
		return newsTitleDao.findByPageRequest(pr);
	}
	
	/**
	 * 根据下载flag获取新闻标题(与个股无关)
	 * @param flag
	 * @return
	 */
	public List<NewsTitle> findNewsTitle(int flag, int limit) {
		List<NewsTitle> res = new ArrayList<NewsTitle>();
		
		res = this.newsTitleDao.findNewsTitle(flag, limit);
		if(null != res && res.size() > 0){
			for(NewsTitle newsTitle : res){
				newsTitle.setFlag(Constant.DOWNLOADING);
				this.newsTitleDao.update(newsTitle);
			}
		}
		
		return res;
	}
	
	/**
	 * 查询今日个股标题
	 * @return
	 */
	public List<NewsTitle> findTodayStockNewsTitle() {
		int begin = new Long(DateUtil.getStartTime(new Date())).intValue();
		int end = new Long(DateUtil.getEndTime(new Date())).intValue();
		return this.newsTitleDao.findStockNewsTitleByTime(begin, end);
	}

	/**
	 * 查询今日新闻标题
	 * @return
	 */
	public List<NewsTitle> findTodayNewsTitle() {
		int begin = new Long(DateUtil.getStartTime(new Date())).intValue();
		int end = new Long(DateUtil.getEndTime(new Date())).intValue();
		return this.newsTitleDao.findNewsTitleByTime(begin, end);
	}

	public int getEndIndex(int start, int step) {
		return this.newsTitleDao.getEndIndex(start, step);
	}

	public List<NewsTitle> getNewsTitleList(int start, int end) {
		return this.newsTitleDao.getNewsTitleList(start, end);
	}

	public void deleteByUrl(String url) {
		this.newsTitleDao.deleteByUrl(url);
	}
	
}
