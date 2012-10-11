package com.mobspot.rim.ui;

import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

import com.mobspot.rim.App;
import com.mobspot.rim.Mobspot;
import com.mobspot.rim.Review;

public class ReviewsScreen extends MobspotScreen 
{
	
	public ReviewsScreen()
	{
		
	}
	
	public void showReviews(App app)
	{
		setTitle("Reviews for "+app.getName());
		
		for(int i=0; i<app.getReviews().size(); i++)
		{
			Review review = (Review)app.getReviews().elementAt(i);
			
			HorizontalFieldManager h1 = new HorizontalFieldManager();
			MobspotField dateField = new MobspotField("("+review.getDate()+")", Mobspot.COLOR_TEXT, Mobspot.FONT_SMALL);
			MobspotField authorField = new MobspotField(review.getAuthor()+" says...");
			h1.add(authorField);
			h1.add(dateField);
			h1.setMargin(new XYEdges(0, 0, 0, 0));
			_mainManager.add(h1);
			
			RoundedManager r = new RoundedManager();
			r.add(new MobspotField(review.getSubject()));
			MobspotField bodyField = new MobspotField(review.getBody(), Mobspot.COLOR_TEXT, Mobspot.FONT_SMALL);
			r.add(bodyField);
			_mainManager.add(r);
			
			HorizontalFieldManager h2 = new HorizontalFieldManager();
			MobspotField upVotes = new MobspotField("Helpful: "+review.getUpVotes(), Mobspot.COLOR_TEXT, Mobspot.FONT_SMALL);
			h2.add(upVotes);
			BitmapField thumbsup = new BitmapField(Res.getBitmap(Res.BITMAP_THUMBSUP));
			h2.add(thumbsup);
			MobspotField downVotes = new MobspotField(""+review.getDownVotes(), Mobspot.COLOR_TEXT, Mobspot.FONT_SMALL);
			h2.add(downVotes);
			BitmapField thumbsdown = new BitmapField(Res.getBitmap(Res.BITMAP_THUMBSDOWN));
			h2.add(thumbsdown);
			r.add(h2);
			
		}
	}
}
