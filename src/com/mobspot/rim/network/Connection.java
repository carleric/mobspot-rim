package com.mobspot.rim.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;

import com.blackberry.util.log.Logger;
import com.blackberry.util.network.HttpConnectionFactory;

/*import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.blackberry.util.log.Logger;
import com.blackberry.util.log.LoggerFactory;
import com.blackberry.util.network.HttpConnectionFactory;*/

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.xml.jaxp.XMLParser;

public class Connection extends Thread
{
	private static final int TIMEOUT = 1500; // ms
	private static final int STREAM_CHUNK_READ_SIZE_BYTES = 1024;

	private static final String HEADER_CONTENTTYPE = "Content-Type";
	private static final String HEADER_SETCOOKIE = "set-cookie";
	private static final String HEADER_LOCATION = "Location";
	
	private static final String CONTENTTYPE_TEXTHTML = "text/html";
	private static final String CONTENTTYPE_JSON = "application/json";
	private static final String CONTENTTYPE_PNG = "image/png";
	private static final String CONTENTTYPE_RIM = "application/vnd.rim.html";
	
	private static final String LOGIN_URL = "http://dev.mobspot.com:8188/sessions/";
	//private final String LOGIN_URL = "http://dev.mobspot.com:8188/client/login";
	private static final String LOGIN_URL_NEW = LOGIN_URL+"new";
	private static final String LOGIN_URL_CREATE = LOGIN_URL+"create";
	private static final String LOGIN_URL_DESTROY = LOGIN_URL+"destroy";
	
	//private volatile String _theUrl;
	
	//private volatile boolean _fetchStarted = false;
	private volatile boolean _stop = false;
	private volatile boolean _pause = false;
	
	//private ConnectionListener _listener;
	private Vector _listeners;
	private String _session;
	private String _method = HttpConnection.GET;
	
	private volatile int _connectionState = 0;
	
	//inactive states, sleep during
	public static final int STATE_LOGGED_OUT = 0;
	public static final int STATE_LOGGED_IN = 1;
	public static final int STATE_IDLE = 2;
	
	//active states, don't sleep during
	public static final int STATE_REQUESTING_AUTHENTICITY_TOKEN = 3;
	public static final int STATE_REQUESTING_NEW_SESSION = 4;
	public static final int STATE_LOGGING_OUT = 5;
	public static final int STATE_REQUESTING = 6;
	//public static final int STATE_REQUESTING_IMAGE = 7;
	//public static final int STATE_SENDING_CONTENT = 8;
	
	//private volatile boolean _loggingIn = false;
	private String _email;
	private String _pw;
	
	private String _authenticityToken;
	private String _userid;
	private String _nickname;
	
	private String _connectionName;
	
	private StreamConnection s = null;
    private OutputStream os = null;
    HttpConnection httpConn;
	
	//protected Logger log = Logger.getLogger(getClass());
	protected static Logger log = Logger.getLogger("Connection");
	
	private static HttpConnectionFactory factory = new HttpConnectionFactory();
	
	//private final int MAX_RETRIES = 3;
	//private int retries = 0;
	
	private volatile Vector _requestQueue;
	//private Request _currentRequest;
	
	public Connection(String connectionName)
	{
		_listeners = new Vector();
		_requestQueue = new Vector();
		_connectionName = connectionName;
		setPriority(Thread.MAX_PRIORITY);
	}
	
	public void addListener(ConnectionListener l)
	{
		_listeners.addElement(l);
	}
	
	public void removeListener(ConnectionListener l)
	{
		_listeners.removeElement(l);
	}
	
	public synchronized void addRequest(Request r)
	{
		_requestQueue.addElement(r);
	}
	
	public boolean doRequests()
	{
		if(_requestQueue.size() > 0)
		{
			setConnectionState(STATE_REQUESTING);
			return true;
		}
		else
			return false;
	}
	
	private Request popRequest()
	{
		Request r = null;
		if(_requestQueue.size() > 0)
		{
			r = (Request)_requestQueue.elementAt(0);
			_requestQueue.removeElementAt(0);
		}
		return r;
	}
	
