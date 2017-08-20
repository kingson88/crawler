package com.joniluo.crawler.http;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpProxyUtil {

	private static List<HttpProxyInfo> proxys=new ArrayList<HttpProxyInfo>();
	
	private static AtomicInteger nextServerCyclicCounter=new AtomicInteger(0);


	static{
//		HttpProxyInfo proxy=new HttpProxyInfo();
//		proxy.setHostProxy("proxy.host.com");
//		proxy.setPort(8080);
//		proxy.setUserName("");
//		proxy.setPassword("");
//		addProxy(proxy);
	}
	public static void addProxy(HttpProxyInfo proxy){
		proxys.add(proxy);
	}
	public static HttpProxyInfo getNextProxy(){
		HttpProxyInfo proxy=null;
		if(proxys.size()>0){
			int nextServerIndex = incrementAndGetModulo(proxys.size());
			proxy=proxys.get(nextServerIndex);
		}
		return proxy;
	}
//	public HttpProxyInfo getNowProxy(){
//		HttpProxyInfo proxy=null;
//		if(proxys.size()>0){
//			if(current.intValue()>=proxys.size()){
//				current.set(0);						
//			}
//			proxy=proxys.get(current.intValue());
//		}
//		return proxy;
//	}
    private static int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                return next;
        }
    }
	
}
