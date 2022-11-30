package com.cairenhui.news.spider.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.page.PageRequest;
import cn.org.rapid_framework.util.MapAndObject;

import com.ibatis.common.util.PageList;
import com.ibatis.sqlmap.client.SqlMapClientDaoSupport;

/**
 * @author fuzr
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public abstract class BaseDao<E,PK extends Serializable> extends SqlMapClientDaoSupport implements EntityDao<E,PK> {
    protected final Log log = LogFactory.getLog(getClass());
    
	public abstract Class getEntityClass();
    
    @SuppressWarnings("unchecked")
	public E getById(PK y) {
        E e = (E) getSqlMapClientTemplate().queryForObject(getFindByPrimaryKeyQuery(), y);
        return e;
    }
    
	public void deleteById(PK id) {
		getSqlMapClientTemplate().delete(getDeleteQuery(), id);
	}
	
    public void save(E entity) {
		prepareObjectForSaveOrUpdate(entity);
		@SuppressWarnings("unused")
		Object primaryKey = getSqlMapClientTemplate().insert(getInsertQuery(), entity);
    }
    
	public void update(E entity) {
		prepareObjectForSaveOrUpdate(entity);
		@SuppressWarnings("unused")
		Object primaryKey = getSqlMapClientTemplate().update(getUpdateQuery(), entity);
	}
	
	/**
	 * 用于子类覆盖,在insert,update之前调用
	 * @param o
	 */
    protected void prepareObjectForSaveOrUpdate(E o) {
    }

    public String getFindByPrimaryKeyQuery() {
        return getEntityClass().getSimpleName()+".getById";
    }
    public String getFindAllQuery() {
        return getEntityClass().getSimpleName()+".findAll";
    }
    public String getInsertQuery() {
        return getEntityClass().getSimpleName()+".insert";
    }

    public String getUpdateQuery() {
    	return getEntityClass().getSimpleName()+".update";
    }

    public String getDeleteQuery() {
    	return getEntityClass().getSimpleName()+".delete";
    }

    public String getCountQuery() {
		return getEntityClass().getSimpleName() +".count";
	}
    public String getFindByPropertiesQuery(){
    	return getEntityClass().getSimpleName() +".getByProperties";
    }
	protected Page pageQuery(String statementName, PageRequest pageRequest) {
		//Number totalCount = (Number) this.getSqlMapClientTemplate().queryForObject(getCountQuery(),pageRequest.getFilters());
		//1000000000为随便设置，不影响程序运行，之后会被重新赋值。
		Page page = new Page(pageRequest,1000000000);
		
		//其它分页参数,用于不喜欢或是因为兼容�?而不使用方言(Dialect)的分页用户使�? 与getSqlMapClientTemplate().queryForList(statementName, parameterObject)配合使用
		Map otherFilters = new HashMap();
		otherFilters.put("offset", page.getFirstResult());
		otherFilters.put("pageSize", page.getPageSize());
		otherFilters.put("lastRows", page.getFirstResult() + page.getPageSize());
		otherFilters.put("sortColumns", pageRequest.getSortColumns());
		
		//混合两个filters为一个filters,MapAndObject.get()方法将在两个对象取�?,Map如果取�?为null,则再在Bean中取�?
		Map parameterObject = new MapAndObject(otherFilters,pageRequest.getFilters());
		return pageQuery(statementName,parameterObject,pageRequest);
		
		//List list = getSqlMapClientTemplate().queryForList(statementName, parameterObject,page.getFirstResult(),page.getPageSize());
		//page.setResult(list);
		//return page;
		 
	}
	
	/**
	 * <b>Summary1: </b>
	 *     pageQuery(请用一句话描述这个方法的作用)
	 * @param statementName
	 * @param paramObject
	 * @param pageRequest
	 * @return
	 */
	protected Page pageQuery(String statementName, Object paramObject, PageRequest pageRequest) {
		PageList pl = getSqlMapClientTemplate().queryForPageList(statementName, paramObject,pageRequest.getPageNumber(),pageRequest.getPageSize());
		Page page = new Page(pageRequest,pl.getTotalCount());
		page.setResult(pl.getDataList());
		return page;
	}
	
	
	protected Page pageQuery(String statementName, PageRequest pageRequest, int limitPage) {
		Page page = pageQuery(statementName, pageRequest);
		Page pageRet = null;
		if(page.getLastPageNumber()>limitPage){
			pageRet = new Page(pageRequest,limitPage*pageRequest.getPageSize());
			pageRet.setResult(page.getResult());
		}else
			pageRet = page;
		return pageRet;
			
		/*
		Number totalCount = (Number) this.getSqlMapClientTemplate().queryForObject(getCountQuery(),pageRequest.getFilters());
		Page page = null;
		if(limitPage==0){
			page = new Page(pageRequest,totalCount.intValue());
		}else if(totalCount.intValue()>limitPage){
			page = new Page(pageRequest,limitPage);
		}else{
			page = new Page(pageRequest,totalCount.intValue());
		}
		//其它分页参数,用于不喜欢或是因为兼容�?而不使用方言(Dialect)的分页用户使�? 与getSqlMapClientTemplate().queryForList(statementName, parameterObject)配合使用
		Map otherFilters = new HashMap();
		otherFilters.put("offset", page.getFirstResult());
		otherFilters.put("pageSize", page.getPageSize());
		otherFilters.put("lastRows", page.getFirstResult() + page.getPageSize());
		otherFilters.put("sortColumns", pageRequest.getSortColumns());
		
		//混合两个filters为一个filters,MapAndObject.get()方法将在两个对象取�?,Map如果取�?为null,则再在Bean中取�?
		Map parameterObject = new MapAndObject(otherFilters,pageRequest.getFilters());
		List list = getSqlMapClientTemplate().queryForList(statementName, parameterObject,page.getFirstResult(),page.getPageSize());
		page.setResult(list);
		return page;
		*/
	}
	/*
	@SuppressWarnings("unchecked")
	protected Page pageQuery(String statementName, PageRequest pageRequest,String statementCount) {
		Number totalCount = (Number) this.getSqlMapClientTemplate().queryForObject(statementCount,pageRequest.getFilters());
		Page page = new Page(pageRequest,totalCount.intValue());
		
		//其它分页参数,用于不喜欢或是因为兼容�?而不使用方言(Dialect)的分页用户使�? 与getSqlMapClientTemplate().queryForList(statementName, parameterObject)配合使用
		Map otherFilters = new HashMap();
		otherFilters.put("offset", page.getFirstResult());
		otherFilters.put("pageSize", page.getPageSize());
		otherFilters.put("lastRows", page.getFirstResult() + page.getPageSize());
		otherFilters.put("sortColumns", pageRequest.getSortColumns());
		
		//混合两个filters为一个filters,MapAndObject.get()方法将在两个对象取�?,Map如果取�?为null,则再在Bean中取�?
		Map parameterObject = new MapAndObject(otherFilters,pageRequest.getFilters());
		List list = getSqlMapClientTemplate().queryForList(statementName, parameterObject,page.getFirstResult(),page.getPageSize());
		page.setResult(list);
		return page;
	}
	*/
	public List findAll() {
		return this.getSqlMapClientTemplate().queryForList(getFindAllQuery());
	}

	public boolean isUnique(E entity, String uniquePropertyNames) {
		throw new UnsupportedOperationException();
	}
	
	public void flush() {
		//ignore
	}
	public List getByProperties(E entity){
	
		List list  = this.getSqlMapClientTemplate().queryForList(getFindByPropertiesQuery(), entity);
		
		
		return list;
	}
}