	private Request getCurrentRequest()
	{
		if(_requestQueue.size() > 0)
			return (Request)_requestQueue.elementAt(0);
		else
			return null;
	}
	
	private void nextRequest()
	{
		if(popRequest() == null)
			setConnectionState(STATE_IDLE);
		else
			setConnectionState(STATE_REQUESTING);
	}
	
	public void onResponse(Request r)
	{
		for(int i=0; i<_listeners.size(); i++)
		{
			ConnectionListener l = (ConnectionListener)_listeners.elementAt(i);
			l.onResponse(r);
		}
	}
	
	public int getConnectionState()
	{
		return _connectionState;
	}
	
	public boolean loggedIn()
	{
		return _connectionState != STATE_LOGGED_OUT;
	}
	
	private void setConnectionState(int state)
	{
		log.debug(_connectionName+" changing state to:"+state+" num request in queue="+_requestQueue.size());
		_connectionState = state;
		if(_connectionState < STATE_REQUESTING_AUTHENTICITY_TOKEN)
			pause();
		else
			resume();
		//stateChanged();
	}
	
	public void setIdle()
	{
		setConnectionState(STATE_IDLE);
	}
	
	public boolean hasSession()
	{
		if(_session != null)
			return true;
		else
			return false;
	}
	
	public String getSession()
	{
		return _session;
	}
	
	public String getAuthenticityToken()
	{
		return _authenticityToken;
	}
	
	public boolean hasAuthenticityToken()
	{
		return _authenticityToken != null;			
	}
	
	/*public boolean loggingIn()
	{
		return _loggingIn;
	}*/
	
	/*private void updateStatus(String status)
	{
		for(int i=0; i<_listeners.size(); i++)
		{
			ConnectionListener l = (ConnectionListener)_listeners.elementAt(i);
			l.updateStatus(status);
		}
	}
	
	private void updateResponse(String response)
	{
		for(int i=0; i<_listeners.size(); i++)
		{
			ConnectionListener l = (ConnectionListener)_listeners.elementAt(i);
			l.updateResponse(response);
		}
	}
	
	private void updateResponseCode(int responseCode)
	{
		for(int i=0; i<_listeners.size(); i++)
		{
			ConnectionListener l = (ConnectionListener)_listeners.elementAt(i);
			l.updateResponseCode(responseCode);
		}
	}
	
	private void updateResponse(Bitmap b) 
	{
		for(int i=0; i<_listeners.size(); i++)
		{
			ConnectionListener l = (ConnectionListener)_listeners.elementAt(i);
			l.updateResponse(b);
		}		
	}
	
	private void stateChanged()
	{
		UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run()
            {
				for(int i=0; i<_listeners.size(); i++)
				{
					ConnectionListener l = (ConnectionListener)_listeners.elementAt(i);
					l.stateChanged(getConnectionState());
				}
            }
        });
	}*/
	
	/*private String getUrl()
	{
	    return _theUrl;
	}*/
	
	public boolean isActive()
	{
	    return getConnectionState() > Connection.STATE_IDLE;
	}        
	 
	/*public void fetch(String url)
	{
		log.debug(_connectionName+" Connection.fetch url="+url);
		_theUrl = url;
		setConnectionState(STATE_REQUESTING_CONTENT);
	}
	
	 public void fetch(String url, String method)
	{
		_method = method;
		fetch(url);
	}
	 
	public void fetchImage(String url)
	{
		log.debug(_connectionName+" Connection.fetchImage url="+url);
		_theUrl = url;
		setConnectionState(STATE_REQUESTING_IMAGE);
	}
	*/
	
	public void stop()
	{
		log.debug(_connectionName+" stopping");
	    _stop = true;
	}
	
	public void pause()
	{
		log.debug(_connectionName+" pausing");
	    _pause = true;
	}
	
	public void resume()
	{
		log.debug(_connectionName+" resuming");
	    _pause = false;
	}
	
