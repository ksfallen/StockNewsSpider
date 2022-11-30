package com.cairenhui.news.spider.task;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.cairenhui.news.spider.model.NewsContent;
import com.cairenhui.news.spider.util.ConfigUtil;
import com.cairenhui.news.spider.util.DownloadUtil;
import com.cairenhui.sns.dao.MarketNewsDao;
import com.cairenhui.sns.model.miniblog.MarketNews;
import com.cairenhui.sns.util.DateUtil;

public class MarketNewsTask {
	
	@Autowired
	public MarketNewsDao marketNewsDao;
	
	String regex2 = "var\\s*date1=([\\d]+)[\\s\\S]*?<a\\s*data-news-id=\"[\\d]+\"[^>]*?>\\s*<p[^>]*>([\\s\\S]*?)(<span[^>]*>\\[more\\]</span>)??</p>[\\s\\S]*?" +
	"</a>(\\s*<div[\\s\\S]{0,500}?消息来源 : ([\\s\\S]*?)</p>)?";
	Pattern pattern2 = Pattern.compile(regex2);
	
	String regex3 = "<a\\s*?data-click[^>]*href=\"([\\s\\S]*?)\"[^>]*?>[\\s\\S]*?</a>";
	Pattern pattern3 = Pattern.compile(regex3);
	
	/**
	 * 抓取当天新闻
	 * @return
	 */
	public List<NewsContent> crawlerNews(){
		List<NewsContent> res = new ArrayList<NewsContent>();
		String html = DownloadUtil.downloadAsString("http://live.wallstreetcn.com/china");
		Matcher matcher2 = pattern2.matcher(html);
		System.out.println(html);
		int i = 0 ;
		while(matcher2.find()){
//			System.out.println(html.substring(matcher2.start(), matcher2.end()));
			i++;
			String time = matcher2.group(1);
			String content = matcher2.group(2);
			MarketNews marketNews = marketNewsDao.getByParam("sourceReleaseTime", Integer.parseInt(time));
			content = content.replaceAll("<[a-zA-Z\\d\\s]*/?>|\\\r|\\\n", "");
			System.out.println(content);
			if(marketNews!=null){
				//该新闻已经存在 更新之
				if(!content.equals(marketNews.getContent())){
					MarketNews marketNewsUpdate = new MarketNews();
					marketNewsUpdate.setId(marketNews.getId());
					marketNewsUpdate.setStatus(3);
					marketNewsUpdate.setUpdateTime(DateUtil.getIntSecondsNow());
					marketNewsUpdate.setContent(content);
					marketNewsDao.update(marketNewsUpdate);
				}
				continue;
			}
			String urlContent = matcher2.group(5);
			String url = null;
			if(urlContent!=null){
				String regexRef = "<a\\s*href=\"([^>]*?)\"[^>]*?>[\\s\\S]*</a>";
				Pattern patternRef = Pattern.compile(regexRef);
				Matcher matcherRef = patternRef.matcher(urlContent);
				if(matcherRef.find()){
					url = matcherRef.group(1);
				}else{
					//通过百度查询来源
					url = findUrlBybaidu(content,urlContent);
					try {
						Thread.sleep(1000l);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			//该新闻不存在 新增
			marketNews = new MarketNews();
			marketNews.setContent(content);
			marketNews.setSourceReleaseTime(Integer.parseInt(time));
			marketNews.setIssueTime(DateUtil.getIntSecondsNow());
			marketNews.setUpdateTime(DateUtil.getIntSecondsNow());
			marketNews.setSourceUrl(url);
			marketNews.setStatus(1);
			marketNewsDao.save(marketNews);
		}
		System.out.println(i);
		
		return res;
		
	}
	
	/**
	 * 从百度查询消息来源
	 * @param content
	 * @param source
	 * @return
	 */
	public String findUrlBybaidu(String content,String source){
		String url = null;
		String keyCotent = content.substring(0,content.length()>15?15:content.length());
		String key = keyCotent.replaceAll("【", "").replaceAll("】", "");
		String web = ConfigUtil.getSiteValue(source);
		if(web==null) return null;
		try {
			String html = DownloadUtil.downloadAsString("http://www.baidu.com/s?wd="+URLEncoder.encode(key, "UTF-8")+"+site%3A"+web);
			Matcher matcher3 = pattern3.matcher(html);
			while(matcher3.find()){
				url = matcher3.group(1);
				return url;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void updateNews(){
		
	}
	
	public static void main(String[] args) {}
	
}
