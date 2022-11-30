package com.cairenhui.news.spider.lcs;

import org.apache.commons.lang.StringUtils;


    /*
     * @author talent_Tony Tang<亮亮>
     * Email: talent_tonybentang@gmail.com
     * Copyright (C) 2009 talent_Tony Tang<亮亮>
     * All rights reserved.
     */
	/*
	 * LCS, Longest-Common-Subsequence
	 */
	public class LCS {    
	    public enum DIRECTION{ TOP, TOP_LEFT, LEFT };
	    private char[] first;
	    private char[] second;
	    private int[][] lcsTable;
	    private DIRECTION[][] lcsAssistTable;
	    private int lcsLength;
	    private String lcs_str;
	    private String lcsMatrix_str;
	    private StringBuffer str_buffer;
	    
	    public LCS(String str1, String str2){
	        first = str1.toCharArray();
	        second = str2.toCharArray();
	        lcsTable = new int[ first.length + 1 ][ second.length + 1 ];
	        lcsAssistTable = new DIRECTION[ first.length + 1 ][ second.length + 1];
	        lcs_str = null;
	        str_buffer = new StringBuffer();
	    }    
	    
	    public int getLCSLength(){
	        lcsLength = getLCSLength(first, second);
	        return lcsLength;
	    }
	    
	    private int getLCSLength(char[] one, char[] two){
	        lcsTable = new int[one.length + 1][two.length + 1];
	        lcsAssistTable = new DIRECTION[one.length + 1][ two.length + 1];
	        
	        for(int i=0; i<one.length ;i++){
	            lcsTable[i][0] = 0;
	        }

	        for(int j=0; j<two.length-1; j++){
	            lcsTable[0][j] = 0;
	        }
	        
	        for(int i=0; i<one.length; i++){
	            for(int j=0; j<two.length; j++){
	                if(one[i] == two[j]){
	                    lcsTable[i + 1][j + 1] = lcsTable[i][j] + 1;
	                    lcsAssistTable[i + 1][j + 1] = DIRECTION.TOP_LEFT;    
	                }
	                else if(lcsTable[i][j + 1] >= lcsTable[i + 1][j]){
	                    lcsTable[i + 1][j + 1] = lcsTable[i][j + 1];
	                    lcsAssistTable[i + 1][j + 1] = DIRECTION.TOP;
	                }
	                else{
	                    lcsTable[i + 1][j + 1] = lcsTable[i + 1][j];
	                    lcsAssistTable[i + 1][j + 1] = DIRECTION.LEFT;
	                }
	            }
	        }
	        
	        lcsLength = lcsTable[one.length][two.length];        
	        return lcsLength;
	    }
	    
	    public void runLCS(){
	        runLCS(lcsAssistTable, first, first.length, second.length);
	        lcs_str = str_buffer.toString();
	    }
	    
	    private void runLCS(DIRECTION[][] lcsAssistTable, char[] one, int oneLength, int twoLength){
	        if(oneLength == 0 || twoLength == 0){
	            return;
	        }
	            
	        int i = oneLength;
	        int j = twoLength;
	        
	        if(lcsAssistTable[i][j] == DIRECTION.TOP_LEFT){
	            runLCS(lcsAssistTable, one, i - 1, j - 1);
	            str_buffer.append(one[ i - 1 ]);
	        }
	        else if(lcsAssistTable[i][j] == DIRECTION.TOP){
	            runLCS(lcsAssistTable, one, i - 1, j);
	        }
	        else{
	            runLCS(lcsAssistTable, one, i, j -1);
	        }
	    }
	    
	    public String getLCSMatrixString(){
	        str_buffer = new StringBuffer();
	        for(int[] row: lcsTable){
	            for(int element : row){
	                str_buffer.append(element + " ");
	            }
	            str_buffer.append("\n");
	        }
	        lcsMatrix_str = str_buffer.toString();
	        return lcsMatrix_str;
	    }
	    
	    public static void print(Object o){
	        System.out.print(o);
	    }
	    
	    public static void println( Object o ){
	        System.out.println(o);
	    }

	    public String getLCS(){
	        return lcs_str;
	    }

	    /**
	     * @return first
	     */
	    public char[] getFirstCharArray(){
	        return first;
	    }
	    
	    /**
	     * @return second
	     */
	    public char[] getSecondCharArray(){
	        return second;
	    }

	    /**
	     * @return lcsAssistTable
	     */
	    public DIRECTION[][] getLcsAssistTable(){
	        return lcsAssistTable;
	    }

	    /**
	     * @return lcsTable
	     */
	    public int[][] getLcsTable(){
	        return lcsTable;
	    }
	    
	    public static void main(String[] args){
	        String a = "我抄我抄我抄抄抄：明月明时有，把酒问蓝天，不知天上宫阙，明夕是何年";
	        String b = "苏轼曾经写过“明月几时有，把酒问青天”的千古名句";
	        
//	        LCS lcs = new LCS(a, b);        
//	        
//	        lcs.getLCSLength();
//	        lcs.runLCS();
//	        println("最大相似子字符串长度是：" + lcs.getLCSLength());
//	        println("最大相似子字符串为：" + lcs.getLCS());
//	        
//	        double res = 0.0;
//	        res = (double)5/7;
//	        System.out.println(res);
	    }
	    
	    /**
	     * 获取两个字符串的相似度
	     * @param s1
	     * @param s2
	     * @return
	     */
	    public static double getSimilarity(String s1, String s2){
	    	double res = 0.0;
	    	if(StringUtils.isNotEmpty(s1) && StringUtils.isNotEmpty(s2)){
	    		LCS lcs = new LCS(s1, s2);        
		        
		        lcs.getLCSLength();
		        lcs.runLCS();
		        
		        int len = lcs.getLCSLength();
		        res = (double)len/s1.length();
		        
		        lcs = null;
	    	}
	    	return res;
	    }
}
