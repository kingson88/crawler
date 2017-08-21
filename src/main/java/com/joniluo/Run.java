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
import java.util.concurrent.ThreadPoolExecutor;
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
import com.joniluo.crawler.util.ItemInfo;
import com.joniluo.crawler.util.TmallItemHttp;

import java.net.URLEncoder;



public class Run {

	
	private static ExecutorService executor;
	protected static final Log log = LogFactory.getLog(Run.class);
	
	public static void main(String[] args) throws Exception {
		int nThreads=2;
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		executor = new ThreadPoolExecutor(nThreads, nThreads,
                 0L, TimeUnit.MILLISECONDS,
                 queue);
		ItemInfo item1=new ItemInfo();
		item1.setItemId(551489442058L);
		item1.setItemUrl("https://detail.tmall.com/item.htm?spm=a221t.7059849.7367061788.1.673bd71dKB319i&acm=lb-zebra-22355-288145.1003.4.1932154&id=551489442058&scm=1003.4.lb-zebra-22355-288145.OTHER_0_1932154");
		
		ItemInfo item2=new ItemInfo();
		item2.setItemId(536763566545L);
		item2.setItemUrl("https://detail.tmall.com/item.htm?spm=a2311wz.7782398.200010.10.6a162188b5lyg7&abtest=_AB-LR979-PR979&pos=1&abbucket=_AB-M979_B1&acm=201601215.1003.1.700922&id=536763566545&scm=1003.1.201601215.C2I_536763566545_700922");
		
		TmallItemHttp paseHtml=new TmallItemHttp(item1);
		TmallItemHttp paseHtml2=new TmallItemHttp(item2);
		executor.submit(paseHtml);
		executor.submit(paseHtml2);

		log.info("Done");
	}  
}
