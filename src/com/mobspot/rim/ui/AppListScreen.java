package com.mobspot.rim.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;

import com.blackberry.util.log.Logger;
import com.mobspot.rim.App;
import com.mobspot.rim.Mobspot;
import com.mobspot.rim.network.Connection;
import com.mobspot.rim.network.ConnectionListener;
import com.mobspot.rim.network.NetworkConstants;
import com.mobspot.rim.network.Request;

public class AppListScreen extends MobspotScreen implements ConnectionListener, FieldChangeListener
{
	private AppList _list;
	private Connection _connection;
	private boolean runBitmapThread = false;
	private boolean pauseBitmapThread = false;
	
	//private static final int ICON_WIDTH = 28;
        
    protected Logger log = Logger.getLogger(getClass());
    
	public AppListScreen()
	{
		super();
		_list = new AppList(this);
    	_mainManager.add(_list);
    	
		_connection = new Connection("CONN_APPLISTING");
		_connection.addListener(this);
		_connection.start();
    }
	
	public void fetchFeaturedApps()
    {
		fetchApps("Featured Apps", NetworkConstants.URL_FEATURED_APPS);
    }
    
	public void fetchAppsByCategory(int category)
    {
		fetchApps("Apps in "+ _mobspot.getCategoryName(category), NetworkConstants.URL_APPS_BY_CATEGORY+category);
    }
	
	public void fetchFriendsApps(String fbuid)
	{
		fetchApps("Friends Apps", NetworkConstants.URL_FRIENDS_APPS+fbuid);
	}
	
	private void fetchApps(String title, String url)
	{
		setTitle(title);
		_list.deleteAll();
		
		_mainManager.setShowLoader(true);
		_mainManager.setStatusText("Loading Apps");
		
		try
		{
			//_fbc.getHttpClient().doGetAsync(url, this);
			//_connection.fetch(url);
			_connection.addRequest(new Request(url));
			_connection.doRequests();
		}
		catch(Throwable t)
		{
			log.debug("exception reading response in AppListScreen: "+t.getMessage());
    		fireAction(UIConstants.ACTION_ERROR, t.getMessage());
		}
		
	}
    
    public boolean onClose()
    {
    	log.debug("AppListScreen.onClose fired");
    	//cancelIconFetch on child AppListItems
    	//_list.cancelIconFetches();
    	
    	_connection.stop();
    	
    	stopBitmapThread();
    	
    	_mobspot.popScreen(this);
    	return true;    
    }

	public void fieldChanged(Field field, int context) 
	{
		log.debug("FeaturedAppsScreen.fieldChanged: "+((AppListItem)field).getItemId());
		fireAction(UIConstants.ACTION_APP_DETAIL, ((AppListItem)field).getApp());
	}
	
	public void loadBitmaps() 
	{
		startBitmapThread();
		(new BitmapThread()).start();
	}

	public void startBitmapThread() 
	{
		runBitmapThread = true;
	}

	public void stopBitmapThread() 
	{
		runBitmapThread = false;
	}
	
	public void onObscured()
	{
		log.debug("AppListScreen obscured, pausing bitmap load");
		pauseBitmapThread = true;
	}
	
	public void onExposed()
	{
		log.debug("AppListScreen exposed, resuming bitmap load");
		pauseBitmapThread = false;
	}
	
	private class BitmapThread extends Thread {

		public void run() 
		{
			for(int i=0;  i < _list.getFieldCount() && runBitmapThread; i++)
			{
				while(pauseBitmapThread)
				{
					try
					{
						sleep(500);
					}
					catch(Throwable t){}
				}
				
				AppListItem appItem = (AppListItem)_list.getField(i);
				App app = appItem.getApp();
			
				if(!app.hasIcon() && !app.isScannedFromDevice())
				{
					try {
						Request r = Connection.doRequest(new Request(Mobspot.URL_SERVER_IMAGES+appItem.getApp().getMobappId()+".png"));
						if(r.getResponse().getResponseCode() == 200)
						{
							byte[] data = r.getResponse().getData().toString().getBytes();
		
							if (data.length > 0) 
							{
								Bitmap a = Bitmap.createBitmapFromBytes(data, 0, data.length, 1);
								
								/*int height = UIConstants.ROUNDEDLISTITEM_HEIGHT-4;
								Bitmap b = new Bitmap(height, height);
								a.scaleInto(b, Bitmap.FILTER_LANCZOS);*/
								
								/*int [] bData = new int[height * height];
								b.getARGB(bData, 0, height, 0, 0, height, height);
								Utility.trimCornerPixels(bData, 10, height, height);
								b.setARGB(bData, 0, height, 0, 0, height, height);*/
								appItem.getApp().setIcon(a);
								appItem.invalidate();
							}
						}
					} 
					catch (Exception e) 
					{
						log.error("error downloading app icon: "+e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void onResponse(final Request r) 
	{
		log.debug("AppListScreen got response="+r.getResponse().getData().toString());
		_mobspot.invokeLater(new Runnable() 
        {
            public void run()
            {
		        //read JSON from response
		    	try
		    	{
		    		JSONObject json = new JSONObject(new JSONTokener(r.getResponse().getData().toString()));
		    		JSONArray apps = json.getJSONArray("mobapps");
		    		//add items to list
		    		for(int i=0; i<apps.length(); i++)
		    		{
		    			JSONObject jsonApp = new JSONObject(new JSONTokener(apps.getString(i)));
		    			
		    			int appId = jsonApp.getInt("mobapp_version_id");
		    			App app = _mobspot.getAppCache().getApp(appId);
		    			if(app == null)
		    			{
		    				app = new App(jsonApp);
		    				_mobspot.getAppCache().addApp(app);
		    			}
		    			_list.addApp(app);
		    		}
		    		_connection.stop();
		    		_mainManager.setShowLoader(false);
		    		loadBitmaps();
		    	}
		    	catch(Exception e)
		    	{
		    		log.error("exception reading response in AppListScreen: "+e.getMessage());
		    		fireAction(UIConstants.ACTION_ERROR, e.getMessage());
		    	}		    	
            }
        });
		
	}
 
    
}
