package com.joniluo.crawler.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class FileUtil {

	 public static void writeFile(String filePath,String content){
		 FileWriter fw = null;
		 File file = new File(filePath);  
		         try {  
		             file.createNewFile(); // 创建文件  
		             fw = new FileWriter(file, true);
		         } catch (IOException e) {  
		             // TODO Auto-generated catch block  
		             e.printStackTrace();  
		         }  
		         
		         PrintWriter pw = new PrintWriter(fw);
		         pw.print(content);

		         pw.flush();

		         try {

		         fw.flush();

		         pw.close();

		         fw.close();

		         } catch (IOException e) {

		         e.printStackTrace();

		         }

	 }
	
	  public static String readTxtFile(String filePath,String encoding){
		  StringBuffer sb=new StringBuffer();
	        try {

	                //String encoding="utf-8";

	                File file=new File(filePath);

	                if(file.isFile() && file.exists()){ //判断文件是否存在

	                    InputStreamReader read = new InputStreamReader(

	                    new FileInputStream(file),encoding);//考虑到编码格式

	                    BufferedReader bufferedReader = new BufferedReader(read);

	                    String lineTxt = null;

	                    while((lineTxt = bufferedReader.readLine()) != null){

	                    	sb.append(lineTxt);

	                    }

	                    read.close();

	        }else{

	            System.out.println("找不到指定的文件");

	        }

	        } catch (Exception e) {

	            System.out.println("读取文件内容出错");

            e.printStackTrace();

	        }
	        return sb.toString();

	    }
}
