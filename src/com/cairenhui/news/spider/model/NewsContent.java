package com.cairenhui.news.spider.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cairenhui.news.spider.base.BaseEntity;

/**
 * NewsContent
 */
public class NewsContent extends BaseEntity {
	
	private static final long serialVersionUID = -1514340324263609673L;
	//columns START
	/**id*/
	private java.lang.Long id;
	/**titleId*/
	private java.lang.Long titleId;
	/**author*/
	private java.lang.String author;
	/**pubDate*/
	private java.lang.Integer pubDate;
	/**title*/
	private java.lang.String title;
	/**content*/
	private java.lang.String content;
	/**source*/
	private java.lang.String source;
	//columns END

	public NewsContent(){
	}

	public NewsContent(
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
	public void setTitleId(java.lang.Long value) {
		this.titleId = value;
	}
	
	public java.lang.Long getTitleId() {
		return this.titleId;
	}
	public void setAuthor(java.lang.String value) {
		this.author = value;
	}
	
	public java.lang.String getAuthor() {
		return this.author;
	}
	public void setPubDate(java.lang.Integer value) {
		this.pubDate = value;
	}
	
	public java.lang.Integer getPubDate() {
		return this.pubDate;
	}
	public void setTitle(java.lang.String value) {
		this.title = value;
	}
	
	public java.lang.String getTitle() {
		return this.title;
	}
	public void setContent(java.lang.String value) {
		this.content = value;
	}
	
	public java.lang.String getContent() {
		return this.content;
	}
	public void setSource(java.lang.String value) {
		this.source = value;
	}
	
	public java.lang.String getSource() {
		return this.source;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("Id",getId())
			.append("TitleId",getTitleId())
			.append("Author",getAuthor())
			.append("PubDate",getPubDate())
			.append("Title",getTitle())
			.append("Content",getContent())
			.append("Source",getSource())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.append(getTitleId())
			.append(getAuthor())
			.append(getPubDate())
			.append(getTitle())
			.append(getContent())
			.append(getSource())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof NewsContent == false) return false;
		if(this == obj) return true;
		NewsContent other = (NewsContent)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.append(getTitleId(),other.getTitleId())
			.append(getAuthor(),other.getAuthor())
			.append(getPubDate(),other.getPubDate())
			.append(getTitle(),other.getTitle())
			.append(getContent(),other.getContent())
			.append(getSource(),other.getSource())
			.isEquals();
	}

	@Override
	public String getKey() {
		return this.id.toString();
	}
	@Override
	protected void initMethod(){
		this.id=0L;
		this.titleId=0L;
		this.author="";
		this.pubDate=0;
		this.title="";
		this.content="";
		this.source="";
	}
}

