package com.joniluo.crawler.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 请求工具类
 * 
 * @author : liii
 * @version : 1.0.0
 * @date : 2015/7/21
 * @see : TODO
 */
public class HttpUtil {
	private static PoolingHttpClientConnectionManager connMgr;
	private static RequestConfig requestConfig;
	private static final int MAX_TIMEOUT = 7000;

	static {

		// 设置连接池
		connMgr = new PoolingHttpClientConnectionManager();
		// 设置连接池大小
		connMgr.setMaxTotal(100);
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
		
		 //HttpsURLConnection.setDefaultHostnameVerifier(hv)

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(MAX_TIMEOUT);
		// 设置读取超时
		configBuilder.setSocketTimeout(MAX_TIMEOUT);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		// 在提交请求之前 测试连接是否可用
		configBuilder.setStaleConnectionCheckEnabled(true);
		requestConfig = configBuilder.build();
	}

	private static RequestConfig getConfig(HttpProxyInfo proxyInfo) {

		// RequestConfig config =
		// RequestConfig.custom().setProxy(proxy).build();

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(MAX_TIMEOUT);
		// 设置读取超时
		configBuilder.setSocketTimeout(MAX_TIMEOUT);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		// 在提交请求之前 测试连接是否可用
		configBuilder.setStaleConnectionCheckEnabled(true);

		if (null != proxyInfo) {
			HttpHost proxy = new HttpHost(proxyInfo.getHostProxy(), proxyInfo.getPort(), "http");
			configBuilder.setProxy(proxy);
		}
		RequestConfig requestConfig = configBuilder.build();
		return requestConfig;
	}

	/**
	 * 发送 GET 请求（HTTP），不带输入数据
	 * 
	 * @param url
	 * @return
	 */
	public static String doGet(String url) {
		return doGet(url, null, "utf-8");
	}

	public static String doGet(String url, String charset) {
		return doGet(url, null, charset);
	}

	public static String doGet(String url, Map<String, String> headers) {
		return doGet(url, headers, "utf-8");
	}

