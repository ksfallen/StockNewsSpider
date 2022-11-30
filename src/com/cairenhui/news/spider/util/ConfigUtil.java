package com.cairenhui.news.spider.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;


public class ConfigUtil {
	static Properties  props = null;
	static Properties  propsSite = null;
	static Logger logger = Logger.getLogger(ConfigUtil.class);
	static{
			props = new Properties();
			propsSite = new Properties();
			try {
				props.load(ClassLoader.getSystemResourceAsStream("common.properties"));
				propsSite.load(ClassLoader.getSystemResourceAsStream("site.properties"));
			} catch (IOException e) {
				logger.error(e);
			}
	}
	
	public static String getValue(String name){
		return props.getProperty(name);
	}
	
	public static String getSiteValue(String name){
		return propsSite.getProperty(name);
	} 
	
	/** 
     * 把中文转成Unicode码 
     * @param str 
     * @return 
     */  
    public static String chinaToUnicode(String str){  
        String result="";  
        for (int i = 0; i < str.length(); i++){  
            int chr1 = (char) str.charAt(i);  
            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)  
                result+="\\u" + Integer.toHexString(chr1);  
            }else{  
                result+=str.charAt(i);  
            }  
        }  
        return result;  
    }  

    public static void main(String[] args) {
//		System.out.print(chinaToUnicode("香港经济日报"));
//		System.out.println("=hket.com");
//		System.out.print(chinaToUnicode("上证报"));
//		System.out.println("=cnstock.com");
//		System.out.print(chinaToUnicode("WSJ中文网"));
//		System.out.println("=wsj.com");
//		System.out.print(chinaToUnicode("WSJ"));
//		System.out.println("=wsj.com");
//		System.out.print(chinaToUnicode("东方财富"));
//		System.out.println("=eastmoney.com");
//		System.out.print(chinaToUnicode("21世纪经济报道"));
//		System.out.println("=21cbh.com");
	}
    
}
