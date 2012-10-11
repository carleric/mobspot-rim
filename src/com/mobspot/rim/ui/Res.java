package com.mobspot.rim.ui;

import net.rim.device.api.system.Bitmap;

public class Res 
{

	//image resources - load them once at startup
	private static Bitmap _0star;
	private static Bitmap _1star;
	private static Bitmap _2star;
	private static Bitmap _3star;
	private static Bitmap _4star;
	private static Bitmap _5star;
	
	private static Bitmap _singleBorder;
	
	private static Bitmap _welcomeImage;
	
	private static Bitmap _backBtnUnFocused;
	private static Bitmap _backBtnFocused;
	
	private static Bitmap _chevron;
	private static Bitmap _chevronOrange;
	
	private static Bitmap _defaultAppIcon;
	private static Bitmap _downloading;
	
	private static Bitmap _toolbar;
	
	private static Bitmap _logo1;
	private static Bitmap _logo2;
	
	private static Bitmap _thumbsup;
	private static Bitmap _thumbsdown;
	
// image identifiers
	
	public static final int BITMAP_SINGLE_BORDER = 0;
	
	public static final int BITMAP_WELCOME = 1;
	
	public static final int BITMAP_BACK_UNFOCUSED = 2;
	public static final int BITMAP_BACK_FOCUSED = 3;
	
	public static final int BITMAP_CHEVRON = 4;
	public static final int BITMAP_CHEVRON_ORANGE = 5;
	
	public static final int BITMAP_DEFUALT_APP_ICON = 6;
	public static final int BITMAP_DOWNLOADING = 7;
	
	public static final int BITMAP_TOOLBAR = 8;
	
	public static final int BITMAP_LOGO1 = 9;
	public static final int BITMAP_LOGO2 = 10;
	
	public static final int BITMAP_THUMBSUP = 11;
	public static final int BITMAP_THUMBSDOWN = 12;
	
	public Res()
	{
		_0star = Bitmap.getBitmapResource("rating0star.png");
    	_1star = Bitmap.getBitmapResource("rating1star.png");
    	_2star = Bitmap.getBitmapResource("rating2star.png");
    	_3star = Bitmap.getBitmapResource("rating3star.png");
    	_4star = Bitmap.getBitmapResource("rating4star.png");
    	_5star = Bitmap.getBitmapResource("rating5star.png");
    	
    	_singleBorder = Bitmap.getBitmapResource("row_single_border_10.png");
		
		_welcomeImage = Bitmap.getBitmapResource("welcome1.png");
		
		_backBtnUnFocused = Bitmap.getBitmapResource("back_button.png");
		_backBtnFocused = Bitmap.getBitmapResource("back_button_focused.png");
		
		_chevron = Bitmap.getBitmapResource("chevron.png");
		_chevronOrange = Bitmap.getBitmapResource("chevron_orange.png");
		
		_defaultAppIcon = Bitmap.getBitmapResource("logo5.png");
    	    	
		_downloading = Bitmap.getBitmapResource("loader2.png");
		
		_toolbar = Bitmap.getBitmapResource("toolbar.png");
		
		_logo1 = Bitmap.getBitmapResource("logo1.png");
		_logo2 = Bitmap.getBitmapResource("logo2.png");
		
		_thumbsup = Bitmap.getBitmapResource("img_thumbsup.png");
		_thumbsdown = Bitmap.getBitmapResource("img_thumbsdn.png");
	}
	
	public static Bitmap getBitmap(int index)
	{
		switch (index)
		{
			case BITMAP_SINGLE_BORDER:
				return _singleBorder;
			case BITMAP_WELCOME:
				return _welcomeImage;
			case BITMAP_BACK_UNFOCUSED:
				return _backBtnUnFocused;
			case BITMAP_BACK_FOCUSED:
				return _backBtnFocused;
			case BITMAP_CHEVRON:
				return _chevron;
			case BITMAP_CHEVRON_ORANGE:
				return _chevronOrange;
			case BITMAP_DEFUALT_APP_ICON:
				return _defaultAppIcon;
			case BITMAP_DOWNLOADING:
				return _downloading;
			case BITMAP_TOOLBAR:
				return _toolbar;
			case BITMAP_LOGO1:
				return _logo1;
			case BITMAP_LOGO2:
				return _logo2;
			case BITMAP_THUMBSUP:
				return _thumbsup;
			case BITMAP_THUMBSDOWN:
				return _thumbsdown;				
			default:
				return null;
		}
	}
	
    public static Bitmap getRatingBitmap(int rating)
    {
    	switch(rating)
    	{
	    	case 0:
	    	default:
	    		return _0star;
	    	case 1:
	    		return _1star;
	    	case 2:
	    		return _2star;
	    	case 3:
	    		return _3star;
	    	case 4:
	    		return _4star;
	    	case 5:
	    		return _5star;
    	}
    }
    
    
}
