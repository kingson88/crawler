package com.joniluo.crawler.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang.StringUtils;

public class StringUtil {
	
	public static String replaceAll(String source){
		return source.replace("\r\n", "").replace("\n", "").replace("\r", "").replace("\t", "").replace(" ", "");
	}
	public static String ascii2String(String ASCIIs) {  
		   if(StringUtil.isBlank(ASCIIs)){
			   return "";
		   }
		            String[] ASCIIss = ASCIIs.split(";");  
		            StringBuffer sb = new StringBuffer();  
		            for (int i = 0; i < ASCIIss.length; i++) {  
		            	String[] s=ASCIIss[i].split("&#");
		            	for(int j=0;j<s.length;j++){
		            		if(isNotEmptyStr(s[j])){
			            	     if(isNumeric(s[j])){
			            	    	 sb.append((char) ascii2Char(Integer.parseInt(s[j])));  
			            	     }else{
			            	    	 sb.append(s[j]);
			            	     }
		            		}
		            	}
		               
		            }  
		            return sb.toString();  
	   } 
	   public static char ascii2Char(int ASCII) {  
		            return (char) ASCII;  
    } 


	public static boolean isNull(String arg) {
		return arg == null ? true : false;
	}

	public static boolean isEmpty(String arg) {
		return "".equals(arg) ? true : false;
	}

	public static boolean isNullStr(String arg) {
		return "null".equals(arg) ? true : false;
	}

	/**
	 * 检查字符串不是null, "", "null"
	 * 
	 * @param arg
	 * @return true/非空, false/验证不通过
	 */
	public static boolean isNotEmptyStr(String arg) {
		return !(isNull(arg) || isEmpty(arg) || isNullStr(arg)) ? true : false;
	}

	/**
	 * 截取字符串后面部分字符.
	 * 
	 * @param str
	 * @param length
	 *            表示双字节长度,比如4,表示返回的字符串长度为8字节.
	 * @param addPoints
	 * @return
	 */
	public static String trimWords(String str, int length, boolean addPoints) {
		String wordStr = str;
		if (wordStr == null || wordStr.equals("")) {
			return "";
		}
		int byteLen = length * 2;
		byte[] strBytes = wordStr.getBytes();
		if (strBytes.length == str.length()) {
			if (strBytes.length <= byteLen) {
				return wordStr;
			}
			byte[] trimBytes = new byte[byteLen];
			System.arraycopy(strBytes, 0, trimBytes, 0, byteLen);
			wordStr = new String(trimBytes);
		} else {
			if (wordStr.length() <= length) {
				return str;
			}
			wordStr = left(str, length);
		}
		if (addPoints) {
			wordStr += "...";
		}
		return wordStr;
	}

	/**
	 * 从左起取字符串前n位。
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String left(String str, int length) {
		if (str == null) {
			throw new IllegalArgumentException("字符串参数值不能为null");
		}
		if (length < 0) {
			throw new IllegalArgumentException("整型参数长度不能小于0");
		}
		if (str.length() < length) {
			throw new IllegalArgumentException("字符串参数长度不能小于" + length);
		}

		return str.substring(0, length);
	}

	/**
	 * 过滤html代码
	 * 
	 * @param content
	 * @return
	 */
	public static String htmlContentConvert(String content) {
		content = content.replace("<", "&lt;");
		content = content.replace(">", "&gt;");
		content = content.replace("&", "&amp;");
		content = content.replace("\"", "&quot;");
		content = content.replace("'", "&apos;");
		content = content.replace("'", "&quot;");
		return content;
	}

	/**
	 * 将数据库表字段字符串转换为驼峰标识字符串
	 * 
	 * @param fieldName
	 * @return
	 */
	public static String convertField(String fieldName) {
		// check参数
		if (!isNotEmptyStr(fieldName)) {
			return null;
		}

		// buffer
		StringBuffer tmpData = new StringBuffer();
		// 转换为小写, 分割字符串
		fieldName = fieldName.toLowerCase();
		String[] tmpDatas = fieldName.split("_");
		if ((null == tmpDatas) || (0 == tmpDatas.length)) {
			return null;
		}		
		int length = tmpDatas.length;
		if (1 == length) {
			tmpData.append(tmpDatas[0]);
			return tmpData.toString();
		} else {
			String data = "";
			tmpData.append(tmpDatas[0]);
			for (int index = 1; index < length; index++) {
				data = tmpDatas[index];
				// 将第一个字母转换为大写
				if(StringUtils.isNotEmpty(data)){
					if(data.length()>1){
						data=data.substring(0,1).toUpperCase() + data.substring(1);	
					}else{
						data=data.substring(0,1).toUpperCase();
					}
				}								
				tmpData.append(data);
			}
		}

		return tmpData.toString();
	}
	