	/**
	 * http get请求。支持SLL和代理
	 * 
	 * @param url
	 * @param headers
	 * @param charset
	 * @return
	 */
	public static String doGet(String url, Map<String, String> headers, String charset) {

		HttpProxyInfo proxyInfo = null;
		RequestConfig requestConfig = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String httpStr = null;
		boolean isSSL = false;

		try {

			if (url.startsWith("https") || url.startsWith("HTTPS")) {
				isSSL = true;
			}
			proxyInfo = HttpProxyUtil.getNextProxy();
			requestConfig = getConfig(proxyInfo);
			if (isSSL) {
				httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
						.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
			} else {
				httpClient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig)
						.build();
			}
			HttpGet httpPost = new HttpGet(url);
			if (null != headers && headers.size() > 0) {
				for (String key : headers.keySet()) {
					httpPost.setHeader(key, headers.get(key).toString());
				}
			}

			if (null != proxyInfo) {
				HttpHost proxy = new HttpHost(proxyInfo.getHostProxy(), proxyInfo.getPort());
				BasicScheme proxyAuth = new BasicScheme();
				// Make client believe the challenge came form a proxy
				proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
				BasicAuthCache authCache = new BasicAuthCache();
				authCache.put(proxy, proxyAuth);

				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(new AuthScope(proxy),
						new UsernamePasswordCredentials(proxyInfo.getUserName(), proxyInfo.getPassword()));

				HttpClientContext context = HttpClientContext.create();
				context.setAuthCache(authCache);
				context.setCredentialsProvider(credsProvider);
				httpPost.setConfig(requestConfig);
				if (isSSL) {
					SslUtils.ignoreSsl();
				}
				response = httpClient.execute(httpPost, context);
			} else {
				httpPost.setConfig(requestConfig);
				if (isSSL) {
					SslUtils.ignoreSsl();
				}
				response = httpClient.execute(httpPost);
			}

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			httpStr = EntityUtils.toString(entity, charset);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResponse(response);
		}
		return httpStr;
	}

	private static void closeResponse(CloseableHttpResponse response) {
		if (response != null) {
			try {
				EntityUtils.consume(response.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void closeInputStream(InputStream in) {
		if (null != in) {
			// 关闭低层流。
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static boolean downLoadFile(String url, String destFileName) {
		// 生成一个httpclient对象
		InputStream in = null;
		boolean result = false;
		CloseableHttpClient httpclient = null;
		HttpResponse response = null;
		boolean isSSL = false;
		try {
			if (url.startsWith("https") || url.startsWith("HTTPS")) {
				isSSL = true;
			}

			HttpProxyInfo proxyInfo = HttpProxyUtil.getNextProxy();
			RequestConfig requestConfig = getConfig(proxyInfo);
			if (isSSL) {
				httpclient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
						.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
			} else {
				httpclient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig)
						.build();
			}
			HttpGet httpget = new HttpGet(url);

			if (null != proxyInfo) {
				HttpHost proxy = new HttpHost(proxyInfo.getHostProxy(), proxyInfo.getPort());
				BasicScheme proxyAuth = new BasicScheme();
				// Make client believe the challenge came form a proxy
				proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
				BasicAuthCache authCache = new BasicAuthCache();
				authCache.put(proxy, proxyAuth);

				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(new AuthScope(proxy),
						new UsernamePasswordCredentials(proxyInfo.getUserName(), proxyInfo.getPassword()));

				HttpClientContext context = HttpClientContext.create();
				context.setAuthCache(authCache);
				context.setCredentialsProvider(credsProvider);
				httpget.setConfig(requestConfig);
				if (isSSL) {
					SslUtils.ignoreSsl();
				}
				response = httpclient.execute(httpget, context);
			} else {
				httpget.setConfig(requestConfig);
				if (isSSL) {
					SslUtils.ignoreSsl();
				}
				response = httpclient.execute(httpget);
			}
			// httpclient = HttpClients.createDefault();
			// HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			in = entity.getContent();
			File file = new File(destFileName);

			FileOutputStream fout = new FileOutputStream(file);
			int l = -1;
			byte[] tmp = new byte[1024];
			while ((l = in.read(tmp)) != -1) {
				fout.write(tmp, 0, l);
				// 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
			}
			fout.flush();
			fout.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeInputStream(in);
		}
		// try {
		// if(null!=httpclient){
		// httpclient.close();
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return result;
	}

	/**
	 * 发送 POST 请求（HTTP），不带输入数据
	 * 
	 * @param apiUrl
	 * @return
	 */
	public static String doPost(String url) {
		return doPost(url, new HashMap<String, Object>(), null, "utf-8");
	}

	/**
	 * 发送 SSL POST 请求（HTTPS），K-V形式
	 * 
	 * @param apiUrl
	 *            API接口URL
	 * @param params
	 *            参数map
	 * @return
	 */
	public static String doPost(String url, Map<String, Object> params, Map<String, String> headers, String charset) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		String httpStr = null;
		boolean isSSL = false;
		try {

			if (url.startsWith("https") || url.startsWith("HTTPS")) {
				isSSL = true;
			}
			if (isSSL) {
				httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
						.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
			} else {
				httpClient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig)
						.build();
			}
			if (null != headers && headers.size() > 0) {
				for (String key : headers.keySet()) {
					httpPost.setHeader(key, headers.get(key).toString());
				}
			}
			httpPost.setConfig(requestConfig);
			List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
				pairList.add(pair);
			}
			httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(charset)));
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			httpStr = EntityUtils.toString(entity, charset);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return httpStr;
	}

	/**
	 * 发送 SSL POST 请求（HTTPS），JSON形式
	 * 
	 * @param apiUrl
	 *            API接口URL
	 * @param json
	 *            JSON对象
	 * @return
	 */
	public static String doPostJson(String url, Object json, Map<String, String> headers, String charset) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		String httpStr = null;
		boolean isSSL = false;
		try {

			if (url.startsWith("https") || url.startsWith("HTTPS")) {
				isSSL = true;
			}
			if (isSSL) {
				httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
						.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
			} else {
				httpClient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig)
						.build();
			}
			if (null != headers && headers.size() > 0) {
				for (String key : headers.keySet()) {
					httpPost.setHeader(key, headers.get(key).toString());
				}
			}
			httpPost.setConfig(requestConfig);
			StringEntity stringEntity = new StringEntity(json.toString(), charset);// 解决中文乱码问题
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			httpStr = EntityUtils.toString(entity, charset);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResponse(response);
		}
		return httpStr;
	}

	/**
	 * 创建SSL安全连接
	 * 
	 * @return
	 */
	private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
		SSLConnectionSocketFactory sslsf = null;
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}

				@Override
				public void verify(String host, SSLSocket ssl) throws IOException {
				}

				@Override
				public void verify(String host, X509Certificate cert) throws SSLException {
				}

				@Override
				public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
				}
			});
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return sslsf;
	}

	/**
	 * 测试方法
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		long beginTime = System.currentTimeMillis();
		String imageUrl = "http://img.alicdn.com/bao/uploaded/i1/2183380830/TB2.gPPtmxjpuFjSszeXXaeMVXa_!!2183380830.jpg_b.jpg";
		downLoadFile(imageUrl, "d:\\2.jpg");
		String html = doGet(
				"https://detail.tmall.com/item.htm?id=553745269452&skuId=3414398044129&user_id=106852162&cat_id=50106425&is_b=1&rn=011b25d66d15c454b44b79742fcc722d",
				null, "utf-8");
		System.out.println(html);
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("Execute main !ok consume %s ms", endTime - beginTime));
	}

}