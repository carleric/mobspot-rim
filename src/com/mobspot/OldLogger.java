package com.mobspot;

import javax.microedition.io.HttpConnection;

public class OldLogger 
{
	private final static boolean DEBUG_ON = true;
	private final static String [] DEBUG_SCOPES = {"facebook", "connection"}; 
	
	private OldLogger(){}
	
	public static void trace(String scope)
	{
		if(inScope(scope) && DEBUG_ON)
		{
			try{
				throw new Exception();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
			
	}
	
	public static void debug(String scope, String msg)
	{
		if(inScope(scope) && DEBUG_ON)
			System.out.println(msg);
	}
	
	public static void debug(String msg)
	{
		debug("default", msg);
	}
	
	public static void debugConnection(HttpConnection hc) 
	{
		debug("network", "Request Method for this connection is " + hc.getRequestMethod());
		debug("network", "URL in this connection is " + hc.getURL());
		debug("network", "Protocol for this connection is " + hc.getProtocol()); // It better be HTTP:)
		debug("network", "This object is connected to " + hc.getHost() + " host");
		debug("network", "HTTP Port in use is " + hc.getPort());
		debug("network", "Query parameter in this request are  " + hc.getQuery());
		debug("network", "Length " + hc.getLength());
	}
	
	private static boolean inScope(String scope)
	{
		for(int i=0; i<DEBUG_SCOPES.length; i++)
		{
			if(scope.compareTo(DEBUG_SCOPES[i]) == 0)
				return true;
		}
		return false;
	}
	
}