	public void run()
	{
	    for(;;)
	    {
	    	// Thread control
	        while(	_pause 
	        		|| getConnectionState() < STATE_REQUESTING_AUTHENTICITY_TOKEN
	        		|| getCurrentRequest() == null
	        		)  
	        {
	            // Sleep for a bit so we don't spin.
	            try 
	            {
	            	//log.debug("["+_connectionName+"]["+getName()+"] sleeping...");
	                sleep(TIMEOUT);
	            } 
	            catch (InterruptedException e) 
	            {
	                log.debug(_connectionName+" exception in sleep: "+e.getMessage());
	            }
	        }
	    	
	    	if ( _stop )
	    	{
	    		log.debug(_connectionName+" stopping connection, returning from run()");
	    		closeConnections();
	            return;
	    	}
	    	
	    	String currentURL = getCurrentRequest().getURL();
	    	log.debug(_connectionName+" running state="+getConnectionState() + " currentRequest="+currentURL);
	            
	        try 
	        {   
	        	if(currentURL == null)
	        	{
	        		log.debug(_connectionName+" url is null!");
	        		//return;
	        	}
	            s = null;
	            os = null;
	            log.debug(_connectionName+" opening connector with url="+currentURL);
	            
	            httpConn = factory.getHttpConnection(currentURL);
            	s = (StreamConnection)httpConn;
	            
	            sendRequest();
	            
	            readResponse();
	            
	            if(getCurrentRequest() != null)
	            {
		            if(getCurrentRequest().getRetries() == getCurrentRequest().getMaxRetries())
					{
						log.debug("exceeded max retries");
						nextRequest();
					}
					else
						getCurrentRequest().incrementRetries();
	            }
            	
	        } 
	        catch (Exception e)
	        {
	        	log.debug(_connectionName+" exception in Connection.run:" +e.getMessage());
	        	return;
	        }
	    }
	}
	
	private void sendRequest()
	{
		try
		{
			httpConn.setRequestMethod(getCurrentRequest().getMethod());
			httpConn.setRequestProperty("User-Agent", Request.getUserAgent());
			httpConn.setRequestProperty("Content-length", "0");
			 
			if (_session != null)
			{
				log.debug(_connectionName+" setting session cookie in request");
				httpConn.setRequestProperty("Cookie", _session);
			}
			 
			if(getConnectionState() == Connection.STATE_REQUESTING_NEW_SESSION
					/*|| getConnectionState() == Connection.STATE_LOGGING_OUT*/)
			{
			 	log.debug(_connectionName+" logging in with email="+_email+" password="+ _pw+" authenticity_token="+_authenticityToken);
			 	String params;
			 	params = "authenticity_token="+_authenticityToken+"&email=" + _email+ "&password="+_pw;
			 
			 	httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			 	httpConn.setRequestProperty("Content-length", ""+params.getBytes().length);
			 
			 	log.debug(_connectionName+" opening output stream");
			 	os = httpConn.openOutputStream();
			 	log.debug(_connectionName+" writing params bytes");
			 	os.write(params.getBytes());
			 	log.debug(_connectionName+" flushing output stream");
			    os.flush();
			}
			 
			//log.debugConnection(httpConn);
		}
		catch(Exception e)
		{
			log.debug(_connectionName+" exception in Connection.sendRequest: "+e.getMessage());
		}
	}
	
