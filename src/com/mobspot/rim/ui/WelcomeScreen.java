package com.mobspot.rim.ui;

import com.blackberry.util.log.Logger;
import com.mobspot.rim.Mobspot;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BitmapField;

public class WelcomeScreen extends MobspotScreen implements FieldChangeListener
{
	
	private RoundedList _list1;
	
	private static final int BTN_LOGIN = 0;
	
	protected Logger log = Logger.getLogger(getClass());
	
	public WelcomeScreen()
	{
		super();
		setTitle(null);
		
		BitmapField welcomeImage = new BitmapField(Res.getBitmap(Res.BITMAP_WELCOME));
		welcomeImage.setMargin(5, 5, 5, 5);
		_mainManager.add(welcomeImage);
		
		_list1 = new RoundedList(this);
		_list1.addItem("Login with Facebook", BTN_LOGIN);		
		_mainManager.add(_list1);
		
		_mobspot.invokeLater(new Runnable()
		{
			public void run()
			{
				_list1.setFocus();
			}
		});
	}

	public void fieldChanged(Field field, int context) 
	{
		log.debug("WelcomeScreen.fieldChanged field="+field.toString());
		
		RoundedListItem buttonField = (RoundedListItem) field;
		switch(buttonField.getItemId())
		{
			case BTN_LOGIN:
				fireAction(UIConstants.ACTION_LOG_ME_IN);
				break;
			
			default:
				break;
		}
		
	}
	
	public boolean onClose()
	{
		_mobspot.requestBackground();
		return true;
	}
}
