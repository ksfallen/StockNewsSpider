package com.cairenhui.news.spider.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cairenhui.news.spider.constant.Constant;
import com.cairenhui.news.spider.model.NewsTitle;
import com.cairenhui.news.spider.service.NewsTitleService;
import com.cairenhui.news.spider.util.DateUtil;
import com.cairenhui.news.spider.util.DownloadUtil;

/**
 * 定时抓取股票新闻标题、url(与个股无关)
 * @author chengyy
 *
 */
public class TitleTask {
	private List<String> urlList = new ArrayList<String>();
	private int timeStamp = 0;
	String defaultEncoding = "GB2312";
	private static Logger logger = Logger.getLogger(TitleTask.class);
	
	boolean isInit = false;
	
	@Autowired
	private NewsTitleService newsTitleService;
	
	public void process(){
		int currentTimeStamp = Integer.parseInt(DateUtil.formatDate(new Date(), "yyyyMMdd"));
		if(currentTimeStamp != timeStamp){
			synchronized(this.urlList){
				this.urlList.clear();
			}
			this.timeStamp = currentTimeStamp;
		}
		//初始化
		if(!isInit){
			init();
			isInit = true;
		}
		//资讯汇总（新浪）(交易日发布，6-18点后好像就没有新的资讯了)
		this.sinaFutureSpider();
		//期货焦点（和讯）(交易日发布，6-18点后好像就没有新的资讯了)
		this.hexunFutureSpider();
		//新浪操盘必读(每周日15——18点之间发布，只有一篇)
		this.sinaCpbdSpider();
		//新浪个股点评(主要集中在每天8——18点之间)
		this.sinaGgdpSpider();
		//新浪股市及时雨(每天每时段都有)
		this.sinaJsySpider();
		//中国证券网实时播报(交易日 8——18点之间)
		this.cnstockSsbbSpider();
		//新浪市场研究(每天 0——10点之间)
		this.sinaMarketSpider();
		//和讯行业研究(交易日 8——18点之间)
		this.hexunYanjiuSpider();
		//新浪基金动态
		this.sinaFundSpider();
	}
	
	/**
	 * 加载今日新闻url
	 */
	private void init() {
		List<NewsTitle> list = this.newsTitleService.findTodayNewsTitle();
		if(null != list && list.size() > 0){
			for(NewsTitle newsTitle : list){
				String url = newsTitle.getUrl();
				if(!this.urlList.contains(url)){
					urlList.add(url);
				}
			}
		}
	}

