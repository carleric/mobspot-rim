package com.mobspot.rim.ui;

import com.mobspot.rim.Mobspot;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

public class Utility {
	
	public static final int FLAG_NO_CORNERS = 0;
	public static final int FLAG_TOP_LEFT = 1;
	public static final int FLAG_TOP_RIGHT = 2;
	public static final int FLAG_BOTTOM_RIGHT = 4;
	public static final int FLAG_BOTTOM_LEFT = 8;
	
	private static final int COLOR_TRANSPARENT = 0x00ffffff;
	
	private static final byte[] PATH_POINT_TYPES_RECT = {
        Graphics.CURVEDPATH_END_POINT, 
        Graphics.CURVEDPATH_END_POINT, 
        Graphics.CURVEDPATH_END_POINT, 
        Graphics.CURVEDPATH_END_POINT
      };
	
    private static final int[] PATH_GRADIENT_TRANS_TO_BG = {
    	COLOR_TRANSPARENT, Mobspot.COLOR_BACKGROUND, 
    	Mobspot.COLOR_BACKGROUND, COLOR_TRANSPARENT
      };
    
    private static final int GRADIENT_WIDTH = 20;
	
	public static String truncateText(String text, int targetWidth, Font font, boolean ellipses)
	{
		int textWidth = font.getAdvance(text);
		if(textWidth > targetWidth)
		{
    		for(int i=text.length()-1; textWidth > targetWidth; i--)
    		{
    			text = text.substring(0, i);
    			textWidth = font.getAdvance(text+"...");
    		}
    		if(ellipses)
    			text += "...";
		}
		return text;
	}
	
	public static void drawTextToGradient(Graphics g, String text, Font font, int fontColor, int x, int y, int width, int gradientColor)
	{
		//g.setColor(fontColor);
		//g.setFont(font);
		//g.drawText(text, x, y, Graphics.LEFT, width);
		
		Bitmap bmp = new Bitmap(width, font.getHeight());
		Graphics tmp = Graphics.create(bmp);
		tmp.setGlobalAlpha(0);
		/*tmp.setColor(fontColor);
		tmp.setFont(font);
		tmp.drawText(text, 0, 0, Graphics.LEFT, width);*/
		
		
		g.drawBitmap(new XYRect(x, y, width, font.getHeight()), bmp, 0, 0);
		
		
		//int [] xPts = {width - GRADIENT_WIDTH, width, width, width - GRADIENT_WIDTH};
        //int [] yPts = {0, 0, font.getHeight(), font.getHeight() };
        //g.drawShadedFilledPath(xPts, yPts, PATH_POINT_TYPES_RECT, PATH_GRADIENT_TRANS_TO_BG, null);
  
	}
	
