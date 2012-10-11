package com.mobspot.rim.ui;


import com.blackberry.util.log.Logger;
import com.mobspot.rim.Mobspot;
import com.mobspot.rim.network.Connection;
import com.mobspot.rim.network.ConnectionListener;
import com.mobspot.rim.network.Request;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class DownloadBitmapField extends Field implements ConnectionListener
{
	private Connection _connection;
	private Bitmap _bitmap;
	private boolean _hasBitmap = false;
	private String _url;
	private int _width = 40;
	private int _height = 40;
	
	protected Logger log = Logger.getLogger(getClass());
	
	public DownloadBitmapField()
	{
		super();
	}
	
	public DownloadBitmapField(String url)
	{
		this();
		fetchBitmap(url);
	}
	
	public void fetchBitmap(String url)
	{
		_url = url;
		if(url != "" && url.indexOf("null") < 0)
		{
			_connection = new Connection("CONN_BITMAP="+url);
			_connection.addListener(this);
			_connection.start();
			//_connection.fetchImage(url);
			_connection.addRequest(new Request(url));
		}
	}
	
	

	public void stateChanged(int state) {
		// TODO Auto-generated method stub
		
	}

	public void updateResponse(String response) {
		// TODO Auto-generated method stub
		
	}

	public void updateResponse(Bitmap image) 
	{
		log.debug("DownloadBitmap.updateResponse got image w="+image.getWidth()+" h="+image.getHeight());
		try
		{
			_hasBitmap = true;
			_width = image.getWidth()+2;
			_height = image.getHeight()+2;
			_bitmap = image;
			_connection.stop();
			_connection = null;
		}
		catch(Exception e)
		{
			log.debug("DownloadBitmap.updateResponse exception: "+e.getMessage());
		}
	}

	public void updateResponseCode(int responseCode) 
	{
		log.debug("DownloadBitmap.updateResponse got responseCode="+responseCode);
		if(responseCode >= 400)
		{
			_connection.stop();
			_connection = null;
		}
	}

	public void updateStatus(String status) {
		// TODO Auto-generated method stub
		
	}
	
	protected void paint(Graphics g)
	{
		if(_hasBitmap)
		{
			g.drawBitmap(0, 0, _bitmap.getWidth(), _bitmap.getHeight(), _bitmap, 0, 0);
		}
		else
		{
			//g.drawText("loading...", 0, 0);
			g.drawBitmap(0, 0, Res.getBitmap(Res.BITMAP_DEFUALT_APP_ICON).getWidth(), Res.getBitmap(Res.BITMAP_DEFUALT_APP_ICON).getHeight(), Res.getBitmap(Res.BITMAP_DEFUALT_APP_ICON), 0, 0);
		}
	}

	protected void layout(int width, int height) 
	{
		setExtent(_width, _height);
	}

	public void onResponse(Request r) {
		// TODO Auto-generated method stub
		
	}
}
