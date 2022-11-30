package com.cairenhui.news.spider.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.cairenhui.searchengine.spider.download.connector.http.HttpResult;
import com.cairenhui.searchengine.spider.download.connector.service.MessageUtil;

public class DownloadUtil {
	//下载失败后重试次数
	private static int times = 3;
	private static String defaultEncode = "UTF-8";
	private static Logger logger = Logger.getLogger(DownloadUtil.class);
	
	public static String download(String url, String encoding){
		String res = "";
		
		for(int i=0; i<times && StringUtils.isEmpty(res); i++){
			HttpResult result = MessageUtil.doGet(url, encoding);
			if(null != result){
				int code = result.getStatuCode();
				if(code == 200){
					res = result.getHtml();
				}
			}
		}
		return res;
	}
	
	public static byte[] download(String url){
		byte[] res = null;
		
		for(int i=0; i<times && (null == res || res.length == 0); i++){
			HttpResult result = MessageUtil.doGet(url);
			if(null != result){
				int code = result.getStatuCode();
				if(code == 200){
					res = result.getResponse();
				}
			}
		}
		return res;
	}
	
	public static String downloadAsString(String url){
		String res = "";
		
		for(int i=0; i<times && StringUtils.isEmpty(res); i++){
			try{
				HttpResult result = MessageUtil.doGet(url, defaultEncode);
				if(null != result){
					int code = result.getStatuCode();
					if(code == 200){
						res = result.getHtml();
					}
				}
			}catch(Exception e){
				logger.error("Read timed out，url = " + url);
			}
		}
		if(StringUtils.isEmpty(res)){
			logger.error("返回数据为空，url = " + url);
		}
		
		return res;
	}
	
	public static String downloadAsString(String url,String encode){
		String res = "";
		
		for(int i=0; i<times && StringUtils.isEmpty(res); i++){
			try{
				HttpResult result = MessageUtil.doGet(url, encode);
				if(null != result){
					int code = result.getStatuCode();
					if(code == 200){
						res = result.getHtml();
					}
				}
			}catch(Exception e){
				logger.error("Read timed out，url = " + url);
			}
		}
		if(StringUtils.isEmpty(res)){
			logger.error("返回数据为空，url = " + url);
		}
		
		return res;
	}
	
	public static void main(String[] a){
		String url = "http://www.baidu.com";
		String s = download(url, "gb2312");
		System.out.println(s);
	}
}
