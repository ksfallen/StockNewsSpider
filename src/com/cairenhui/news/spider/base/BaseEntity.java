package com.cairenhui.news.spider.base;


import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.BeanUtilsBean;

import cn.org.rapid_framework.util.DateConvertUtils;

import com.ibatis.common.object.KeyObject;

/**
 * @author fuzr
 */
public abstract class BaseEntity extends KeyObject implements java.io.Serializable {
	
	/**类是否已经初始化,默认是false*/
	public Boolean isInited = false;
	
	protected static final String DATE_FORMAT = "yyyy-MM-dd";
	
	protected static final String TIME_FORMAT = "HH:mm:ss";
	
	protected static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	protected static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
	
	
	public static String date2String(java.util.Date date,String dateFormat) {
		return DateConvertUtils.format(date,dateFormat);
	}
	
	public static <T extends java.util.Date> T string2Date(String dateString,String dateFormat,Class<T> targetResultType) {
		return DateConvertUtils.parse(dateString,dateFormat,targetResultType);
	}
	
	public void reSet(String value){
	}
	
	public String getBody(){
		return null;
	}
	/**
	 * 外部bean调用初始化方法
	 * @author fuzr
	 */
	public void init(){
		if(isInited){
			Exception e = new Exception("The Model is inited");
			e.printStackTrace();
			return;
		}
		initMethod();
		isInited = true;
	}
	/**
	 * 子类bean必须实现的初始化方法
	 * 现已经基本上初始化了值<br/>
	 * <b>
	 * String:""<br/>
	 * Integer:如数据库有要求，则按照数据库要求，没有则初始值是0<br/>
	 * Boolean：false<br/>
	 * time:当前的System.currentTimeMillis()<br/>
	 * </b>
	 * @author fuzr
	 */
	protected abstract void initMethod();
	/**
	 * 父类实现而来copy2方法
	 * 如果遇到null对象则不copy
	 * @author fuzr
	 */
	public void copyTo(KeyObject arg0){
		try {
			BeanUtilsBean bub = BeanUtilsBean.getInstance();
			PropertyDescriptor[] arg0Descriptors = bub.getPropertyUtils().getPropertyDescriptors(arg0);
			for (int i = 0; i < arg0Descriptors.length; i++) {
				String name = arg0Descriptors[i].getName();
				if ("class".equals(name)){
					continue;
				}
				if(bub.getPropertyUtils().isReadable(arg0,name) && bub.getPropertyUtils().isWriteable(this, name)) {
					Object value = bub.getPropertyUtils().getSimpleProperty(arg0,name);
					if(value==null)continue;
					bub.copyProperty(this, name, value);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 给Integer 操作数量加减
	 * @param num
	 * @param count
	 * @return
	 */
	public int addNum(Integer num,int count){
		if(num==null)return 0+count;
		else return num+count;
	}
}
