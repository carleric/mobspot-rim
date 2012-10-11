package com.mobspot.rim.ui;

import com.mobspot.rim.Category;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;

public class RoundedList extends Manager
{
	//private static final int MARGIN = 5;
	//private static final int PADDING = 5;
	//public static final int ITEM_HEIGHT = 46;//32;
	private int _width;
	protected FieldChangeListener _fieldListener;

	RoundedList(FieldChangeListener fieldListener)
	{
		super(Manager.NO_HORIZONTAL_SCROLL);
		
		//_width = Display.getWidth() - ((_margin + _padding)*2);
		_fieldListener = fieldListener;
	}
	
	public void addItem(String text, int buttonId)
	{
		RoundedListItem b = new RoundedListItem(text, buttonId);
		this.addItem(b);
	}
	
	public void addItem(Category category)
	{
		RoundedListItem b = new RoundedListItem(category);
		this.addItem(b);
	}
	
	public void addItem(RoundedListItem item)
	{
		((Field)item).setChangeListener(_fieldListener);
		if(getFieldCount() == 0)
			item.setIsTop(true);
		else
			((RoundedListItem)(getField(getFieldCount()-1))).setIsBottom(false);
		item.setIsBottom(true);
		add(item);
	}

	protected void sublayout(int width, int height) 
	{
		_width = width-(2*UIConstants.ROUNDEDLIST_MARGIN);
		for(int i=0; i<getFieldCount(); i++)
		{
			RoundedListItem b =((RoundedListItem)getField(i));
			layoutChild(b, _width, UIConstants.ROUNDEDLISTITEM_HEIGHT+1);
			setPositionChild(b, UIConstants.ROUNDEDLIST_MARGIN, (i*UIConstants.ROUNDEDLISTITEM_HEIGHT)+UIConstants.ROUNDEDLIST_MARGIN);
		}
		setExtent(_width+UIConstants.ROUNDEDLIST_MARGIN+1, UIConstants.ROUNDEDLISTITEM_HEIGHT*getFieldCount()+UIConstants.ROUNDEDLIST_MARGIN+1);
	}
	
	protected void subpaint(Graphics graphics)
	{
		for(int i=0; i<getFieldCount(); i++)
		{
			paintChild(graphics, getField(i));
		}
	}
}
