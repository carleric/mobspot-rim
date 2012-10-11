package com.mobspot.rim.ui;


import com.blackberry.util.log.Logger;
import com.mobspot.rim.App;

import net.rim.device.api.ui.*;

class AppList extends RoundedList implements FieldChangeListener
{	
	protected Logger log = Logger.getLogger(getClass());
	
	public AppList(FieldChangeListener fieldListener) 
	{
		super(fieldListener);
	}
	
	public void addApp(App app)
	{
		AppListItem b = new AppListItem(app);
		((Field)b).setChangeListener(_fieldListener);
		if(getFieldCount() == 0)
			b.setIsTop(true);
		else
			((RoundedListItem)(getField(getFieldCount()-1))).setIsBottom(false);
		b.setIsBottom(true);
		add(b);
	}
	
	public void updateApp(App app)
	{
		for(int i=0; i<getFieldCount(); i++)
		{
			if(((AppListItem)getField(i)).getApp().getName().equals(app.getName()))
			{
				replace(getField(i), new AppListItem(app));
				break;
			}
		}
	}
	
	public boolean hasApp(String name)
	{
		for(int i=0; i<getFieldCount(); i++)
		{
			if(((AppListItem)getField(i)).getApp().getName().equals(name))
			{
				return true;
			}
		}
		return false;
	}
	
	public void addOrUpdateApp(App app)
	{
		if(hasApp(app.getName()))
			updateApp(app);
		else
			addApp(app);
	}
	
	/*public void deleteAll()
	{
		log.debug("deleting all fields in AppList");
		for(int i=0; i<getFieldCount(); i++)
		{
			delete(getField(i));
		}
	}*/

	public boolean containsAppWithModule(String moduleName) 
	{
		for (int i = 0; i < getFieldCount(); i++) 
		{
			AppListItem appItem = (AppListItem)getField(i);
			App app = appItem.getApp();
			if (app.hasModules() && app.getModules().contains(moduleName))
				return true;
			else
				continue;
		}
		return false;
	}

	public void fieldChanged(Field field, int context) 
	{
		AppListItem item = (AppListItem)field;
		log.debug("clicked on app id="+item.getItemId());
	}
}
