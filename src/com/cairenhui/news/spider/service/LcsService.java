package com.cairenhui.news.spider.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.cairenhui.news.spider.lcs.LCS;
import com.cairenhui.news.spider.util.ConfigUtil;

@Component
public class LcsService{
	private Map<String, List<String>> map = new HashMap<String, List<String>>();
	private final String CACHE = "./lcs/cache.dat";
	
	@SuppressWarnings("unchecked")
	public LcsService(){
		Object cacheMap = this.readObject(CACHE);
		if (cacheMap != null) {
			map = (Map<String, List<String>>) cacheMap;
		}
	}
	
	public void saveCache() {
		this.saveObject(CACHE, map);
	}
	
	private void saveObject(String path, Object obj) {
		synchronized (obj) {
			ObjectOutputStream out;
			try {
				File file = new File(path);
				File parent = file.getParentFile();
				if (parent.exists() == false) {
					parent.mkdirs();
				}
				out = new ObjectOutputStream(new FileOutputStream(path));
				out.writeObject(obj);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		Object cacheMap = this.readObject(CACHE);
//		if (cacheMap != null) {
//			map = (Map<String, List<String>>) cacheMap;
//		}
//	}
	
	private Object readObject(String path) {
		Object ret = null;
		ObjectInputStream in;
		try {
			File file = new File(path);
			if (file.exists() == false) {
				return null;
			}
			in = new ObjectInputStream(new FileInputStream(file));
			ret = in.readObject();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public synchronized boolean hasSimilar(String stockCode, String content){
		boolean res = false;
		if(StringUtils.isNotEmpty(stockCode) && StringUtils.isNotEmpty(content)){
			List<String> list = map.get(stockCode);
			if(null == list){
				List<String> newList = new ArrayList<String>();
				newList.add(content);
				map.put(stockCode, newList);
			}else{
				for(String s : list){
					double sim = LCS.getSimilarity(content, s);
					if(sim > 0.8){
						res = true;
						break;
					}
				}
				this.add(stockCode, content);
			}
		}
		return res;
	}

	private void add(String stockCode, String content) {
		List<String> list = map.get(stockCode);
		if(null == list){
			List<String> newList = new ArrayList<String>();
			newList.add(content);
			map.put(stockCode, newList);
		}else{
			int len = list.size();
			if(!list.contains(content)){
				if(len > 19){
					list.remove(0);
				}
				list.add(content);
			}
		}
	}

	public int getStartIndex() {
		int start = 0;
		try{
			List<String> list = map.get("start");
			if(null == list || list.size() == 0){
				start = 0;
			}else{
				start = Integer.parseInt(list.get(0));
			}
		}catch(Exception e){
			start = 0;
		}
		if(start == 0){
			start = Integer.parseInt(ConfigUtil.getValue("start"));
		}
		return start;
	}

	public void saveStart(int start) {
		List<String> newList = new ArrayList<String>();
		newList.add(String.valueOf(start));
		map.put("start", newList);
	}
}
