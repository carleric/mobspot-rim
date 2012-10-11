package com.mobspot.rim.ui;


import com.blackberry.util.log.Logger;
import com.mobspot.rim.Mobspot;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.TextField;

public class RoundedManager extends Manager 
{
	
	private int _width;
	private int _height = 0;
	private int _minheight = 32;
	private int _y;
	//private static final int MARGIN = 5;
	//private static final int PADDING = 10;
	
	private int _startColor;
	private int _endColor;
	
	private int _cornerFlags;
	
	protected Logger log = Logger.getLogger(getClass());

	protected RoundedManager()
	{
		super(Manager.NO_VERTICAL_SCROLL | Manager.FOCUSABLE);
		_startColor = Mobspot.COLOR_UNFOCUSED1;
		_endColor = Mobspot.COLOR_UNFOCUSED2;
		_cornerFlags = Utility.FLAG_TOP_LEFT | Utility.FLAG_TOP_RIGHT | Utility.FLAG_BOTTOM_LEFT | Utility.FLAG_BOTTOM_RIGHT;
	}
	
	protected RoundedManager(int minheight)
	{
		this();
		_minheight = minheight;
	}
	
	protected RoundedManager(int height, int startColor, int endColor)
	{
		this(height);
		_startColor = startColor;
		_endColor = endColor;
	}
	
	protected RoundedManager(int height, int startColor, int endColor, int cornerFlags)
	{
		this(height, startColor, endColor);
		_cornerFlags = cornerFlags;
	}
	

	protected void sublayout(int width, int height) 
	{
		_width = width-(2*UIConstants.MARGIN);
		//_height = height;
		int y = UIConstants.PADDING;
		for(int i=0; i < getFieldCount(); i++)
    	{
			layoutChild(getField(i), _width-(2*UIConstants.PADDING), height);
			setPositionChild(getField(i), UIConstants.PADDING, y);
    		y += getField(i).getHeight() + UIConstants.PADDING;
    	}
		_height = Math.max(y, _minheight);
		
		//log.debug("RoundedManager.sublayout setting extent w="+_width+" height="+_height);
		setExtent(_width+UIConstants.MARGIN+10, _height+UIConstants.MARGIN+10);
	}
	
	protected void subpaint(Graphics g)
	{
		Utility.paintRoundedGradientBlackBorder(g, UIConstants.MARGIN, UIConstants.MARGIN, _width, _height, _startColor, _endColor, _cornerFlags);
		
    	for(int i=0; i < getFieldCount(); i++)
    	{
    		paintChild(g, getField(i));
    	}
	}
	
	protected void onFocus(int direction)
	{
		log.debug("RoundedManager.onFocus dir="+direction);
		/*if(getFieldCount()>0)
			getField(0).setFocus();*/
		
		_startColor = Mobspot.COLOR_FOCUSED1;
		_endColor = Mobspot.COLOR_FOCUSED2;
		invalidate();
		super.onFocus(direction);
		
	}
	
	protected void onUnfocus()
	{
		_startColor = Mobspot.COLOR_UNFOCUSED1;
		_endColor = Mobspot.COLOR_UNFOCUSED2;
		invalidate();
		super.onUnfocus();
	}
	

	
	
}