	/**
	 * 将字符串的第index字符转换为大写
	 * 
	 * @param data
	 * @param index
	 * @return
	 */
	public static String convertString(String data, int index) {
		// check
		if (StringUtil.isNull(data) || StringUtil.isEmpty(data)) {
			return data;
		}
		
		// 字符串长度
		int length = data.length();
		if ((index < 0) || (length < index)) {
			return data;
		}
		
		// 取出第index字母并进行替换
		char beginOld = data.charAt(0);
		char beginNew = (beginOld + "").toUpperCase()
				.charAt(0);
		data = data.replace(beginOld, beginNew);
				
		return data;
	}
	
	/**
	 * 判断一个字符串是否是数字
	 * 
	 * @param number
	 * @return true/是数字, false/不是数字
	 */
	public static boolean isNumeric(String number) {
		String model = "[0-9]*";
		
		Pattern pattern = Pattern.compile(model);
		Matcher isNum = pattern.matcher(number);
		
		return isNum.matches();
	}
	
	
	/**
	 * 判断一个字符串是否是汉字
	 * 
	 * @param number
	 * @return true/是, false/不是
	 */
	public static boolean isHanzi(String str) {
		 boolean temp = false;
	       Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
	       Matcher m=p.matcher(str); 
	       if(m.find()){ 
	           temp =  true;
	       }
	       return temp;
	}
	/**
	 * 判断一个字符串是否是字母
	 * 
	 * @param number
	 * @return true/是, false/不是
	 */
	public static boolean isZimu(String fstrData){   
		char c = fstrData.charAt(0);   
		if(((c >= 'a'&& c <= 'z') || (c >= 'A'&& c <= 'Z'))){   
			return true;   
		}else{   
			return false;   
		}   
	}
	
	/**
	 * 判断一个字符串是否是整数数字，包括负数
	 * @param number
	 * @return
	 */
	public static boolean isInteger(String number) {
		String model = "-?[0-9]*";
		
		Pattern pattern = Pattern.compile(model);
		Matcher isNum = pattern.matcher(number);
		
		return isNum.matches();
	}
	
	/**
	 * 判断字符串是否为 "" 或 null
	 * @param str
	 * @return boolean true表示为 "" 或 null,false表示不为"" 或  null
	 */
	public static boolean isBlank(String str){
		return isNull(str) || str.trim().length() == 0 || str.trim().equals("null");
	}
	
	/**
	 * 判断字符串是否是有效的商品编码
	 * @param upc
	 * @return
	 */
	public static boolean isValidUpc(String upc) {
		if(null != upc){
			upc = upc.trim();
			if(upc.length() == 13 || upc.length() == 8){
				if(upc.length() == 8){upc = "00000" + upc;}
				String code = upc.substring(0,12);
				int sumj = 0, sume = 0;
				int result = 0;
				for (int i = 0; i < code.length() - 1; i = i + 2) {
					sumj += code.charAt(i) - '0';
					sume += code.charAt(i + 1) - '0';
				}
				result = sumj + sume * 3;
				result = result % 10;
				result = 10 - result;
				if(result == 10){
					result = 0;
				}
				String calUpc = code + result;
				return calUpc.equals(upc);
			}
		}
		return false;
	}
	
	/**
	 * 获取字符串的非null格式,字符串为空则返回""
	 * @param str String
	 * @return
	 */
	public static String getStr(String str){
		if(null == str){
			return "";
		}
		return str;
	}
	
	/**
	 * 判断多个字符串中是否有一个字符串为空
	 * @param strs
	 * @return boolean 有一个字符串为空则或数组为空则返回true,反之,返回false
	 */
	public static boolean hasBlankStr(String... strs){
		if(null != strs){ 
			for(String str : strs){
				if(isBlank(str)){
					return true;
				}
			}
		}else{
			return true;
		}
		return false;
	}
		
	/**
	 * 设置对象的字符串的默认值为""
	 * @param obj
	 * @return 若str为null,返回"",反之返回str
	 */
	public static String defaultEmptyStr(Object obj){
		return defaultStr(obj,null);
	}
	
	/**
	 * 设置对象的字符串的默认值
	 * @param obj
	 * @param defaultStr 
	 * @return  str为null,defaultStr不为null,返回defaultStr,反之返回"";否则返回str
	 */
	public static String defaultStr(Object obj,String defaultStr){
		if(null == obj){
			if(null != defaultStr){
				return defaultStr;
			}else{
				return "";
			}
		}else{
			return obj.toString();
		}
	}
	



	/**
	 * 获取一个空串
	 */
	public static String getEmptyString(){
		return "";
	}
	
}
