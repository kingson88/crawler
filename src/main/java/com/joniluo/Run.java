package com.joniluo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joniluo.crawler.http.HttpUtil;

import java.net.URLEncoder;



public class Run {

	protected static final Log log = LogFactory.getLog(Run.class);
	
	public static void main(String[] args) throws Exception {
		long beginTime = System.currentTimeMillis();
		String imageUrl = "http://img.alicdn.com/bao/uploaded/i1/2183380830/TB2.gPPtmxjpuFjSszeXXaeMVXa_!!2183380830.jpg_b.jpg";
		HttpUtil.downLoadFile(imageUrl, "d:\\2.jpg");
		String html = HttpUtil.doGet(
				"https://detail.tmall.com/item.htm?id=553745269452&skuId=3414398044129&user_id=106852162&cat_id=50106425&is_b=1&rn=011b25d66d15c454b44b79742fcc722d",
				null, "utf-8");
		System.out.println(html);
		 html = HttpUtil.doGet(
				"https://detail.tmall.com/item.htm?id=553745269452&skuId=3414398044129&user_id=106852162&cat_id=50106425&is_b=1&rn=011b25d66d15c454b44b79742fcc722d",
				null, "utf-8");
		System.out.println(html);
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("Execute main !ok consume %s ms", endTime - beginTime));
		log.info("Done");
	}  
}
