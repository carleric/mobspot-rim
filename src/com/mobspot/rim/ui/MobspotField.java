package com.mobspot.rim.ui;

import com.mobspot.rim.Mobspot;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public class MobspotField extends LabelField
{

	//String _text;
	private int _color = Mobspot.COLOR_TEXT;
	private Font _font = Mobspot.FONT_MEDIUM;
	private int _width, _height;
	//private static final int PADDING = 4;
	//private static final int MINHEIGHT =  Mobspot.FONT_MEDIUM.getHeight();
	
	public MobspotField()
	{
		super();		
	}
	
	public MobspotField(String text)
	{
		this();
		super.setText(text);
		super.setPadding(UIConstants.PADDING_TOP, UIConstants.PADDING_RIGHT, UIConstants.PADDING_BOTTOM, UIConstants.PADDING_LEFT);
		//_text = text;
	}
	
	public MobspotField(String text, int color)
	{
		this(text);
		_color = color;
	}
	
	public MobspotField(String text, int color, Font font)
	{
		this(text);
		_font = font;
		_color = color;
	}
	
	public MobspotField(String text, int color, long style)
	{
		super(text, style);
		_color = color;
	}
	
	/*public void setText(String text)
	{
		_text = text;
	}*/
	
	/*protected void layout(int width, int height) 
	{
		String txt = this.getText();
		_width = width;
		int h=0;
		if(txt != null)
		{
			String tmp = txt;
			for(int i=0; tmp.length() > 0; i++)
			{
				if(i == tmp.length() -1)
					break;
				String leading = tmp.substring(0, i);
				if(_font.getAdvance(leading) > width)
				{
					h += _font.getHeight();
					tmp = tmp.substring(i); 
					i = 0;
				}
			}
		}
		_height = Math.max(h, MINHEIGHT);
		setExtent(_width, _height);
	}*/

	protected void paint(Graphics g) 
	{
		//g.setColor(Color.BLACK);
		//g.drawRect(0, 0, _width, _height);
		
		g.setColor(_color);
		g.setFont(_font);
		setFont(_font);
		super.paint(g);
		/*int h=0;
		if(_text != null)
		{
			String tmp = _text;
			for(int i=0; tmp.length() > 0; i++)
			{
				String leading = tmp.substring(0, i);
				
				if(i == tmp.length())
				{
					g.drawText(leading, 0, h);
					break;
				}
				
				if(_font.getAdvance(leading) > _width)
				{
					g.drawText(leading, 0, h);
					h += _font.getHeight();
					tmp = tmp.substring(i); 
					i = 0;
				}
			}
		}*/		
	}
	
	public int getPreferredHeight()
	{
		return _height;
	}
	
}
