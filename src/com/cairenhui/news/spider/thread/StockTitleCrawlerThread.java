package com.cairenhui.news.spider.thread;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cairenhui.news.spider.SiteParser;
import com.cairenhui.news.spider.SiteParserFactory;
import com.cairenhui.news.spider.constant.Constant;
import com.cairenhui.news.spider.model.NewsStockProfile;
import com.cairenhui.news.spider.model.NewsTitle;
import com.cairenhui.news.spider.pool.StockNewsTitlePool;
import com.cairenhui.news.spider.pool.StockPool;
import com.cairenhui.news.spider.service.LcsService;
import com.cairenhui.news.spider.service.NewsStockProfileService;
import com.cairenhui.news.spider.service.NewsTitleService;
import com.cairenhui.news.spider.util.DateUtil;
import com.cairenhui.news.spider.util.DownloadUtil;
import com.cairenhui.sns.dao.StockProfileDao;
import com.cairenhui.sns.model.stock.StockProfile;
import com.cairenhui.sns.service.StockProfileService;
import com.cairenhui.sns.service.TrieSearchService;

public class StockTitleCrawlerThread implements Runnable {
	private static Logger logger = Logger.getLogger("logger.proxy");
	
	private String defaultEncoding = "UTF-8";
	private StockPool stockPool = StockPool.getInstance();
	private StockNewsTitlePool newsTitlePool = StockNewsTitlePool.getInstance();
	
	private static String newsRegex = "<div\\s*class=\"g-section\\s*news sfe-break-bottom-16\">\\s*"  
								    + "<span[^>]*>\\s*" 
								    + "<a\\s*href=\"([^\"]+)\"[^>]+>([^<]+)</a>\\s*"  //新闻标题,url
								    + "</span>\\s*" 
								    + "<br>\\s*" 
								    + "<div[^>]*>\\s*" 
								    + "<span[^>]*>([^<]+)</span>\\s*-\\s*<span[^>]*>([^<]+)</span>\\s*" 	//新闻来源,发布日期
								    + "</div>\\s*" 
								    + "<div[^>]*>\\s*" 
								    + "<div[^>]*>([^<]+)</div>\\s*";
	private static String pageRegex = "共有约 <b>(\\d*)</b> 条结果";  //新闻数量
	
	private static Pattern newsPattern = Pattern.compile(newsRegex);
	private static Pattern pagePattern = Pattern.compile(pageRegex);
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
	
	@Autowired
	private NewsStockProfileService newsStockProfileService;
	@Autowired
	private StockProfileService stockProfileService;
	@Autowired
	private NewsTitleService newsTitleService;
	@Autowired
	private LcsService lcsService;
	@Autowired
	private TrieSearchService trieSearchService;
	@Autowired
	private StockProfileDao stockProfileDao;

	public StockTitleCrawlerThread() {
		
	}

