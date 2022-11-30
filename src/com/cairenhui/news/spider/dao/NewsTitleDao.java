package com.cairenhui.news.spider.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.page.PageRequest;

import com.cairenhui.news.spider.base.BaseDao;
import com.cairenhui.news.spider.model.NewsTitle;

@SuppressWarnings("unchecked")
@Component
public class NewsTitleDao extends BaseDao<NewsTitle,java.lang.Long>{
	
	@SuppressWarnings("unchecked")
	public Class getEntityClass() {
		return NewsTitle.class;
	}
	
	public void saveOrUpdate(NewsTitle entity) {
		if(entity.getId() == null || entity.getId()==0)save(entity); else update(entity);
	}
	
	public Page findByPageRequest(PageRequest pageRequest) {
		return pageQuery("NewsTitle.pageSelect",pageRequest);
	}

	/**
	 * 根据下载flag获取新闻标题(与个股无关)
	 * @param flag
	 * @return
	 */
	public List<NewsTitle> findNewsTitle(int flag, int limit) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("flag", String.valueOf(flag));
		params.put("limit", String.valueOf(limit));
		
		return this.getSqlMapClientTemplate().queryForList("NewsTitle.findNewsTitle", params);
	}

	public List<NewsTitle> findNewsTitleByTime(int begin, int end) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("begin", String.valueOf(begin));
		params.put("end", String.valueOf(end));
		
		return this.getSqlMapClientTemplate().queryForList("NewsTitle.findNewsTitleByTime", params);
	}

	public int getEndIndex(int start, int step) {
		int maxId = (Integer)this.getSqlMapClientTemplate().queryForObject("NewsTitle.getMaxId");
		int end = 0;
		if(maxId > start){
			if(maxId > start + step){
				end = start + step; 
			}else{
				end = maxId;
			}
		}else{
			end = start;
		}
		return end;
	}

	public List<NewsTitle> getNewsTitleList(int start, int end) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("start", String.valueOf(start));
		params.put("end", String.valueOf(end));
		return this.getSqlMapClientTemplate().queryForList("NewsTitle.findByStartEndId", params);
	}

	public void deleteByUrl(String url) {
		this.getSqlMapClientTemplate().delete("NewsTitle.deleteByUrl", url);
		}

	public List<NewsTitle> findStockNewsTitleByTime(int begin, int end) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("begin", String.valueOf(begin));
		params.put("end", String.valueOf(end));
		
		return this.getSqlMapClientTemplate().queryForList("NewsTitle.findStockNewsTitleByTime", params);
	}

	public NewsTitle getByStockCodeAndTime(String stockCode, Integer time, String title) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockCode", stockCode);
		params.put("time", time);
		params.put("title", title);
		return (NewsTitle) this.getSqlMapClientTemplate().queryForObject("NewsTitle.getByStockCodeAndTime", params);
	}
	

}
