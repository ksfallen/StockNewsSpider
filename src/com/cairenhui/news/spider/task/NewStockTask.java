package com.cairenhui.news.spider.task;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.cairenhui.news.spider.model.NewsContent;
import com.cairenhui.news.spider.util.DownloadUtil;
import com.cairenhui.sns.dao.MarketNewsDao;

public class NewStockTask {
	
	@Autowired
	public MarketNewsDao marketNewsDao;
	
	String regex = "var ztRUwLJd = (\\{[\\s\\S]*?\\});";
	Pattern pattern = Pattern.compile(regex);
	
	/**
	 * 抓上会通过的公司信息
	 * http://data.eastmoney.com/xg/gh/data.aspx?type=list&style=all&status=3&page=1&pageSize=100&sortType=B&sortRule=-1&jsname=ztRUwLJd
	 * @return
	 */
	public List<NewsContent> crawlerNewStock(){
		List<NewsContent> res = new ArrayList<NewsContent>();
		String html = DownloadUtil.downloadAsString("http://data.eastmoney.com/xg/gh/data.aspx?type=list&style=all&status=3&page=1&pageSize=100&sortType=B&sortRule=-1&jsname=ztRUwLJd");
		Matcher matcher = pattern.matcher(html);
		String jsonStr = new String();
		if(matcher.find()){
			jsonStr = matcher.group(1);
		}
		try {
			JSONObject jo = new JSONObject(jsonStr);
			JSONArray ja = jo.getJSONArray("data");
			for (int i = 0 ; i<ja.length();i++) {
				String stockInfo = ja.get(i).toString();
				String stockInfos[] = stockInfo.split(",");
				System.out.println("===========================================================");
				for (int j =0 ;j<stockInfos.length ;j++) {
					switch (j) {
						case 0:
							System.out.println("公司名称:"+stockInfos[j]);
							break;
						case 1:
							System.out.println("申报日期:"+stockInfos[j]);
							break;
						case 2:
							System.out.println("当前状态:"+stockInfos[j]);
							break;
						case 3:
							System.out.println("上会日期:"+stockInfos[j]);
							break;
						case 4:
							System.out.println("申购日期:"+stockInfos[j]);
							break;
						case 5:
							System.out.println("上市日期:"+stockInfos[j]);
							break;
						case 6:
							System.out.println("拟发行数量(万):"+stockInfos[j]);
							break;
						case 7:
							System.out.println("拟上市地点:"+stockInfos[j]);
							break;
						case 8:
							System.out.println("详细概况:"+"http://data.eastmoney.com/xg/gh/detail/corp_"+stockInfos[j]+".html");
							break;
						case 9:
							System.out.println("专题:"+stockInfos[j]);
							break;
						case 10:
							System.out.println("公告:"+stockInfos[j]);
							break;
					}
				}
				System.out.println("===========================================================");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(jsonStr);
		return res;
		
	}
	
	String regex2 = "<a\\s*?href=\"([\\s\\S]*?)\">[\\s\\S]*?招股说明书[\\s\\S]*?</a>";
	Pattern pattern2 = Pattern.compile(regex2);
//	http://www.csrc.gov.cn/pub/zjhpublic/G00306202/201203/P020120307551329840426.pdf
//	<a href="./P020120307551329840426.pdf">哈尔滨博实自动化股份有限公司首次公开发行股票招股说明书（申报稿）.pdf</a>
	public void crawlerStockInfo(String url){
		String html = DownloadUtil.downloadAsString(url);
//		System.out.println(html);
		Matcher matcher2 = pattern2.matcher(html);
		if(matcher2.find()){
			System.out.println(matcher2.group(1).replaceFirst("\\.", url.substring(0, url.lastIndexOf("/"))));
		}
	}
	
	public static void main(String[] args) {
//		new NewStockTask().crawlerNewStock();
		new NewStockTask().crawlerStockInfo("http://www.csrc.gov.cn/pub/zjhpublic/G00306202/201203/t20120307_206826.htm");
		
	}
	
}
