package com.mobspot.rim.ui;

import java.util.Enumeration;
import java.util.Vector;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

import com.blackberry.util.log.Logger;
import com.mobspot.rim.App;
import com.mobspot.rim.AppCache;

public class MyAppsScreen extends MobspotScreen implements FieldChangeListener
{
	private AppList _list;
	
	protected Logger log = Logger.getLogger(getClass());
	
	public MyAppsScreen()
	{
		super();
		
		setTitle("My Apps");
		
		_list = new AppList(this);
		_mainManager.add(_list);
    }
	
    
    public void readInstalledApps()
    {
    	scanDevice();
    }

	
	public void fieldChanged(Field field, int context) 
	{
		log.debug("MyAppsScreen.fieldChanged: "+((AppListItem)field).getItemId());
		fireAction(UIConstants.ACTION_APP_DETAIL, ((AppListItem)field).getApp());
	}

	
	
	//TODO: merge AppCache and AppList?
	private void scanDevice()
	{
		_mobspot.invokeLater(new Runnable()
		{
			
			public void run()
			{
				AppCache appCache = _mobspot.getAppCache();
				
				//scan method A: CodeModuleGroupManager API
				CodeModuleGroup [] groups = CodeModuleGroupManager.loadAll();
		        for (int i=0; i < groups.length; i++)
		        {
		        	String modName = groups[i].getFriendlyName();
		        	int modFlags = groups[i].getFlags();
		        	Vector modules = new Vector();
		        	for(Enumeration en = groups[i].getModules(); en.hasMoreElements();)
		        	{
		        		modules.addElement((String)en.nextElement().toString());
		        	}
		        	
		        	if((modFlags & CodeModuleGroup.FLAG_LIBRARY) != CodeModuleGroup.FLAG_LIBRARY)
		        	{
		        		App app = new App(-1, -1, true, 
		        				modName, 
		        				groups[i].getDescription(), 
		        				groups[i].getVendor(),
		        				groups[i].getVersion(),
		        				modules);
		        		if(!_mobspot.msc.isAppNameHidden(modName))
		        		{
			        		_list.addApp(app);
			        		appCache.addApp(app);
		        		}
		        	}
		        }
		        
		        //scan method B: CodeModuleManager API
		        int [] handles = CodeModuleManager.getModuleHandles(false);
		        for(int i = 0; i < handles.length; i++)
		        {
		        	String modName = CodeModuleManager.getModuleName(handles[i]);
		        	String modDesc = CodeModuleManager.getModuleDescription(handles[i]);
		        	if(!_list.containsAppWithModule(modName)
		        			&& !_mobspot.msc.isAppNameHidden(modName))
		        	{
		        		App app = new App(-1, -1, true,
		        				modName, 
		        				modDesc,
		        				CodeModuleManager.getModuleVendor(handles[i]),
		        				CodeModuleManager.getModuleVersion(handles[i]),
		        				null
		        				);
		        		_list.addApp(app);
		        		appCache.addApp(app);
		        	}
		        }
		        
		        //scan method C: ApplicationManager API
		        ApplicationManager manager = ApplicationManager.getApplicationManager();
		        ApplicationDescriptor descriptors[] = manager.getVisibleApplications();
		        for(int i=0; i< descriptors.length; i++)
		        {
		        	String appname = descriptors[i].getName();
		        	Vector modules = new Vector();
	        		modules.addElement(descriptors[i].getModuleName());
	        		
	        		if(!_mobspot.msc.isAppNameHidden(appname))
		        	{
		        		App app = new App(
		        				-1, -1, true,
		        				appname,
		        				CodeModuleManager.getModuleDescription(descriptors[i].getModuleHandle()),
		        				CodeModuleManager.getModuleVendor(descriptors[i].getModuleHandle()),
		        				descriptors[i].getVersion(),
		        				modules);
		        		
		        		app.setIcon(descriptors[i].getEncodedIcon().getBitmap());
		      
		        		appCache.addOrUpdateApp(app);
		        		
		        		if(_list.containsAppWithModule(descriptors[i].getModuleName()))
			        		_list.updateApp(app);
			        	else
			        		_list.addApp(app);
		        	}
		        }
			}
		});		
	}
	
	public boolean onClose()
	{
		close();
		
		return true;
		
	}
   
}
