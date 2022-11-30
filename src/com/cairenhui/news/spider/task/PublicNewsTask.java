package com.cairenhui.news.spider.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cairenhui.news.spider.constant.Constant;
import com.cairenhui.news.spider.dao.NewsTitleDao;
import com.cairenhui.news.spider.model.NewsTitle;
import com.cairenhui.news.spider.util.DownloadUtil;
import com.cairenhui.sns.dao.MarketNewsDao;
import com.cairenhui.sns.dao.StockProfileDao;
import com.cairenhui.sns.dao.UserProfileDao;
import com.cairenhui.sns.model.stock.StockProfile;
import com.cairenhui.sns.service.MailService;
import com.cairenhui.sns.service.OperationUpdatService;
import com.cairenhui.sns.util.ConfigProperties;
import com.cairenhui.sns.util.DateUtil;

public class PublicNewsTask {
	Logger logger = Logger.getLogger(PublicNewsTask.class);
	
	//邮件配置
	private static final String HOST = ConfigProperties.get("MailHost");
	private static final String MAIL_FROM = ConfigProperties.get("MailFrom");
	private static final String MAIL_TO = ConfigProperties.get("MailTo");
	private static final String AUTH = "true";
	private static final String USERNAME = ConfigProperties.get("MailUserName");
	private static final String PASSWORD = ConfigProperties.get("MailPassword");
	private static final Long SENDUSERID = ConfigProperties.get("sendUserId",0l);
	
	private static final String PATTERN_STR = "^[\u0000-\u00FF\u4E00-\u9FA5\uFF00-\uFFFF]+$";
	
	@Autowired
	public MarketNewsDao marketNewsDao;
	@Autowired
	public NewsTitleDao newsTitleDao;
	@Autowired
	public StockProfileDao stockProfileDao;
	@Autowired
	public UserProfileDao userProfileDao;
	@Autowired
	public MailService mailService;
	@Autowired
	public OperationUpdatService operationUpdatService;
	
	private Map<String,StockProfile> stockMap = new HashMap<String, StockProfile>();
	
//	<li><a href="http://static.sse.com.cn/disclosure/listedinfo/announcement/c/2013-04-11/601727_20130412_1.pdf"  target="_blank">601727 : 上海电气澄清公告</a>
//	&nbsp;<span class="list_date">2013-04-12</span></li>
	String regex = "<li><a\\s*href=\"([\\s\\S]*?)\"[^>]*?>(\\d*) : ([\\s\\S]*?)</a>[\\s\\S]*?<span[^>]*?>([\\s\\S]*?)</span></li>";
	Pattern pattern = Pattern.compile(regex);
//	<tr><td class='td1'></td><td align='left' class='td2'><a href='finalpage/2013-04-12/62351805.PDF' target=new>S ST华新：2013年第一季度业绩预告公告</a>&nbsp;&nbsp;&nbsp;<img border=0 src='images/Pdf.gif' align=absMiddle width=16><font color=#999999>(75 k)</font><span class='link1'>[2013-04-12]</span></td></tr>
	String regex2 = "<tr>[\\s\\S]*?<a\\s*href=\'([\\s\\S]*?)\'[^>]*?>([\\s\\S]{0,10}?)：([\\s\\S]*?)</a>[\\s\\S]*?<span[^>]*?>\\[([\\s\\S]*?)\\]</span></td></tr>";
	Pattern pattern2 = Pattern.compile(regex2);
	
