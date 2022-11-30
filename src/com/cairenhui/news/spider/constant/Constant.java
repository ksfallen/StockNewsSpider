package com.cairenhui.news.spider.constant;

public class Constant {
	//资讯类型
	public final static int NEWS_TYPE_GGZX = 1;			//个股资讯
	public final static int NEWS_TYPE_PUBLIC = 3;		//股票公告
	public final static int NEWS_TYPE_QHHZ = 2;			//期货资讯汇总
	public final static int NEWS_TYPE_CPBD = 6;			//新浪操盘必读
	public final static int NEWS_TYPE_GGDP = 7;			//新浪个股点评
	public final static int NEWS_TYPE_JSY = 8;			//新浪股市及时雨
	public final static int NEWS_TYPE_RESEARCH = 10;	//新浪市场研究
	public final static int NEWS_TYPE_JJDT = 11;		//新浪基金动态
	
	//是否下载 0: 1:已下载
	public final static int NEWS_UNDOWNLOAD = 0;
	public final static int NEWS_DOWNLOADED = 1;
	
	//下载状态
	public final static int DOWNLOAD_SUCC = 1;			//下载成功
	public final static int DOWNLOADING	  = 2;			//正在下载
	public final static int DOWNLOAD_FAIL = -1;			//下载失败
	public final static int DOWNLOAD_CANC = -2;			//无法下载，缺少对该网站内容的解析
	public final static int DOWNLOAD_PARS = -3;			//无法解析网页内容
	public final static int SAVE_FAIL	  = -4;			//保存新闻内容失败
	
	//marketType
	public final static int MARKET_TYPE_SH = 1;
	public final static int MARKET_TYPE_SZ = 2;
	
}
