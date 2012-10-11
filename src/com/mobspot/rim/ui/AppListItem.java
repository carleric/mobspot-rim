
package com.mobspot.rim.ui;

import com.blackberry.util.log.Logger;
import com.mobspot.rim.App;
import com.mobspot.rim.Mobspot;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

public class AppListItem extends RoundedListItem
{
	private App _app;
	
	protected Logger log = Logger.getLogger(getClass());
	
	public AppListItem(App app)
    {
    	super(null, app.getMobappVersionId());
    	_app = app;
    }
	
	protected void paint(Graphics g)
	{
		super.paint(g);
		
		Bitmap b;
		if(_app.hasIcon())
		{
			b = _app.getIcon();
		}
		else
		{
			b = Res.getBitmap(Res.BITMAP_DEFUALT_APP_ICON);
		}
		g.drawBitmap(new XYRect(5, UIConstants.PADDING_TOP, b.getWidth(), b.getHeight()), b, 0, 0);
		
		if(!_app.isScannedFromDevice())
		{
			int priceTextWidth = Mobspot.FONT_SMALL.getAdvance(_app.getPrice());
			g.setFont(Mobspot.FONT_SMALL);
			g.drawText(_app.getPrice(), _width - (priceTextWidth + UIConstants.CHEVRON_WIDTH + 2), UIConstants.PADDING_TOP, Graphics.LEFT, priceTextWidth);
		}
		
		int textWidth = _width - (b.getWidth() + UIConstants.PADDING_LEFT + UIConstants.RATING_WIDTH + UIConstants.CHEVRON_WIDTH);
		
		g.setColor(Mobspot.COLOR_TITLE);
		g.setFont(Mobspot.FONT_MEDIUM);
		String title = Utility.truncateText(_app.getName(), textWidth, Mobspot.FONT_MEDIUM, true);
    	g.drawText(title, UIConstants.ICON_WIDTH + UIConstants.PADDING_LEFT, UIConstants.PADDING_TOP);
		//Utility.drawTextToGradient(g, _app.getName(), Mobspot.FONT_MEDIUM, Mobspot.COLOR_TITLE, 
		//		ICON_WIDTH+PADDING_LEFT, PADDING_TOP, textWidth, Mobspot.COLOR_BACKGROUND);
    	
    	g.setColor(Mobspot.COLOR_DESCRIPTION);
    	g.setFont(Mobspot.FONT_SMALL);
    	
    	if(_app.getDescription() != null &&! _app.getDescription().startsWith("null"))
    	{
    		String desc = _app.getDescription();
    		
    		desc = Utility.truncateText(desc, textWidth, Mobspot.FONT_SMALL, true);
    		g.drawText(desc, UIConstants.ICON_WIDTH+UIConstants.PADDING_LEFT, UIConstants.PADDING_TOP+Mobspot.FONT_MEDIUM.getHeight(), Graphics.LEFT, textWidth);
    	}
		
    	if(!_app.isScannedFromDevice())
    		g.drawBitmap(new XYRect(_width-(UIConstants.RATING_WIDTH+UIConstants.CHEVRON_WIDTH), UIConstants.PADDING_TOP+Mobspot.FONT_MEDIUM.getHeight(), UIConstants.RATING_WIDTH, UIConstants.RATING_HEIGHT), Res.getRatingBitmap(_app.getRatingThisVersionInt()), 0, 0);
    	
	}
	
	
	public App getApp()
	{
		return _app;
	}
	
	protected boolean navigationClick(int status, int time)  
	{  
		log.debug("AppListItem.navigationClick");
	    fieldChangeNotify(1);  
	    
	    
	    return true;  
	}
	
	protected void onFocus(int direction)  
    {  
    	log.debug("onFocus direction="+direction+" appItem title="+_app.getName());
    	_hasFocus = true;
    	invalidate(); 
    }  

	
}