	/**
	 * 抓取最新公告
	 * 上证 http://www.sse.com.cn/disclosure/listedinfo/announcement/s_docdatesort_asc.htm
	 * @return
	 */
	public void crawlerPublicNewsSH(){
		Map<String,List<NewsTitle>> mailMap = new HashMap<String, List<NewsTitle>>();
		String html = DownloadUtil.downloadAsString("http://www.sse.com.cn/disclosure/listedinfo/announcement/s_docdatesort_asc.htm");
		Matcher matcher = pattern.matcher(html);
		int i = 0 ;
		while(matcher.find()){
			try {
				i++;
				String stockCode = matcher.group(2).replaceAll(" ", "");
				StockProfile stockProfile = stockProfileDao.getByParam("stockCode", stockCode);
				if(stockProfile==null)
					stockCode = stockCode+":0";
				else{
					stockCode = stockCode+":"+stockProfile.getStockName();
					if(userProfileDao.getByUserId(stockProfile.getUserId())!=null)
						stockMap.put(stockCode, stockProfile);
				}
				String title = matcher.group(3);
				Integer level = 0 ;
				if(!filterLow(title)) level=1;
				if(!filterHigh(title)) level=9;
				else level=2;
				String url = matcher.group(1);
				String dataString = matcher.group(4);
				Integer time = 0;
				try {
					time = DateUtil.date2secondInt(DateUtil.stringToTimestamp(dataString, "yyyy-MM-dd"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				NewsTitle n = newsTitleDao.getByStockCodeAndTime(stockCode,time,title);
				if(n==null){
					NewsTitle newsTitle = new NewsTitle();
					newsTitle.setFlag(Constant.NEWS_UNDOWNLOAD);
					newsTitle.setPubDate(time);
					newsTitle.setSource("上交所");
					newsTitle.setStockCode(stockCode);
					newsTitle.setStockMarketType(Constant.MARKET_TYPE_SH);
					newsTitle.setTitle(title);
					newsTitle.setType(Constant.NEWS_TYPE_PUBLIC);
					newsTitle.setUrl(url);
					newsTitle.setLevel(level);
					newsTitleDao.save(newsTitle);
					if(mailMap.containsKey(stockCode)){
						List<NewsTitle> l = mailMap.get(stockCode);
						l.add(newsTitle);
					}else{
						List<NewsTitle> l = new ArrayList<NewsTitle>();
						l.add(newsTitle);
						mailMap.put(stockCode, l);
					}
					logger.info("股票代码:"+stockCode+"  标题:"+title+"  pdf地址:"+matcher.group(1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sendMail(mailMap);
		sendBlog(mailMap);
		stockMap=new HashMap<String, StockProfile>();
	}
	
	/**
	 * 抓取最新公告
	 * //	深证 http://disclosure.szse.cn/m/drgg.htm
	 * @return
	 */
	public void crawlerPublicNewsSZ(){
		Map<String,List<NewsTitle>> mailMap = new HashMap<String, List<NewsTitle>>();
		String html = DownloadUtil.downloadAsString("http://disclosure.szse.cn/m/drgg.htm");
		Matcher matcher2 = pattern2.matcher(html);
		int i = 0 ;
		while(matcher2.find()){
			try {
				i++;
				String stockName = matcher2.group(2).replaceAll(" ", "");;
				StockProfile stockProfile = stockProfileDao.getByParam("stockName", stockName);
				String stockCode = "";
				if(stockProfile==null)
					stockCode="0:"+stockName;
				else{
					stockCode=stockProfile.getStockCode()+":"+stockName;
					if(userProfileDao.getByUserId(stockProfile.getUserId())!=null)
						stockMap.put(stockCode, stockProfile);
				}
				String title = matcher2.group(3);
				Integer level = 0 ;
				if(!filterLow(title)) level=1;
				if(!filterHigh(title)) level=9;
				else level=2;
				String dataString = matcher2.group(4);
				Integer time = 0;
				try {
					time = DateUtil.date2secondInt(DateUtil.stringToTimestamp(dataString, "yyyy-MM-dd"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				NewsTitle n = newsTitleDao.getByStockCodeAndTime(stockCode,time,title);
				if(n==null){
					String url = "http://disclosure.szse.cn/m/"+matcher2.group(1);
					NewsTitle newsTitle = new NewsTitle();
					newsTitle.setFlag(Constant.NEWS_UNDOWNLOAD);
					newsTitle.setPubDate(time);
					newsTitle.setSource("深交所");
					newsTitle.setStockCode(stockCode);
					newsTitle.setStockMarketType(Constant.MARKET_TYPE_SZ);
					newsTitle.setTitle(title);
					newsTitle.setType(Constant.NEWS_TYPE_PUBLIC);
					newsTitle.setUrl(url);
					newsTitle.setLevel(level);
					newsTitleDao.save(newsTitle);
					if(mailMap.containsKey(stockCode)){
						List<NewsTitle> l = mailMap.get(stockCode);
						l.add(newsTitle);
					}else{
						List<NewsTitle> l = new ArrayList<NewsTitle>();
						l.add(newsTitle);
						mailMap.put(stockCode, l);
					}
					logger.info("股票代码:"+stockCode+"  标题:"+matcher2.group(3)+"  pdf地址:http://disclosure.szse.cn/m/"+matcher2.group(1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sendMail(mailMap);
		sendBlog(mailMap);
		stockMap=new HashMap<String, StockProfile>();
	}
	
	/**
	 * 对非重要公告降权
	 * @param title
	 * @return
	 */
	public boolean filterLow(String title){
		String keys = "监事、担保、自我评价、独立董事、公司章程、审计报告、补充流动资金、增资";
		String[]keyArray = keys.split("、");
		for (String string : keyArray) {
			if(title.indexOf(string)>0) return false;
		}
		return true;
	}
	
	/**
	 * 对非重要公告降权
	 * @param title
	 * @return
	 */
	public boolean filterHigh(String title){
		String keys = "重组、停牌、非公开、增持、减持、快报、风险警示、复牌、修正、权益分派、年度报告、一季度报告、三季度报告、半年度报告、限售、预告";
		String[]keyArray = keys.split("、");
		for (String string : keyArray) {
			if(title.indexOf(string)>0) return false;
		}
		return true;
	}
	
	/**
	 * 发邮件
	 * @param map
	 * @throws Exception 
	 */
	public void sendMail(Map<String,List<NewsTitle>> map){
		if(map==null||map.isEmpty()) return;
		Set<Entry<String, List<NewsTitle>>> s = map.entrySet();
		for (Entry<String, List<NewsTitle>> entry : s) {
			try {
				String title = "";
				title = entry.getKey()+"公告";
				List<NewsTitle> l = entry.getValue();
				for (NewsTitle newsTitle : l) {
					if(newsTitle.getLevel()>3) title=title+"※";
				}
				String content = getMailContent(l);
				mailService.send(HOST, MAIL_FROM, "爬虫", AUTH, USERNAME, PASSWORD, MAIL_TO, title, content);
				Thread.sleep(50l);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送博文
	 * @param map
	 * @throws InterruptedException 
	 */
	public void sendBlog(Map<String,List<NewsTitle>> map){
		if(map==null||map.isEmpty()) return;
		Set<Entry<String, List<NewsTitle>>> s = map.entrySet();
		for (Entry<String, List<NewsTitle>> entry : s) {
			try {
				String[] names = entry.getKey().split(":");
				String stockName = "";
				String date = "";
				Long sendUserId = 0l;
				int contentType = 0;
				if(!"0".equals(names[1])) stockName = names[1];
				else stockName = names[0];
				
				//1. 组装博文内容
				List<NewsTitle> l = entry.getValue();
				StringBuffer contentSb = new StringBuffer();
				int i = 0;
				for (NewsTitle newsTitle : l) {
					i++;
					String content = newsTitle.getTitle()+" : "+newsTitle.getUrl();
					if(i==l.size()) {
						date = DateUtil.intToString(newsTitle.getPubDate(), "yyyy-MM-dd");
						contentType = newsTitle.getStockMarketType();
						if(stockMap.containsKey(newsTitle.getStockCode())){
							sendUserId=stockMap.get(newsTitle.getStockCode()).getUserId();
						}else{
							sendUserId=SENDUSERID;
						}
					}
					contentSb.append(content);
					if(i<l.size()) 
						contentSb.append("<br/>");
				}
				String content = contentSb.toString();
				
				//2. 组装博文标题
				String title = "";
				if(contentType==Constant.MARKET_TYPE_SH){
					title = "上交所发布了关于\""+stockName+"\"的"+i+"条新公告"+"["+date+"]";
				}else if(contentType==Constant.MARKET_TYPE_SZ){
					title = "深交所发布了关于\""+stockName+"\"的"+i+"条新公告"+"["+date+"]";
				}
				//发布博文
				operationUpdatService.launchBlogAndReturn(sendUserId, title, content, 1, 0,"", "", 0l,"");
				Thread.sleep(500l);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getMailContent(List<NewsTitle> l){
		StringBuffer contentSb = new StringBuffer();
		contentSb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		contentSb.append("<html>");
		contentSb.append("<head>");
		contentSb.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\">");
		contentSb.append("<title>和我一起在财人汇上快乐理财吧</title>");
		contentSb.append("</head>");
		contentSb.append("<body style=\"background:#fffee8;\">");
		for (NewsTitle newsTitle : l) {
			String content = newsTitle.getTitle()+" : <a href='"+newsTitle.getUrl()+"'>查看详情</a>";
			contentSb.append(content);
			contentSb.append("<br/>");
		}
		contentSb.append("</body>");
		contentSb.append("</html>");
		return contentSb.toString();
	}
	
	public void crawlerNews(){
		crawlerPublicNewsSH();
//		crawlerPublicNewsSZ();
	}
	
	
	public static void main(String[] args) {
//		new PublicNewsTask().crawlerPublicNewsSH();
//		new PublicNewsTask().crawlerPublicNewsSZ();
//		String html = DownloadUtil.downloadAsString("http://www.sse.com.cn/disclosure/listedinfo/announcement/s_docdatesort_asc.htm","utf-8");
//		System.out.println(html);
	}
	
}
