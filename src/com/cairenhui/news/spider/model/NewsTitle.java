package com.cairenhui.news.spider.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cairenhui.news.spider.base.BaseEntity;

/**
 * NewsTitle
 */
@SuppressWarnings("serial")
public class NewsTitle extends BaseEntity {
		
	//date formats
	
	//columns START
	/**id*/
	private java.lang.Long id;
	/**stockCode*/
	private java.lang.String stockCode;
	/**stockMarketType*/
	private java.lang.Integer stockMarketType;
	/**title*/
	private java.lang.String title;
	/**url*/
	private java.lang.String url;
	/**type*/
	private java.lang.Integer type;
	/**source*/
	private java.lang.String source;
	/**pubDate*/
	private java.lang.Integer pubDate;
	/**flag*/
	private java.lang.Integer flag;
	/**
	 * level 等级
	 */
	private java.lang.Integer level;
	//columns END

	public NewsTitle(){
	}

	public NewsTitle(
		java.lang.Long id
	){
		this.id = id;
	}

	public void setId(java.lang.Long value) {
		this.id = value;
	}
	
	public java.lang.Long getId() {
		return this.id;
	}
	public void setStockCode(java.lang.String value) {
		this.stockCode = value;
	}
	
	public java.lang.String getStockCode() {
		return this.stockCode;
	}
	public void setStockMarketType(java.lang.Integer value) {
		this.stockMarketType = value;
	}
	
	public java.lang.Integer getStockMarketType() {
		return this.stockMarketType;
	}
	public void setTitle(java.lang.String value) {
		this.title = value;
	}
	
	public java.lang.String getTitle() {
		return this.title;
	}
	public void setUrl(java.lang.String value) {
		this.url = value;
	}
	
	public java.lang.String getUrl() {
		return this.url;
	}
	public void setType(java.lang.Integer value) {
		this.type = value;
	}
	
	public java.lang.Integer getType() {
		return this.type;
	}
	public void setSource(java.lang.String value) {
		this.source = value;
	}
	
	public java.lang.String getSource() {
		return this.source;
	}
	public void setPubDate(java.lang.Integer value) {
		this.pubDate = value;
	}
	
	public java.lang.Integer getPubDate() {
		return this.pubDate;
	}
	public void setFlag(java.lang.Integer value) {
		this.flag = value;
	}
	
	public java.lang.Integer getFlag() {
		return this.flag;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("Id",getId())
			.append("StockCode",getStockCode())
			.append("StockMarketType",getStockMarketType())
			.append("Title",getTitle())
			.append("Url",getUrl())
			.append("Type",getType())
			.append("Source",getSource())
			.append("PubDate",getPubDate())
			.append("Flag",getFlag())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.append(getStockCode())
			.append(getStockMarketType())
			.append(getTitle())
			.append(getUrl())
			.append(getType())
			.append(getSource())
			.append(getPubDate())
			.append(getFlag())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof NewsTitle == false) return false;
		if(this == obj) return true;
		NewsTitle other = (NewsTitle)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.append(getStockCode(),other.getStockCode())
			.append(getStockMarketType(),other.getStockMarketType())
			.append(getTitle(),other.getTitle())
			.append(getUrl(),other.getUrl())
			.append(getType(),other.getType())
			.append(getSource(),other.getSource())
			.append(getPubDate(),other.getPubDate())
			.append(getFlag(),other.getFlag())
			.isEquals();
	}

	@Override
	public String getKey() {
		return this.id.toString();
	}
	@Override
	protected void initMethod(){
		this.id=0L;
		this.stockCode="";
		this.stockMarketType=0;
		this.title="";
		this.url="";
		this.type=0;
		this.source="";
		this.pubDate=0;
		this.flag=0;
	}

	public java.lang.Integer getLevel() {
		return level;
	}

	public void setLevel(java.lang.Integer level) {
		this.level = level;
	}
}

