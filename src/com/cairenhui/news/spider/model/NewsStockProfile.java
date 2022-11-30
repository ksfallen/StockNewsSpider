package com.cairenhui.news.spider.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cairenhui.news.spider.base.BaseEntity;

/**
 * NewsStockProfile
 */
public class NewsStockProfile extends BaseEntity {
		
	//date formats
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//columns START
	/**id*/
	private java.lang.Integer id;
	/**stockCode*/
	private java.lang.String stockCode;
	/**stockName*/
	private java.lang.String stockName;
	/**stockAsNames*/
	private java.lang.String stockAsNames;
	//columns END

	public NewsStockProfile(){
	}

	public NewsStockProfile(
		java.lang.Integer id
	){
		this.id = id;
	}

	public void setId(java.lang.Integer value) {
		this.id = value;
	}
	
	public java.lang.Integer getId() {
		return this.id;
	}
	public void setStockCode(java.lang.String value) {
		this.stockCode = value;
	}
	
	public java.lang.String getStockCode() {
		return this.stockCode;
	}
	public void setStockName(java.lang.String value) {
		this.stockName = value;
	}
	
	public java.lang.String getStockName() {
		return this.stockName;
	}
	public void setStockAsNames(java.lang.String value) {
		this.stockAsNames = value;
	}
	
	public java.lang.String getStockAsNames() {
		return this.stockAsNames;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("Id",getId())
			.append("StockCode",getStockCode())
			.append("StockName",getStockName())
			.append("StockAsNames",getStockAsNames())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.append(getStockCode())
			.append(getStockName())
			.append(getStockAsNames())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof NewsStockProfile == false) return false;
		if(this == obj) return true;
		NewsStockProfile other = (NewsStockProfile)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.append(getStockCode(),other.getStockCode())
			.append(getStockName(),other.getStockName())
			.append(getStockAsNames(),other.getStockAsNames())
			.isEquals();
	}

	@Override
	public String getKey() {
		return this.id.toString();
	}
	@Override
	protected void initMethod(){
		this.id=0;
		this.stockCode="";
		this.stockName="";
		this.stockAsNames="";
	}
}