	@Override
	public void run() {
		while (true) {
			//下载间隔
			int interval = 0;
			try{
				NewsStockProfile stock = stockPool.dequeue();
				if (stock != null) {
					String stockCode = stock.getStockCode();
					String stockName = stock.getStockName();
					String marketType = "";
					
					StockProfile stockProfile = stockProfileService.getByStockCodeAndMarketType(stockCode, marketType);
					if(null == stockProfile){
						continue;
					}
					marketType = stockProfile.getMarketType();
					
					if(marketType.equalsIgnoreCase("sh")){
						stockCode = "SHA:" + stockCode;
					}else if(marketType.equalsIgnoreCase("sz")){
						stockCode =  "SHE:" + stockCode;
					}
					
					String search = null;
					search = "http://www.google.com.hk/finance/company_news?"
						   + "hl=zh-CN&lr=lang_zh-CN&ie=UTF-8&q="
						   + stockCode
						   + "&gl=cn&start=0&num=50";
					
					//下载开始时间
					int startSec = DateUtil.getTimeInSec(new Date());
					
					String html = DownloadUtil.download(search, defaultEncoding);
					this.parse(stockCode, stockName, marketType, html);
					
					//下载结束时间
					int endSec = DateUtil.getTimeInSec(new Date());
					interval = endSec - startSec;
				}else{
//					logger.info("the stockPool is empty.");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				if(interval < 10){
					Thread.sleep(2 * 1000);
				}
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	/**
	 * 获取财经新闻页数
	 * @param html
	 * @return
	 */
	public int parseNum(String html) {
		int pageNum = 0;
		if("".equals(html))
			return 0;
		Matcher matcher = pagePattern.matcher(html);
		if(matcher.find()){
			String numStr = matcher.group(1);
			if(numStr.trim().equalsIgnoreCase("")){
				return 0;
			}
			int num = Integer.parseInt(numStr);
			int mod = num%10;
			pageNum = mod == 0 ? num/10 : num/10 + 1;
		}
		return pageNum;
	}
	
	/**
	 * 解析页面内容
	 * @param stockCode
	 * @param stockName
	 * @param html
	 */
	public void parse(String stockCode, String stockName, String marketType, String html) {
		int pubDate = 0;
		String url = "";
		String source = "";
		String title = "";
		
		Matcher newsMatcher = newsPattern.matcher(html);
		while(newsMatcher.find()){
			try{
				pubDate = parseDate(newsMatcher.group(4));
				url = newsMatcher.group(1);
				source = newsMatcher.group(3);
				title = newsMatcher.group(2);
				
				if(StringUtils.isEmpty(url) || url.indexOf("wap.cnfol.com") != -1){
					continue;
				}
				if(!url.trim().startsWith("http://")){
					continue;
				}
				
				//filter url
				url = filterStockNewsUrl(url);
				
				//非同一天的内容不保存
				Date date = DateUtil.getSecToDate(pubDate);
				if(null != date){
					if(!DateUtil.formatDate(date, "yyyyMMdd").equalsIgnoreCase(String.valueOf(newsTitlePool.getTimeStamp()))){
						continue;
					}
				}
				//已经保存的不在保存
				if(this.newsTitlePool.contains(url)){
					continue;
				}
				//重复检查
				SiteParser site = SiteParserFactory.createSiteField(url);
				if(null != site){
					boolean b = lcsService.hasSimilar(stockCode, title);
					if(b){
						logger.info("检测到重复标题, url = " + url + ", title = " + title);
						this.newsTitlePool.addUrl(url);
						continue;
					}
				}
				//判断标题是否乱码
				if(!title.matches("[\\x00-\\xff\u4E00-\u9FA5\uFE30-\uFFA0]+")){
					continue;
				}
				//非相关股票不保存
				if(!isStockRelative(title, stockCode.substring(4), marketType)){
					this.newsTitlePool.addUrl(url);
					continue;
				}
				
				this.newsTitlePool.addUrl(url);
				
				NewsTitle newsTitle = new NewsTitle();
				newsTitle.setStockCode(stockCode.substring(4));
				newsTitle.setStockMarketType(marketType.equalsIgnoreCase("sh") ? Constant.MARKET_TYPE_SH : Constant.MARKET_TYPE_SZ);
				newsTitle.setTitle(title);
				newsTitle.setUrl(url);
				newsTitle.setType(Constant.NEWS_TYPE_GGZX);
				newsTitle.setSource(source);
				newsTitle.setPubDate(pubDate);
				newsTitle.setFlag(Constant.NEWS_UNDOWNLOAD);
				
				newsTitleService.save(newsTitle);
				logger.info("save title[url = " + newsTitle.getUrl() + ", title = " + newsTitle.getTitle() + "]");
			
				Thread.sleep(100);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 从输入字符串中提取股票标签
	 * @param content
	 * @return
	 */
	public boolean isStockRelative(String content, String stockCode, String marketType) {
		boolean res = false;

		Set<String> stockCodeList = new HashSet<String>();
		
		List<String> keys = new ArrayList<String>();
		try {
			keys = trieSearchService.searchStockTag2(content);
		} catch (Exception e) {
			return false;
		}
		if(keys != null && keys.size() > 0 && !keys.isEmpty()){
			int i = 0;
			for(String k : keys){
				boolean isWord = false;
				int n = 0;
				//判断汉字
				for(int j=0; j<k.length(); j++) {
					n = (int)k.charAt(j);
					if((19968 <= n && n < 40623)) {
						isWord = true;
						break;
					}
				}
				
				if(isWord){
					//查询股票标签信息
					stockProfileDao.setSlaveOk(true);
					List<StockProfile> list = stockProfileDao.findStockByKeyWordsPage(k, 0, 1);
					if(list != null && !list.isEmpty()){
						StockProfile profile = list.get(0);
						stockCodeList.add(profile.getMarketType() + profile.getStockCode());
						i+=2;
					}
				}else{
					StockProfile stockProfile = stockProfileService.getByStockAlias(k);
					
					if(null != stockProfile){
						stockCodeList.add(stockProfile.getMarketType() + stockProfile.getStockCode());
					}
					
					i++;
				}
			}
		}
		res = stockCodeList.contains(marketType + stockCode);
		return res;
	}

	/**
	 * 从google个股资讯url中提取真正的url
	 * @param url
	 * @return
	 */
	Pattern pattern = Pattern.compile("(&amp;|&)url=([\\s\\S]*?)(&amp;|&)cid");
	private String filterStockNewsUrl(String url) {
		String res = url;
		try{
			Matcher matcher = pattern.matcher(url);
			if(matcher.find()){
				String url2 = matcher.group(2);
				if(url2.indexOf("http://") > -1){
					res = URLDecoder.decode(url2, "UTF-8");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}

	private int parseDate(String str) {
		str = str.replaceAll("\\s*", "");
		Pattern hourPattern = Pattern.compile("(\\d*)小时前");
		Matcher hourMatcher = hourPattern.matcher(str);
		Pattern minutePattern = Pattern.compile("(\\d*)分钟前");
		Matcher minuteMatcher = minutePattern.matcher(str);
		Calendar cal = Calendar.getInstance();
		if(str.matches("\\d{4}年\\d{1,2}月\\d{1,2}日")){
			try {
				return DateUtil.getTimeInSec(sdf1.parse(str));
			} catch (ParseException e) {
				e.printStackTrace();
				return DateUtil.getTimeInSec(new Date());
			}
		}else if(hourMatcher.find()){
			cal.setTime(new Date());
			cal.add(Calendar.HOUR_OF_DAY, 0 - Integer.parseInt(hourMatcher.group(1)));
			return DateUtil.getTimeInSec(cal.getTime());
		}else if(minuteMatcher.find()){
			cal.setTime(new Date());
			cal.add(Calendar.MINUTE, 0 - Integer.parseInt(minuteMatcher.group(1)));
			return DateUtil.getTimeInSec(cal.getTime());
		}
		else {
			return DateUtil.getTimeInSec(new Date());
		}
	}
	
	public NewsTitleService getNewsTitleService() {
		return newsTitleService;
	}

	public void setNewsTitleService(NewsTitleService newsTitleService) {
		this.newsTitleService = newsTitleService;
	}

	public NewsStockProfileService getNewsStockProfileService() {
		return newsStockProfileService;
	}

	public void setNewsStockProfileService(NewsStockProfileService newsStockProfileService) {
		this.newsStockProfileService = newsStockProfileService;
	}

	public LcsService getLcsService() {
		return lcsService;
	}

	public void setLcsService(LcsService lcsService) {
		this.lcsService = lcsService;
	}

	public StockProfileService getStockProfileService() {
		return stockProfileService;
	}

	public void setStockProfileService(StockProfileService stockProfileService) {
		this.stockProfileService = stockProfileService;
	}
	
	public static void main(String[] a){
		StockTitleCrawlerThread c = new StockTitleCrawlerThread();
		String url = "http://www.google.com.hk/finance/company_news?hl=zh-CN&lr=lang_zh-CN&ie=UTF-8&q=SHA:600787&gl=cn&start=0&num=50";
		String html = DownloadUtil.download(url, "UTF-8");
		c.parse("SHA:600787", "zc", "sh", html);
	}

	public TrieSearchService getTrieSearchService() {
		return trieSearchService;
	}

	public void setTrieSearchService(TrieSearchService trieSearchService) {
		this.trieSearchService = trieSearchService;
	}

	public StockProfileDao getStockProfileDao() {
		return stockProfileDao;
	}

	public void setStockProfileDao(StockProfileDao stockProfileDao) {
		this.stockProfileDao = stockProfileDao;
	}

}