	public static void paintRoundedGradientBlackBorder(Graphics g, int x, int y, int width, int height, int startColor, int endColor, int cornerFlags)
	{
		int [] imgData = new int[width * height];
		
		int alpha = (startColor >> 24) & 0xff;
		int red   = (startColor >> 16) & 0xff;
        int green = (startColor >> 8) & 0xff;
        int blue  = (startColor >> 0) & 0xff;
        
        int endred   = (endColor >> 16) & 0xff;
        int endgreen = (endColor >> 8) & 0xff;
        int endblue  = (endColor >> 0) & 0xff;

        double rdiff = (double)(endred - red) / height;
        double gdiff = (double)(endgreen - green) / height;
        double bdiff = (double)(endblue - blue) / height;
        
        double dr = red;
        double dg = green;
        double db = blue;
        
        //Logger.getLogger("RoundedGradient").debug("paintRoundedGradientBlackBorder x="+x+" y="+y+" width="+width+" height="+height+" startColor="+startColor+" endColor="+endColor+" rdiff="+rdiff+" gdiff="+gdiff+" bdiff="+bdiff+" cornerFlags="+cornerFlags);
        
		for(int i=0; i<imgData.length; i++)
		{
			int row = i/width;
			int col = i - (row*width);
			
			if(col == 0 && row != 0)
			{
				dr   += rdiff;
				dg += gdiff;
				db  += bdiff;
				if(dr - ((int)dr) >= .5)
					red = (int)(Math.ceil(dr));
				if(dg - ((int)dg) >= .5)
					green = (int)(Math.ceil(dg));
				if(db - ((int)db) >= .5)
					blue = (int)(Math.ceil(db));
				//Logger.getLogger("RoundedGradient").debug("rgb changed r="+red+" g="+green+" b=" +blue);
			}
			
			if(row == 0 || row == height-1 || col == 0 || col == width-1)
			{
				imgData[i] = 0xff000000;
			}
			else
			{
				imgData[i] = (alpha << 24) |(red << 16) | (green << 8) | (blue);
			}
		}
		
		//trim the gradient (roughly, to be cleaned up by overlay of corner images)
		final int RADIUS = 10;
		
		if(cornerFlags != FLAG_NO_CORNERS)
		{
			//trim top left
			if((cornerFlags & FLAG_TOP_LEFT) == FLAG_TOP_LEFT)
			{
				trimCornerPixels(imgData, RADIUS, 0, 1, 0, 1, width, height);
			}
			
			//trim top right
			if((cornerFlags & FLAG_TOP_RIGHT) == FLAG_TOP_RIGHT)
			{
				trimCornerPixels(imgData, RADIUS, 0, 1, width-1, -1, width, height);
			}
			
			//trim bottom right
			if((cornerFlags & FLAG_BOTTOM_RIGHT)== FLAG_BOTTOM_RIGHT)
			{
				trimCornerPixels(imgData, RADIUS, height-1, -1, width-1, -1, width, height);
			}
			
			//trim bottom left
			if((cornerFlags & FLAG_BOTTOM_LEFT) == FLAG_BOTTOM_LEFT)
			{
				trimCornerPixels(imgData, RADIUS, height-1, -1, 0, 1, width, height);				
			}
		}
		
		//construct Bitmap
		Bitmap b = new Bitmap(width, height);
		b.setARGB(imgData, 0, width, 0, 0, width, height);
		
		//draw gradient
		g.drawBitmap(new XYRect(x, y, width, height), b, 0, 0);
		
		if(cornerFlags != FLAG_NO_CORNERS)
		{
			final int BORDER_CROP_SIZE = 10;
			final int BORDER_CROP_OFFSET = 22;
			
			
			//overlay top left
			if((cornerFlags & FLAG_TOP_LEFT) == FLAG_TOP_LEFT)
			{
				//log.debug("drawing TL corner overlay");
				g.drawBitmap(new XYRect(x, y, BORDER_CROP_SIZE, BORDER_CROP_SIZE), Res.getBitmap(Res.BITMAP_SINGLE_BORDER), 0, 0);
				//g.drawRect(x, y, BORDER_CROP_SIZE, BORDER_CROP_SIZE);
			}
			
			//overlay top right
			if((cornerFlags & FLAG_TOP_RIGHT) == FLAG_TOP_RIGHT)
			{
				//log.debug("drawing TR corner overlay");
				g.drawBitmap(new XYRect(width-BORDER_CROP_SIZE+x, y, BORDER_CROP_SIZE, BORDER_CROP_SIZE), Res.getBitmap(Res.BITMAP_SINGLE_BORDER), BORDER_CROP_OFFSET, 0);
				//g.drawRect(width-BORDER_CROP_SIZE+x, y, BORDER_CROP_SIZE, BORDER_CROP_SIZE);
			}
			
			//overlay bottom right
			if((cornerFlags & FLAG_BOTTOM_RIGHT) == FLAG_BOTTOM_RIGHT)
			{
				//log.debug("drawing BR corner overlay");
				g.drawBitmap(new XYRect(width-BORDER_CROP_SIZE+x, height-BORDER_CROP_SIZE+y, BORDER_CROP_SIZE, BORDER_CROP_SIZE), Res.getBitmap(Res.BITMAP_SINGLE_BORDER), BORDER_CROP_OFFSET, BORDER_CROP_OFFSET);
				//g.drawRect(width-BORDER_CROP_SIZE+x, height-BORDER_CROP_SIZE+y, BORDER_CROP_SIZE, BORDER_CROP_SIZE);
			}
			
			//overlay bottom left
			if((cornerFlags & FLAG_BOTTOM_LEFT) == FLAG_BOTTOM_LEFT)
			{
				//log.debug("drawing BL corner overlay");
				g.drawBitmap(new XYRect(x, height-BORDER_CROP_SIZE+y, BORDER_CROP_SIZE, BORDER_CROP_SIZE), Res.getBitmap(Res.BITMAP_SINGLE_BORDER), 0, BORDER_CROP_OFFSET);
				//g.drawRect(x, height-BORDER_CROP_SIZE+y, BORDER_CROP_SIZE, BORDER_CROP_SIZE);
			}
		}
	}
	
	public static void trimCornerPixels(
			int [] imgData, 
			final int radius, 
			final int ystart, 
			final int ydiff, 
			final int xstart, 
			final int xdiff, 
			final int width,
			final int height)
	{
		int yend = (ydiff > 0) ? radius+1 : height - radius -2;
		for(int ypos=ystart, opp = radius; ypos != yend; ypos = ypos+ydiff, opp--)
		{
			double adj = Math.ceil(radius -(Math.sqrt((radius*radius)-(opp*opp))));
			int xend = (xdiff > 0) ? (int)adj : (width - (int)adj-1);
			int xpos = xstart;
			while( xpos != xend )
			{
				setImagePixel(imgData, xpos, ypos, 0, -1, width);
				xpos = xpos + xdiff;
			}
		}
	}
	
	public static void trimCornerPixels(
			int [] imgData,
			final int radius,
			final int width,
			final int height)
	{
		trimCornerPixels(imgData, radius, 0, 1, 0, 1, width, height);
		trimCornerPixels(imgData, radius, 0, 1, width-1, -1, width, height);
		trimCornerPixels(imgData, radius, height-1, -1, width-1, -1, width, height);
		trimCornerPixels(imgData, radius, height-1, -1, 0, 1, width, height);
	}
	

	/**
	 * 
	 * @param imgData
	 * @param x
	 * @param y
	 * @param alpha
	 * @param color: if -1, use the current color in the image
	 * @param width
	 */
	public static void setImagePixel(int [] imgData, int x, int y, int alpha, int color, int width)
	{
		if(color == -1)
			color = imgData[y*width+x];
		imgData[y*width+x] = (alpha << 24) | (((color >> 16) & 0xff) << 16) | (((color >> 8) & 0xff) << 8) | ((color >> 0) & 0xff);
	}
	
	
}
