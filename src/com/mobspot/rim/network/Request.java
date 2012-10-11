package com.mobspot.rim.network;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;

public class Request
{
	private String _method;
	private volatile String _url;
	private int _numRetries = 0;
	private int _maxRetries = 3;
	private boolean _dependsOnPrevious = false;
	
	
	private Response _response;
	
	public static String getUserAgent() {
		StringBuffer sb = new StringBuffer();
		sb.append("BlackBerry");
		sb.append(DeviceInfo.getDeviceName());
		sb.append("/");
		sb.append(DeviceInfo.getSoftwareVersion());
		sb.append(" Profile/");
		sb.append(System.getProperty("microedition.profiles"));
		sb.append(" Configuration/");
		sb.append(System.getProperty("microedition.configuration"));
		sb.append(" VendorID/");
		sb.append(Branding.getVendorId());

		return sb.toString();
	}
	
	public Request(String url)
	{
		this._url = url;
		this._method = HttpConnection.GET;
		this._response = new Response();
	}
	
	public Request(String url, String method)
	{
		this(url);
		this._method = method;
	}
	
	public Request(String url, int maxRetries)
	{
		this(url);
		_maxRetries = maxRetries;
	}
	
	public synchronized String getURL()
	{
		return _url;
	}
	
	public synchronized String getMethod()
	{
		return _method;
	}
	
	public synchronized void incrementRetries()
	{
		_numRetries++;
	}
	
	public synchronized int getRetries()
	{
		return _numRetries;
	}
	
	public synchronized int getMaxRetries()
	{
		return _maxRetries;
	}
	
	public synchronized Response getResponse()
	{
		return _response;
	}
	
	public synchronized void setDependsOnPrevious(boolean depends)
	{
		_dependsOnPrevious = depends;
	}
	
	public synchronized boolean getDependsOnPrevious()
	{
		return _dependsOnPrevious;
	}
	
	public class Response
	{
		private Object _data;
		private int _responseCode;
		private String _contentType;
		private String _cookie;
		
		public synchronized void setData(Object data)
		{
			_data = data;
		}
		
		public synchronized Object getData()
		{
			return _data;
		}
		
		public synchronized int getResponseCode()
		{
			return _responseCode;
		}
		
		public synchronized void setResponseCode(int code)
		{
			_responseCode = code;
		}
		
		public synchronized void setContentType(String contentType)
		{
			_contentType = contentType;
		}
		
		public synchronized String getContentType()
		{
			return _contentType;
		}
	}
}
