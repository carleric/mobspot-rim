package com.mobspot.rim.ui;

import com.blackberry.util.log.Logger;
import com.mobspot.rim.Category;
import com.mobspot.rim.Mobspot;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;


public class RoundedListItem extends Field
{
	protected int _itemId;
	protected int _padding = 10;
	protected String _text; 
	protected Category _category;
    
    protected int _height;
	protected int _width;
    
	protected boolean _isTop = false;
	protected boolean _isBottom = false;
    
	protected boolean _hasFocus = false;
    protected Bitmap bgCurrent;
	
	
	
	//private Font _font = MobspotApplication.getFont(MobspotApplication.FONT_MEDIUM);
	protected Logger log = Logger.getLogger(getClass());

    public RoundedListItem(String text, int buttonId)
    {
        super(FOCUSABLE);
        _text = text;
        _itemId = buttonId;
        setFont(Mobspot.FONT_MEDIUM);
    }   
    
    public RoundedListItem(Category category)
    {
    	this(category.getName(), category.getId());
    	_category = category;
    }
    
    public int getItemId()
    {
    	return _itemId;
    }
    
    public void setIsTop(boolean isTop)
    {
    	_isTop = isTop;
    }
    
    public void setIsBottom(boolean isBottom)
    {
    	_isBottom = isBottom;
    }

    public int getPreferredWidth() 
    {
        return _width;
    }

    public int getPreferredHeight() 
    {
        return _height;
    }

    protected void layout(int width, int height) 
    {
    	_width = width;
    	_height = height;
        setExtent(_width, _height);
    }
    
    protected boolean navigationClick(int status, int time)  
    {  
    	log.debug("RoundedListButton.navigationClick");
        fieldChangeNotify(1);  
        return true;  
    }  
    
    protected void fieldChangeNotify(int context)  
    {  
    	log.debug("RoundedListButton.fieldChangeNotify");
        try  
        {  
            this.getChangeListener().fieldChanged(this, context);  
        }  
        catch (Exception e)  
        {
        	log.error(e.getMessage());
        }  
    }  
  
    protected void onFocus(int direction)  
    {  
    	//log.debug("onFocus direction="+direction);
    	_hasFocus = true;
    	invalidate();  
    }  
  
    protected void onUnfocus()  
    {  
    	//log.debug("onUnFocus");
    	_hasFocus = false;
    	
        invalidate();  
    }  
  
    protected void drawFocus(Graphics graphics, boolean on)  
    {  
    	//log.debug("RoundedListItem.drawFocus on="+on);
    }
  
    protected void paint(Graphics g)  
    {  
    	
    	int startColor = _hasFocus ? Mobspot.COLOR_FOCUSED1 : Mobspot.COLOR_UNFOCUSED1;
    	int endColor = _hasFocus ? Mobspot.COLOR_FOCUSED2 : Mobspot.COLOR_UNFOCUSED2;
    	final int OFFSET = 0;
    	
    	if(_isTop && _isBottom)
    	{
    		Utility.paintRoundedGradientBlackBorder(g, OFFSET, OFFSET, _width, _height, startColor, endColor, 
    				Utility.FLAG_TOP_LEFT | Utility.FLAG_TOP_RIGHT | Utility.FLAG_BOTTOM_LEFT | 
    				Utility.FLAG_BOTTOM_RIGHT); 
    	}
    	else if(_isTop && !_isBottom)
    	{
    		Utility.paintRoundedGradientBlackBorder(g, OFFSET, OFFSET, _width, _height, startColor, endColor, 
    				Utility.FLAG_TOP_LEFT | Utility.FLAG_TOP_RIGHT);
    	}
    	else if(!_isTop && _isBottom)
    	{
    		Utility.paintRoundedGradientBlackBorder(g, OFFSET, OFFSET, _width, _height, startColor, endColor, 
    				Utility.FLAG_BOTTOM_LEFT | Utility.FLAG_BOTTOM_RIGHT);
    	}
    	else
    	{
    		Utility.paintRoundedGradientBlackBorder(g, OFFSET, OFFSET, _width, _height, startColor, endColor, 
    				Utility.FLAG_NO_CORNERS);
    	}
    	
    	g.drawBitmap(new XYRect(_width-UIConstants.CHEVRON_WIDTH+6, (_height/2)-(UIConstants.CHEVRON_HEIGHT/2), UIConstants.CHEVRON_WIDTH, UIConstants.CHEVRON_HEIGHT), 
    			Res.getBitmap(Res.BITMAP_CHEVRON), 0, 0);
      
        
        if(_text != null && _text.compareTo("") != 0)
        {
        	g.setColor(Mobspot.COLOR_TEXT);  
        	g.setFont(Mobspot.FONT_MEDIUM);
        	int txtWidth = _width - (UIConstants.CHEVRON_WIDTH + _padding);
        	String txt = Utility.truncateText(_text, txtWidth, Mobspot.FONT_MEDIUM, true);
        	g.drawText(txt, _padding, (_height/2)-(Mobspot.FONT_MEDIUM.getHeight()/2), Graphics.LEFT, txtWidth);
        }
        
        if(_category != null)
        {
        	g.setColor(Mobspot.COLOR_TEXT);
        	g.setFont(Mobspot.FONT_MEDIUM);
        	String count = String.valueOf(_category.getAppCount());
        	int txtWidth = Mobspot.FONT_MEDIUM.getAdvance(count);
        	g.drawText(count, _width-(UIConstants.CHEVRON_WIDTH + _padding + txtWidth), (_height/2)-(Mobspot.FONT_MEDIUM.getHeight()/2), Graphics.LEFT, txtWidth);
        }
        
    }  
    
    public Category getCategory()
    {
    	return _category;
    }
    
    public void invalidate()
    {
    	super.invalidate();
    }
}

