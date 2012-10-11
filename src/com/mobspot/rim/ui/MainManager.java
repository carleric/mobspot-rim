package com.mobspot.rim.ui;

import com.blackberry.util.log.Logger;
import com.mobspot.rim.Mobspot;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class MainManager extends VerticalFieldManager 
{
	
	private boolean _showLoader = false;
	private String _statusText;
	private int _frame = 0;
	private static final int NUM_FRAMES = 16; 
	private static final int FRAME_DELAY = 50;
	private long _frame_timer = 0;
	private int _width, _height;
	private static final int MIN_SHOW_LOADER_TIME = 500;
	private long _start_loader = 0;
	
	protected Logger log = Logger.getLogger(getClass());

	public MainManager()
	{
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR | Manager.USE_ALL_WIDTH | Manager.USE_ALL_HEIGHT);
	}

	protected void sublayout(int width, int height) 
	{
		/*int y = 0;
		for(int i=0; i<getFieldCount(); i++)
		{
			//layoutChild(getField(i), width, height);
			//setPositionChild(getField(i), 0, y);
			y += getField(i).getHeight();
		}*/
		_width = width;
		_height = height;
		//setExtent(_width, _height);
		
		super.sublayout(width, height);
	}
	
	protected void subpaint(Graphics g)
	{
		//log.debug("paint", "painting MainManager background w="+_width+" h="+_height);
		g.setColor(Mobspot.COLOR_BACKGROUND);
		int h = Math.max(getVirtualHeight(), Display.getHeight())+40;
		g.fillRect(0, 0, _width, h);
		
		setFont(Mobspot.FONT_MEDIUM);
		
		
		
		
			
		if(_showLoader || (System.currentTimeMillis() - _start_loader < MIN_SHOW_LOADER_TIME))
		{
			if(_start_loader == 0)
				_start_loader = System.currentTimeMillis();
			
			if(_frame_timer ==0)
				_frame_timer = System.currentTimeMillis();
			
			try
			{
				//log.debug("MainManager.subpaint: painting loader. frame="+_frame);
				//Thread.sleep(500);	
				
				g.drawBitmap(new XYRect((getContentWidth()/2)-60, 100, 120, 20), Res.getBitmap(Res.BITMAP_DOWNLOADING), _frame*2, 0);
				
				g.setColor(Color.BLACK);
				g.drawRect((getContentWidth()/2)-60, 100, 120, 20);
				
				if(_frame < NUM_FRAMES)
				{
					if((System.currentTimeMillis() -_frame_timer) > FRAME_DELAY)
					{
						_frame ++;
						_frame_timer = System.currentTimeMillis();
					}
				}
				else
					_frame = 0;
				
				g.setFont(Mobspot.FONT_SMALL);
				g.setColor(Color.WHITE);
				int statusTextWidth = (Mobspot.FONT_SMALL).getAdvance(_statusText);
				g.drawText(_statusText, 
						(getContentWidth()/2)
						 -(statusTextWidth/2),
						 125);
				StringBuffer s = new StringBuffer();
				for(int i=0; i<_frame/4; i++)
				{
					s.append(".");
				}
				g.drawText(s.toString(), getContentWidth() - ((getContentWidth() - statusTextWidth)/2), 125);
				
				invalidate();
				
			}
			catch(Exception e)
			{
				log.debug("MainManager.subpaint: exception painting loader: "+e.getMessage());
			}
		}
		else
		{
			//log.debug("paint", "MainManager.subpaint: painting children");
			for(int i=0; i<getFieldCount(); i++)
			{
				paintChild(g, getField(i));
			}
			//super.paint(g);
		}
		
	}
	
	public void setShowLoader(boolean showLoader)
	{
		_showLoader = showLoader;
		invalidate();
	}
	
	public void setStatusText(String statusText)
	{
		_statusText = statusText;
	}

}
