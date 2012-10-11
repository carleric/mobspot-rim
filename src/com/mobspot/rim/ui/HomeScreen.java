package com.mobspot.rim.ui;

import com.blackberry.util.log.Logger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.Dialog;

public class HomeScreen extends MobspotScreen implements FieldChangeListener
{
	private RoundedList _list1;
	private RoundedList _list2;
	
	private static final int BTN_USER = 0;
	private static final int BTN_MYAPPS = 1;
	private static final int BTN_FEATUREDAPPS = 2;
	private static final int BTN_CATEGORIES = 3;
	private static final int BTN_ACTIVITY = 4;
	private static final int BTN_FRIENDS_APPS = 5;
	
	protected Logger log = Logger.getLogger(getClass());
	
	public HomeScreen()
	{
		super();
		setTitle(null);
		
		if(!_mobspot.msc.addOrUpdateUser(_mobspot.fbc))
		{
			fireAction(UIConstants.ACTION_LOGIN_FAILED);
			fireAction(UIConstants.ACTION_ERROR, "There was an error logging into mobspot.  Please try logging in again.");
		}
		
		if(_mobspot.fbc.hasSession())
		{
			try
			{
				_list1 = new RoundedList(this);
				_list1.addItem("Welcome "+_mobspot.fbc.getLoggedInUser().getFirstName()+"!", BTN_USER);
				_mainManager.add(_list1);
			}
			catch(Throwable e){}
		}	
		
			
		_list2 = new RoundedList(this);
		_list2.addItem("My Apps", BTN_MYAPPS);
		_list2.addItem("Featured Apps", BTN_FEATUREDAPPS);
		_list2.addItem("Browse Apps by Category", BTN_CATEGORIES);
		_list2.addItem("Friends Apps", BTN_FRIENDS_APPS);
		//_list2.addItem("Site Activity", BTN_ACTIVITY);
		_mainManager.add(_list2);
		
		_mobspot.invokeLater(new Runnable()
		{
			public void run()
			{
				_list2.setFocus();
			}
		});
	}

	public void fieldChanged(Field field, int context) 
	{
		log.debug("WelcomeScreen.fieldChanged field="+field.toString());
		
		RoundedListItem buttonField = (RoundedListItem) field;
		switch(buttonField.getItemId())
		{
			case BTN_MYAPPS:
				fireAction(UIConstants.ACTION_MY_APPS);
				break;
			case BTN_FEATUREDAPPS:
				fireAction(UIConstants.ACTION_FEATURED_APPS);
				break;
			case BTN_CATEGORIES:
				fireAction(UIConstants.ACTION_CATEGORIES);
				break;
			case BTN_FRIENDS_APPS:
				fireAction(UIConstants.ACTION_FRIENDS_APPS);
				break;
			case BTN_ACTIVITY:
				fireAction(UIConstants.ACTION_ACTIVITY);
				break;
			default:
				break;
		}
		
	}
	
	public boolean onClose()
	{
		int promptResponse = Dialog.ask("Please choose:", new String[] { "Background", "Exit", "Logout & Exit" }, 0);
		switch(promptResponse)
		{
			case 0:
				_mobspot.requestBackground();
				break;
			case 1:
				log.info("User Exit.");
				_mobspot.saveAndExit();
				break;
			case 2:
				log.info("User Logout & Exit.");
				_mobspot.logoutAndExit();
				break;
		}
		return true;
	}
}