	private void readResponse()
	{
		try
		{
			try
			{	
				String content = "";
				int responseCode = httpConn.getResponseCode();
				getCurrentRequest().getResponse().setResponseCode(responseCode);
		        log.debug(_connectionName+" got response code:"+responseCode);
		        //updateResponseCode(responseCode);
		        
		        // Read the session ID from a cookie in the response headers.
		    	String cookie = httpConn.getHeaderField(HEADER_SETCOOKIE);
		    	if (cookie != null) 
		    	{
		    	  int semicolon = cookie.indexOf(';');
		    	  _session = cookie.substring(0, semicolon);
		    	  log.debug(_connectionName+" got session cookie: "+_session);
		    	}
		    	else
		    		log.debug(_connectionName+" no cookie in response");
		    	
		    	
		    	if (responseCode == HttpConnection.HTTP_OK)
		        {
		        	String contentType = httpConn.getHeaderField(HEADER_CONTENTTYPE);
		        	getCurrentRequest().getResponse().setContentType(contentType);
		        	
		            log.debug(_connectionName+" got content type: "+contentType);
		            
		            if(contentType != null && contentType.startsWith(CONTENTTYPE_TEXTHTML))
		            {
		            	/*if(getConnectionState() == Connection.STATE_REQUESTING_AUTHENTICITY_TOKEN
		            			|| getConnectionState() == Connection.STATE_REQUESTING_NEW_SESSION
		            			|| getConnectionState() == Connection.STATE_LOGGING_OUT)
		            	{
		            		try
		            		{
		                		XMLParser parser = new XMLParser();
		                		parser.parse(s.openInputStream(), new ResponseHandler());
		            		}
		            		catch(Exception e)
		            		{
		            			//TODO: handle parser error thrown when already logged in and it chokes on nbsp entities
		            			log.debug(_connectionName+" exception parsing sessions/new: "+ e.getMessage());
		            			e.printStackTrace();
		            		}
		            	}*/
		            	/*else if(getConnectionState() == Connection.STATE_REQUESTING_NEW_SESSION
	        			|| getConnectionState() == Connection.STATE_LOGGING_OUT)
		            	{
		            		content = readInputStream(httpConn, s);
		            		log.debug(content);
		            	}*/
		            	if(getConnectionState() == Connection.STATE_REQUESTING)
		            	{
		            		content = readInputStreamToString(httpConn, s);
		            		setConnectionState(STATE_IDLE);
		                	//updateResponse(content);
		            		getCurrentRequest().getResponse().setData(content);
		            		onResponse(getCurrentRequest());
		            		nextRequest();
		            	}
		            }
		            // if json, just read as text and update listeners with a string
		            else if(contentType != null && contentType.startsWith(CONTENTTYPE_JSON))
		            {
		            	content = readInputStreamToString(httpConn, s);
		            	
		            	if(getConnectionState() == Connection.STATE_REQUESTING)
		                {
		            		log.debug(_connectionName+" reading json response, updating response, setting it idle");
		            		setConnectionState(STATE_IDLE);
		                	//updateResponse(content);
		            		getCurrentRequest().getResponse().setData(content);
		            		onResponse(getCurrentRequest());
		            		nextRequest();
		                }
		            	else
		            	{
		            		log.debug(_connectionName+" INCONSISTENT STATE: got a json response while in state="+getConnectionState());
		            	}
		            }
		            
		            else if(contentType != null && contentType.startsWith(CONTENTTYPE_PNG))
		            {
		            	Bitmap b = readInputStreamToBitmap(httpConn, s);
		            	
		            	if(getConnectionState() == Connection.STATE_REQUESTING)
		                {
		            		log.debug(_connectionName+" reading png response, updating response, setting it idle");
		            		setConnectionState(STATE_IDLE);
		                	//updateResponse(b);
		            		getCurrentRequest().getResponse().setData(b);
		            		onResponse(getCurrentRequest());
		            		nextRequest();
		                }
		            }
		            else
		            {
		            	content = readInputStreamToString(httpConn, s);
			        	log.debug(_connectionName+"responeCode="+responseCode+ " message="+httpConn.getResponseMessage()+"\n\n content=\n"+content);
		            }
		        }
		        else 
		        { 
		        	content = readInputStreamToString(httpConn, s);
		        	log.debug(_connectionName+" bad response! code="+responseCode+ " message="+httpConn.getResponseMessage()+"\n\n content=\n"+content);
		        } 
			}
			finally
			{
				closeConnections();
			}
		}
		catch(Exception e)
		{
			log.debug(_connectionName+" exception in Connection.readResponse: "+e.getMessage());
		}
	}
	
	private void closeConnections()
	{
		try
		{
			if(httpConn != null)
				httpConn.close();
			if(os != null)
	    		os.close();
	    	if(s != null)
	    		s.close(); 
		}
		catch(Throwable t)
		{
			log.error("Error closing connections: "+t.getMessage());
		}
	}

