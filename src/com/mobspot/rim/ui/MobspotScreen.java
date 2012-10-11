
package com.mobspot.rim.ui;

import java.util.Enumeration;
import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

import com.blackberry.facebook.ui.Action;
import com.blackberry.facebook.ui.ActionListener;
import com.blackberry.util.log.Logger;
import com.mobspot.rim.Mobspot;


/**
 * Main screen to show the listing of all directories/files
 */
public class MobspotScreen extends FullScreen
{
    protected Mobspot _mobspot;
    //protected AppList _list;
    protected TopManager _topManager;
    //protected VerticalFieldManager _mainManager;
    protected MainManager _mainManager;
    
    protected Vector actionListeners = new Vector();
    
    
    protected Logger log = Logger.getLogger(getClass());
    
    MobspotScreen() 
    {
    	super(Manager.NO_VERTICAL_SCROLL /*| Screen.NO_SYSTEM_MENU_ITEMS*/);
    	_mobspot = (Mobspot)UiApplication.getUiApplication();
    	
    	//setFont(MobspotApplication.getFont(MobspotApplication.FONT_MEDIUM));
        
    	Background bg = BackgroundFactory.createSolidBackground(0x777777);
        this.setBackground(bg);
        
        _topManager = new TopManager();
        //_mainManager = new VerticalFieldManager(Manager.VERTICAL_SCROLL|Manager.VERTICAL_SCROLLBAR|Manager.USE_ALL_WIDTH);
        _mainManager = new MainManager();
        
        add(_topManager);
        add(_mainManager);
    }
    
    protected void setTitle(String title)
    {
    	_topManager.setTitle(title);
    }
    
        
    public boolean onClose()
    {
    	log.debug("onClose fired");
    	_mobspot.popScreen(this);
    	return true;    
    }
    
    public void addActionListener(ActionListener actionListener) {
		if (actionListener != null) {
			actionListeners.addElement(actionListener);
		}
	}

	protected void fireAction(String action) {
		fireAction(action, null);
	}

	protected void fireAction(String action, Object data) {
		Enumeration listenersEnum = actionListeners.elements();
		while (listenersEnum.hasMoreElements()) {
			((ActionListener) listenersEnum.nextElement()).onAction(new Action(this, action, data));
		}
	}
    
    
    private class TopManager extends Manager
    {
    	//private TextField _searchField;
    	private BackButton _backButton;
    	//private LabelField _titleField;
    	private String _title;
    	
    	private static final int PADDING = 4;
    	private static final int TOP_MANAGER_HEIGHT = 40;
    	//private static final int BACK_BUTTON_WIDTH = 33;
    	
    	
    	TopManager()
    	{
    		super(Manager.USE_ALL_WIDTH);
    		 
    	    //_searchField = new TextField();
    	    //_searchField.setMinimalWidth(30);
    	    _backButton = new BackButton();   	    
    	    
    	    add(_backButton);
    	}

		protected void sublayout(int width, int height) 
		{
			if(_title != null)
			{
				layoutChild(_backButton, width, height);
				setPositionChild(_backButton, PADDING, PADDING);
			}
			setExtent(Display.getWidth(), TOP_MANAGER_HEIGHT);
		}
		
		protected void subpaint(Graphics g)
		{
			for(int i=0; i<Display.getWidth(); i+=10)
			{
				g.drawBitmap(new XYRect(i, 0, 10, 62), Res.getBitmap(Res.BITMAP_TOOLBAR), 0, 0);
			}
			
			if(_title != null)
			{
				//backbutton
				paintChild(g, _backButton);
				
				//logo
				Bitmap logo2 = Res.getBitmap(Res.BITMAP_LOGO2);
				g.drawBitmap(new XYRect(Display.getWidth()-logo2.getWidth()-PADDING, 
						PADDING, logo2.getWidth(), logo2.getHeight()), 
						logo2, 0, 0);
				
				//title
				int targetWidth = Display.getWidth() - 
					(_backButton.getWidth() + logo2.getWidth() + (PADDING*2)+10);
				String txt = Utility.truncateText(_title, targetWidth, Mobspot.FONT_LARGE, true);
				int txtWidth = Mobspot.FONT_LARGE.getAdvance(txt);
				g.setColor(Mobspot.COLOR_TITLE);
				g.setFont(Mobspot.FONT_LARGE);
				int x = (Display.getWidth()/2) - (txtWidth/2);
				log.debug("drawing title at x="+x+" dw="+Display.getWidth()+" tw="+targetWidth);
				g.drawText(txt, x, PADDING, 
						DrawStyle.LEFT, targetWidth);
				
				
			}
			else
			{
				Bitmap logo1 = Res.getBitmap(Res.BITMAP_LOGO1);
				g.drawBitmap(new XYRect(PADDING, 
						PADDING, logo1.getWidth(), logo1.getHeight()), 
						logo1, 0, 0);
			
				//paintChild(g, _searchField);
			}
		}
		
		public void setTitle(String title)
		{
			_title = title;
		}
    }
    
    private class BackButton extends Field
    {
    	private String _label;
    	private int _width;
    	private int _height;
    	private boolean _hasFocus = false;
    	//private Font _font = MobspotApplication.getFont(MobspotApplication.FONT_MEDIUM);
    	
    	
    	BackButton()
    	{
    		super(FOCUSABLE);
    		_label = "Back";
    		setFont(Mobspot.FONT_MEDIUM);
    	}
    	
    	protected void onFocus(int direction)  
		{  
			_hasFocus = true;
		    invalidate();  
		}  
    	  
		protected void onUnfocus()  
		{  
			_hasFocus = false;
		    invalidate();  
		}  
    	
    	protected void drawFocus(Graphics graphics, boolean on)  
        {  
        	//log.debug("BackButton.drawFocus on="+on);
        }
    	
    	private Bitmap getCurrentBitmap()
    	{
    		return _hasFocus ? Res.getBitmap(Res.BITMAP_BACK_FOCUSED) : Res.getBitmap(Res.BITMAP_BACK_UNFOCUSED); 
    	}
    	
    	public void paint(Graphics g)
    	{
    		
    		g.drawBitmap(new XYRect(0,0,15,30), getCurrentBitmap(), 0, 0);
        	for(int i=15; i<_width - 7; i+=4)
        	{
        		g.drawBitmap(new XYRect(i,0,4,30), getCurrentBitmap(), 15, 0);
        	}
        	g.drawBitmap(new XYRect(_width - 7,0,7,30), getCurrentBitmap(), 26, 0);
        	
        	g.setColor(Color.WHITE); 
        	g.drawText(_label, 15,5/*(_height/2)-(MobspotApplication.getFont(MobspotApplication.FONT_MEDIUM).getHeight()/2)*/);
    	}

		protected void layout(int width, int height) 
		{
			_width = Mobspot.FONT_MEDIUM.getAdvance(_label)+22;
			_height = height;
			setExtent(_width, height);
		}
		
		protected boolean navigationClick(int status, int time)  
		{  
			log.debug("BackButton.navigationClick");
		    fieldChangeNotify(1);  
		    ((Mobspot)UiApplication.getUiApplication()).popActiveScreen();
		    
		    return true;  
		} 
		
		
    }
    
    
}
