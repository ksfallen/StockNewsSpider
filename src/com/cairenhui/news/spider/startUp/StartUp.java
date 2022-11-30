package com.cairenhui.news.spider.startUp;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartUp {

	public static void main(String[] args) {
		 new ClassPathXmlApplicationContext(new String[]{"applicationContext-resource.xml","applicationContext-dao.xml","applicationContext-service.xml","applicationContext-timeTask.xml"});
	}

}