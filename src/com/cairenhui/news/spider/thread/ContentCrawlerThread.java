package com.cairenhui.news.spider.thread;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.cairenhui.news.spider.SiteParser;
import com.cairenhui.news.spider.SiteParserFactory;
import com.cairenhui.news.spider.constant.Constant;
import com.cairenhui.news.spider.model.NewsContent;
import com.cairenhui.news.spider.model.NewsTitle;
import com.cairenhui.news.spider.pool.NewsTitlePool;
import com.cairenhui.news.spider.service.NewsContentService;
import com.cairenhui.news.spider.service.NewsTitleService;
import com.cairenhui.news.spider.util.DateUtil;
import com.cairenhui.news.spider.util.DownloadUtil;

/**
 * 资讯内容下载线程
 * @author chengyy
 *
 */
public class ContentCrawlerThread implements Runnable {
	private NewsTitlePool pool = NewsTitlePool.getInstance();
	private static Logger logger = Logger.getLogger(ContentCrawlerThread.class);
	
	private NewsContentService newsContentService;
	
	private NewsTitleService newsTitleService;
	
	@Override
	public void run() {
		while(true){
			//下载间隔
			int interval = 0;
			try{
				NewsTitle newsTitle = pool.dequeue();
				if(null != newsTitle){
					String url = newsTitle.getUrl();
					long titleId = newsTitle.getId();
					String t = null == newsTitle.getTitle() ? "" : newsTitle.getTitle();
					
					SiteParser site = SiteParserFactory.createSiteField(url);
					if(null != site){
						//开始时间
						int startSec = DateUtil.getTimeInSec(new Date());
						
						String html = DownloadUtil.download(url, site.getEncoding());
						if(StringUtils.isNotEmpty(html)){
							site.doParse(html);

							String author = site.getAuthor();
							if (author == null || "".equals(author.trim()) == true) {
								author = " ";
							}

							String pubDate = site.getDate();
							if (pubDate == null || "".equals(pubDate.trim()) == true) {
								pubDate = " ";
							}

							String title = site.getTitle();
							if (title == null || "".equals(title.trim()) == true) {
								title = " ";
							}

							String content = site.getBody();
							if (content == null || "".equals(content.trim()) == true) {
								content = " ";
							}
//							if(content.length() >= 2000){
//								content = content.substring(0, 2000) + "[更多]";
//							}

							String source = site.getSource();
							if (source == null || "".equals(source.trim()) == true) {
								source = " ";
							}
							
							int dateInt = DateUtil.getTimeInSec(DateUtil.parseDate(pubDate, "yyyy-MM-dd HH:mm:ss"));
							if(StringUtils.isNotEmpty(content.trim()) && dateInt > 0){
								NewsContent newsContent = new NewsContent();
								newsContent.setTitleId(titleId);
								newsContent.setAuthor(author);
								newsContent.setPubDate(dateInt);
								newsContent.setContent(content);
								newsContent.setSource(source);
								if(StringUtils.isNotEmpty(t)){
									newsContent.setTitle(t);
								}else{
									newsContent.setTitle(title);
								}
								
								try{
									this.newsContentService.save(newsContent);
									if(StringUtils.isNotEmpty(title) && StringUtils.isEmpty(t)){
										newsTitle.setTitle(title);
									}
									newsTitle.setFlag(Constant.DOWNLOAD_SUCC);
									this.newsTitleService.update(newsTitle);
									
									logger.info("保存新闻[title = " + newsContent.getTitle() + ", url = " + url + "]");
								}catch(Exception e){
									e.printStackTrace();
									//保存新闻内容出错
									newsTitle.setFlag(Constant.SAVE_FAIL);
									this.newsTitleService.update(newsTitle);
								}
							}else{
								//解析内容失败
								newsTitle.setFlag(Constant.DOWNLOAD_PARS);
								this.newsTitleService.update(newsTitle);
							}
						}else{
							//下载网页失败
							System.out.println("下载失败，url = " + url);
							newsTitle.setFlag(Constant.DOWNLOAD_FAIL);
							this.newsTitleService.update(newsTitle);
						}
						//结束时间
						int endSec = DateUtil.getTimeInSec(new Date());
						interval = endSec - startSec;
					}else{
						//缺少对该网站内容的解析
						newsTitle.setFlag(Constant.DOWNLOAD_CANC);
						this.newsTitleService.update(newsTitle);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				if(interval < 10){
					Thread.sleep(2 * 1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public NewsContentService getNewsContentService() {
		return newsContentService;
	}

	public void setNewsContentService(NewsContentService newsContentService) {
		this.newsContentService = newsContentService;
	}

	public NewsTitleService getNewsTitleService() {
		return newsTitleService;
	}

	public void setNewsTitleService(NewsTitleService newsTitleService) {
		this.newsTitleService = newsTitleService;
	}

}
