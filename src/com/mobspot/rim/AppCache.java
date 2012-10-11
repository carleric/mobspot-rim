package com.mobspot.rim;

import java.util.Vector;

import com.blackberry.util.log.Logger;



public class AppCache /*implements ConnectionListener*/
{
	private static Vector _apps;
	private static final int MAX_CACHE_SIZE = 100;
	
	protected Logger log = Logger.getLogger(getClass());
	
	private static final String URL_ALL_APPS = "http://dev.mobspot.com:8188/remote_client/all_apps";
	
	public AppCache()
	{
		_apps = new Vector();
	}
	
	public void addOrUpdateApp(App app)
	{
		if(hasApp(app.getName()))
		{
			updateAppWithSameName(app);
		}
		else
		{
			addApp(app);
		}
	}
	
	public boolean addApp(App app)
	{
		if(app.getMobappId() != -1 && hasApp(app.getMobappId()))
		{
			log.debug("AppCache.addApp skipping "+app.getMobappId()+" already in cache");
			return false;
		}
		else if(app.getMobappId() == -1 && hasApp(app.getName()))
		{
			log.debug("AppCache.addApp skipping "+app.getName()+" already in cache");
			return false;
		}
		else
		{
			if(_apps.size() == MAX_CACHE_SIZE)
				_apps.removeElementAt(0);
			_apps.addElement(app);	
			return true;
		}			
	}
	
	public boolean hasApp(int mobapp_version_id)
	{
		for(int i=0; i<_apps.size(); i++)
		{
			if(mobapp_version_id == ((App)_apps.elementAt(i)).getMobappVersionId())
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean hasApp(String title)
	{
		for(int i=0; i<_apps.size(); i++)
		{
			if(title.equalsIgnoreCase(((App)_apps.elementAt(i)).getName()))
			{
				return true;
			}
		}
		return false;
	}
	
	public App getApp(int mobapp_version_id)
	{
		App app = null;
		for(int i=0; i<_apps.size(); i++)
		{
			if(mobapp_version_id == ((App)_apps.elementAt(i)).getMobappVersionId())
			{
				app = (App)_apps.elementAt(i);
				break;
			}
		}
		return app;
	}
	
	public void updateAppWithSameName(App newApp)
	{
		App app = null;
		for(int i=0; i<_apps.size(); i++)
		{
			if(newApp.getName() == ((App)_apps.elementAt(i)).getName())
			{
				_apps.setElementAt(newApp, i);
				break;
			}
		}
	}
}