	/**
	 * 新浪基金动态
	 */
	private void sinaFundSpider() {
		String url = "http://roll.finance.sina.com.cn/finance/jj4/jjdt/index.shtml";
		String regex = "<li>"
			         + "<a\\s*href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>"
			         + "<span>\\(([^\\)]*?)\\)</span>"
			         + "</li>";
		String format = "MM月dd日 HH:mm";
		
		List<NewsTitle> list = this.getTitles(url, defaultEncoding, regex, 1, 2, 3, format);
		
		if(null != list && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				NewsTitle bean = list.get(i);
				bean.setType(Constant.NEWS_TYPE_JJDT);
				bean.setSource("sina");
				bean.setFlag(Constant.NEWS_UNDOWNLOAD);
				
				this.newsTitleService.save(bean);
				logger.info("save title[url = " + bean.getUrl() + ", title = " + bean.getTitle() + "]");
			}
		}
	}

	/**
	 * 和讯行业研究
	 */
	private void hexunYanjiuSpider() {
		String url = "http://stock.hexun.com/yanjiu/index.html";
		String regex = "<li>\\s*"
					 + "<span[^>]*?><div[^>]*?>\\(([^\\)]*?)\\)</div>.*?</span>\\s*"
					 + "<a\\s*href=\'([^\']*?)\'[^>]*?>([^<]*?)</a>\\s*"
					 + "</li>";
		String format = "MM/dd HH:mm";
		
		List<NewsTitle> list = this.getTitles(url, defaultEncoding, regex, 2, 3, 1, format);
		
		if(null != list && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				NewsTitle bean = list.get(i);
				bean.setType(Constant.NEWS_TYPE_RESEARCH);
				bean.setSource("hexun");
				bean.setFlag(Constant.NEWS_UNDOWNLOAD);
				
				this.newsTitleService.save(bean);
				logger.info("save title[url = " + bean.getUrl() + ", title = " + bean.getTitle() + "]");
			}
		}
	}

	/**
	 * 新浪市场研究
	 */
	private void sinaMarketSpider() {
		String url = "http://finance.sina.com.cn/column/marketresearch.shtml";
		String regex = "<td[^>]*?>"
    		 		 + "<a\\s*href=([^\\s]*)[^>]*?>([^<]*?)</a>"
    		 		 + "<font[^>]*?>\\(([^\\)]*?)\\)</font>"
    		 		 + "</td>";
		String format = "dd日HH:mm";
		
		List<NewsTitle> list = this.getTitles(url, defaultEncoding, regex, 1, 2, 3, format);
		
		if(null != list && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				NewsTitle bean = list.get(i);
				bean.setType(Constant.NEWS_TYPE_RESEARCH);
				bean.setSource("sina");
				bean.setFlag(Constant.NEWS_UNDOWNLOAD);
				
				this.newsTitleService.save(bean);
				logger.info("save title[url = " + bean.getUrl() + ", title = " + bean.getTitle() + "]");
			}
		}
	}

	/**
	 * 中国证券网实时播报
	 */
	private void cnstockSsbbSpider() {
		String url = "http://live.cnstock.com/zbtbts/xml/zbtbts_list_1.xml?_=" + System.currentTimeMillis();
		String regex = "<Item\\s*href=\"([^\"]*?)\">\\s*"
					 + "<Title><!\\[CDATA\\[([^]]*?)]]></Title>\\s*"
					 + "<SubTitle><!\\[CDATA\\[[^]]*?]]></SubTitle>\\s*"
					 + "<ListTitle><!\\[CDATA\\[[^]]*?]]></ListTitle>\\s*"
					 + "<Intro><!\\[CDATA\\[[^]]*?]]></Intro>\\s*" 
					 + "<Image\\s*src=\'[^\']*?\'></Image>\\s*" 
					 + "<Date>([^<]*?)</Date>\\s*"
					 + "</Item>";
		String format = "yyyy-MM-dd HH:mm:ss";
		
		List<NewsTitle> list = this.getTitles(url, "GBK", regex, 1, 2, 3, format);
		
		if(null != list && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				NewsTitle bean = list.get(i);
				bean.setType(Constant.NEWS_TYPE_JSY);
				bean.setSource("cnstock");
				bean.setFlag(Constant.NEWS_UNDOWNLOAD);
				
				this.newsTitleService.save(bean);
				logger.info("save title[url = " + bean.getUrl() + ", title = " + bean.getTitle() + "]");
			}
		}
	}

	/**
	 * 新浪股市及时雨
	 */
	private void sinaJsySpider() {
		String url = "http://finance.sina.com.cn/column/jsylist.shtml";
		String regex = "<td[^>]*?>"
	         		 + "<a\\s*href=([^\\s]*)[^>]*?>([^<]*?)</a>"
	         		 + "<font[^>]*?>\\(([^\\)]*?)\\)</font>"
	         		 + "</td>";
		String format = "dd日HH:mm";
		
		List<NewsTitle> list = this.getTitles(url, defaultEncoding, regex, 1, 2, 3, format);
		
		if(null != list && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				NewsTitle bean = list.get(i);
				bean.setType(Constant.NEWS_TYPE_JSY);
				bean.setSource("sina");
				bean.setFlag(Constant.NEWS_UNDOWNLOAD);
				
				this.newsTitleService.save(bean);
				logger.info("save title[url = " + bean.getUrl() + ", title = " + bean.getTitle() + "]");
			}
		}
	}

	/**
	 * 新浪个股点评
	 */
	private void sinaGgdpSpider() {
		String url = "http://finance.sina.com.cn/column/ggdplist.shtml";
		String regex = "<td[^>]*?>"
			         + "<a\\s*href=([^\\s]*)[^>]*?>([^<]*?)</a>"
			         + "<font[^>]*?>\\(([^\\)]*?)\\)</font>"
			         + "</td>";
		String format = "dd日HH:mm";
		
		List<NewsTitle> list = this.getTitles(url, defaultEncoding, regex, 1, 2, 3, format);
		
		if(null != list && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				NewsTitle bean = list.get(i);
				bean.setType(Constant.NEWS_TYPE_GGDP);
				bean.setSource("sina");
				bean.setFlag(Constant.NEWS_UNDOWNLOAD);
				
				this.newsTitleService.save(bean);
				logger.info("save title[url = " + bean.getUrl() + ", title = " + bean.getTitle() + "]");
			}
		}
	}

	/**
	 * 新浪操盘必读
	 */
	private void sinaCpbdSpider() {
		String url = "http://finance.sina.com.cn/focus/cpbd/index.shtml";
		String regex = "<li>"
			         + "<a\\s*href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>"
			         + "<span>\\(([^\\)]*?)\\)</span>"
			         + "</li>";
		String format = "yyyy-MM-dd HH:mm:ss";
		
		List<NewsTitle> list = this.getTitles(url, defaultEncoding, regex, 1, 2, 3, format);
		
		if(null != list && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				NewsTitle bean = list.get(i);
				bean.setType(Constant.NEWS_TYPE_CPBD);
				bean.setSource("sina");
				bean.setFlag(Constant.NEWS_UNDOWNLOAD);
				
				this.newsTitleService.save(bean);
				logger.info("save title[url = " + bean.getUrl() + ", title = " + bean.getTitle() + "]");
			}
		}
	}

	/**
	 * 期货焦点（和讯）
	 */
	private void hexunFutureSpider() {
		String url = "http://futures.hexun.com/focus/index.html";
		String regex = "<li><span>"
					 + "<a[^>]*?>[^<]*?</a>\\s*\\(([^\\)]*?)\\)\\s*</span>"
					 + "<a\\s*href=\'([^\']*?)\'[^>]*?>([^<]*?)</a>"
					 + "</li>";
		String format = "MM-dd HH:mm";
		
		List<NewsTitle> list = this.getTitles(url, defaultEncoding, regex, 2, 3, 1, format);
		
		if(null != list && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				NewsTitle bean = list.get(i);
				bean.setType(Constant.NEWS_TYPE_QHHZ);
				bean.setSource("hexun");
				bean.setFlag(Constant.NEWS_UNDOWNLOAD);
				
				this.newsTitleService.save(bean);
				logger.info("save title[url = " + bean.getUrl() + ", title = " + bean.getTitle() + "]");
			}
		}
	}

	/**
	 * 抓取新浪汇总
	 */
	private void sinaFutureSpider() {
		String url = "http://finance.sina.com.cn/futuremarket/info_all.html";
		String regex = "<li>"
			         + "<a\\s*href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>"
			         + "<span>\\(([^\\)]*?)\\)</span>"
			         + "</li>";
		String format = "yyyy-MM-dd HH:mm:ss";
		
		List<NewsTitle> list = this.getTitles(url, defaultEncoding, regex, 1, 2, 3, format);
		
		if(null != list && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				NewsTitle bean = list.get(i);
				bean.setType(Constant.NEWS_TYPE_QHHZ);
				bean.setSource("sina");
				bean.setFlag(Constant.NEWS_UNDOWNLOAD);
				
				this.newsTitleService.save(bean);
				logger.info("save title[url = " + bean.getUrl() + ", title = " + bean.getTitle() + "]");
			}
		}
	}

	private List<NewsTitle> getTitles(String url, String encoding, String regex, int urlGroup, int titleGroup, int dateGroup, String format) {
		List<NewsTitle> res = new ArrayList<NewsTitle>();
		
		String html = DownloadUtil.download(url, encoding);
		if (StringUtils.isNotEmpty(html)) {
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(html);
			while (matcher.find()) {
				String link = matcher.group(urlGroup);
				String title = matcher.group(titleGroup).replaceAll("<[^>]*?>", "");
				String dateStr = matcher.group(dateGroup);
				int dateInt = 0;
				
				if(StringUtils.isEmpty(link) || StringUtils.isEmpty(title) || StringUtils.isEmpty(dateStr)){
					continue;
				}
				if(link.indexOf("http") == -1){
					continue;
				}
				//非同一天的内容不保存
				Date date = DateUtil.parseDate(dateStr, format, true);
				if(null != date){
					if(!DateUtil.formatDate(date, "yyyyMMdd").equalsIgnoreCase(String.valueOf(timeStamp))){
						break;
					}
					dateInt = DateUtil.getTimeInSec(date);
				}
				//已经保存的不在保存
				if(this.urlList.contains(link)){
					break;
				}
				
				NewsTitle bean = new NewsTitle();
				bean.setUrl(link.trim());
				bean.setTitle(title.trim());
				bean.setPubDate(dateInt);
				
				res.add(bean);
				this.urlList.add(link);
			}
		}
		return res;
	}
	
	public static void main(String[] a){
		TitleTask task = new TitleTask();
		task.process();
	}
}
