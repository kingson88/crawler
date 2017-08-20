package com.joniluo.crawler.http;

public class HttpProxyInfo {

	private String hostProxy;
	
	private int port;
	
	private String userName;
	
	private String password;

	public String getHostProxy() {
		return hostProxy;
	}

	public void setHostProxy(String hostProxy) {
		this.hostProxy = hostProxy;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
