package com.mobspot.rim.ui;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.extension.component.PictureScrollField;
import net.rim.device.api.ui.extension.component.PictureScrollField.HighlightStyle;
import net.rim.device.api.ui.extension.component.PictureScrollField.ScrollEntry;

import com.mobspot.rim.App;
import com.mobspot.rim.Mobspot;
import com.mobspot.rim.network.Connection;
import com.mobspot.rim.network.ConnectionListener;
import com.mobspot.rim.network.Request;

public class ScreenshotsScreen extends MobspotScreen implements ConnectionListener
{
	private PictureScrollField _pictureScrollField;
	private RoundedManager _screenshotsRound;
	private MobspotField _screenShotsLabel;
	
	private Vector _downloadedScreenshots;
	
	private Connection _connection;
	
	
	public ScreenshotsScreen()
	{
		setTitle("Screenshots");
		
		_downloadedScreenshots = new Vector();
		
		_screenshotsRound = new RoundedManager();
		_pictureScrollField = new PictureScrollField(Display.getWidth()-50, UIConstants.SCREENSHOT_HEIGHT);
		_pictureScrollField.setHighlightStyle(HighlightStyle.ILLUMINATE);
        _pictureScrollField.setHighlightBorderColor(Color.RED);
        _pictureScrollField.setBackground(BackgroundFactory.createSolidTransparentBackground(Mobspot.COLOR_BACKGROUND, 0));
        _pictureScrollField.setLabelsVisible(true);  
        _screenshotsRound.add(_pictureScrollField);
        
        _screenShotsLabel = new MobspotField("Screenshots");
		_mainManager.add(_screenShotsLabel);
		_mainManager.add(_screenshotsRound);
		
		_connection = new Connection("CONN_SCREENSHOTS");
		_connection.addListener(this);
		_connection.start();
	}
	
	public void showScreenshots(App app)
	{
		_mainManager.setShowLoader(true);
		_mainManager.setStatusText("Loading App Screenshots");
		
		for(int i=0; i< app.getScreenShotURLs().size(); i++)
		{
			String sUrl = (String)app.getScreenShotURLs().elementAt(i);
			log.debug("adding request for screenshot at "+sUrl);
			_connection.addRequest(new Request(sUrl));
			
		}
		_connection.doRequests();		
	}
	
	private void updatePictureScroller()
	{
		try 
		{
			ScrollEntry [] entries = new ScrollEntry[_downloadedScreenshots.size()];
			for(int i=0; i<_downloadedScreenshots.size(); i++)
			{
				Bitmap a = (Bitmap)_downloadedScreenshots.elementAt(i);
				int scaleWidth = (a.getHeight()/UIConstants.SCREENSHOT_HEIGHT)*a.getWidth();
				Bitmap b = new Bitmap(scaleWidth, UIConstants.SCREENSHOT_HEIGHT);
				a.scaleInto(b, Bitmap.FILTER_LANCZOS);
				entries[i] = new ScrollEntry(a, "screenshot "+(i+1), "screenshot "+(i+1));
			}
			_pictureScrollField.setData(entries, 0);
			_screenShotsLabel.setText("Screenshots (" + _downloadedScreenshots.size() + ")");
			
		} catch (Exception e) {
			log.error(e.getMessage());
			//fireAction(ACTION_ERROR, e.getMessage());
		}
	}

	public void onResponse(final Request r) 
	{
		_mobspot.invokeLater(new Runnable() 
        {
            public void run()
            {
				if(r.getResponse().getResponseCode() == 200)
				{
					_downloadedScreenshots.addElement(r.getResponse().getData());
					
		    		updatePictureScroller();
		    		
		    		if(_downloadedScreenshots.size() > 0)
						_mainManager.setShowLoader(false);
				}
            }
        });
	}
}