	private static String readInputStreamToString(HttpConnection httpConn, StreamConnection s)
	{
		byte[] data = new byte[STREAM_CHUNK_READ_SIZE_BYTES];
        int len = 0;
        int size = 0;
        StringBuffer raw = new StringBuffer();
        InputStream input = null;
        
        try
        {
			try
			{
				input = s.openInputStream();
		    	
				while ( -1 != (len = input.read(data)) )
		        {
					log.debug("reading input stream");
		            raw.append(new String(data, 0, len));
		            size += len;    
		        } 
			}
			
			finally
			{
				if(input != null)
					input.close(); 
			}
        }
        catch(Exception e)
		{
			log.debug("exception downloading content: "+e.getMessage());
		}
		
        return raw.toString();
	}
	
	private Bitmap readInputStreamToBitmap(HttpConnection httpConn, StreamConnection s)
	{
		byte imageData[] = {};
        
        DataInputStream input = null;
        ByteArrayOutputStream bStrm = null;  
        Bitmap b = null;
        
	    try
	    {
			try
			{
				input = s.openDataInputStream();			
				
				int length = (int) httpConn.getLength();
				if (length != -1)
				{
					log.debug(_connectionName+" reading image data fully, length="+length);
					imageData = new byte[length];
				
					input.readFully(imageData);
				}
				else  // Length not available...
				{       
					bStrm = new ByteArrayOutputStream();
				    
					int ch;
					while ((ch = input.read()) != -1)
						bStrm.write(ch);
				    
					imageData = bStrm.toByteArray();
					bStrm.close();                
				}
				
				b = Bitmap.createBitmapFromPNG(imageData, 0, imageData.length);
			}
			
			finally
			{
				// Clean up
				if (input != null)
				  input.close();
				if (bStrm != null)
				  bStrm.close();      
			}
	    }
	    catch(Exception e)
	    {
	    	log.debug(_connectionName+" exception downloading image: "+e.getMessage());
	    }
	
		
        return b;
	}
	
	/*public void login(String user, String pw)
	{
		log.debug(_connectionName+" Connection.login("+user+","+pw+")");
		//_fetchStarted = true;
		//_loggingIn = true;
		_method = HttpConnection.GET;
	    _theUrl = Connection.LOGIN_URL_NEW;
	    _email = user;
	    _pw = pw;
	    setConnectionState(Connection.STATE_REQUESTING_AUTHENTICITY_TOKEN);
	}
	
	public void logout()
	{
		log.debug("Logging out");
		//_fetchStarted = true;
		//_loggingIn = true;
		_method = HttpConnection.GET;
	    _theUrl = Connection.LOGIN_URL_DESTROY;
	    setConnectionState(Connection.STATE_LOGGING_OUT);
	}*/
	
	/*private class ResponseHandler extends DefaultHandler
	{
		private boolean doneProcessing = false;
		
		public void startDocument()
		{
			log.debug(_connectionName+" starting parse of new document");
			doneProcessing = false;
		}
		
		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			if (doneProcessing)
			{
				log.debug(_connectionName+" skipping element parse.  done processing this document.");
				return;
			}
			try
			{
				log.debug(_connectionName+" parsing element "+uri+" : "+localName+" : "+ qName + " : "+attributes.getLength()+" atts");
				//if(_authenticityToken == null && localName.equalsIgnoreCase("input"))
				if(getConnectionState() == Connection.STATE_REQUESTING_AUTHENTICITY_TOKEN
						&& localName.equalsIgnoreCase("input"))
				{
					for(int i=0; i< attributes.getLength(); i++)
					{
						//log.debug("name="+attributes.getLocalName(i)+" value="+attributes.getValue(i));
						if(attributes.getLocalName(i).equalsIgnoreCase("name") && attributes.getValue(i).equalsIgnoreCase("authenticity_token"))
						{
							_authenticityToken = attributes.getValue("value");
							_theUrl = "http://dev.mobspot.com:8188/sessions/create";
							_method = HttpConnection.POST;
							setConnectionState(Connection.STATE_REQUESTING_NEW_SESSION);
							doneProcessing = true;	
							log.debug("got authenticity token: "+_authenticityToken);
							break;
						}
					}				
				}
				else if((getConnectionState() == Connection.STATE_REQUESTING_AUTHENTICITY_TOKEN
						|| getConnectionState() == Connection.STATE_REQUESTING_NEW_SESSION)
						&& localName.equalsIgnoreCase("a"))
            	{
            		//look for <div id=user-info><p>Welcome <span id=userlogin><a href=/users/314337 class=nickname title=Carl B>Carl B</a>...
					for(int i=0; i< attributes.getLength(); i++)
					{
						//log.debug("name="+attributes.getLocalName(i)+" value="+attributes.getValue(i));
						if(attributes.getLocalName(i).equalsIgnoreCase("href") && attributes.getValue(i).startsWith("/users/"))
						{
							_userid = attributes.getValue(i).substring(7);
							_nickname = attributes.getValue("title");
							setConnectionState(Connection.STATE_LOGGED_IN);
							doneProcessing = true;		
							log.debug(_connectionName+" logged in as userid=: "+_userid+" and nickname="+_nickname);
							break;
						}
					}
            	}
            	else if(getConnectionState() == Connection.STATE_LOGGING_OUT && localName.equalsIgnoreCase("a"))
            	{
            		//look for <div id=user-info><p><a href="/login" id="userlogin">Log in</a> or <a href="/user-signup">Join now</a></p>
					for(int i=0; i< attributes.getLength(); i++)
					{
						//log.debug("name="+attributes.getLocalName(i)+" value="+attributes.getValue(i));
						if(attributes.getLocalName(i).equalsIgnoreCase("href") && attributes.getValue(i).startsWith("/login")
								&& attributes.getValue("id").equalsIgnoreCase("userlogin"))
						{
							setConnectionState(Connection.STATE_LOGGED_OUT);
							doneProcessing = true;
							log.debug(_connectionName+" logged out");
							break;
						}
					}
            	}
			}catch(Exception e)
			{
				log.debug(_connectionName+" caught parser exception: "+e.getMessage());
			}
		}
		
		public void error(SAXParseException e)
		{
			log.debug(_connectionName+" caught parser error: "+e.getMessage());
		}
		
		public void fatalError(SAXParseException e)
		{
			log.debug(_connectionName+" caught parser fatalError: "+e.getMessage());
		}
	}*/
	
	public static Request doRequest(Request r) throws Exception
	{
		String url = r.getURL();
		
		HttpConnection conn = null;
		StreamConnection s = null;
		
		conn = factory.getHttpConnection(url);
		
        log.debug("opening static connector with url="+url);
        s = (StreamConnection)conn;

		try 
		{
			if ((url == null) || url.equalsIgnoreCase("") ) {
				return null;
			}
			conn.setRequestMethod(r.getMethod());
			conn.setRequestProperty("User-Agent", Request.getUserAgent());
			conn.setRequestProperty("Content-length", "0");
			
			r.getResponse().setResponseCode(conn.getResponseCode());
			
			switch (r.getResponse().getResponseCode()) {

			case HttpConnection.HTTP_OK: {
				r.getResponse().setContentType(conn.getHeaderField(HEADER_CONTENTTYPE));
				
				r.getResponse().setData(readInputStreamToString(conn, s));
				break;
			}

			case HttpConnection.HTTP_TEMP_REDIRECT:
			case HttpConnection.HTTP_MOVED_TEMP:
			case HttpConnection.HTTP_MOVED_PERM: {
				url = conn.getHeaderField("Location");
				return doRequest(new Request(url));
			}

			default:
				break;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (IOException e) {
				}
			}
		}

		return r;
	}
	
	public static boolean isValidURL(String url)
	{
		return (!url.equals("")
		&& url != null
		&& !url.equals("http://null")
		&& !url.equals("null"));
	}

	
}
