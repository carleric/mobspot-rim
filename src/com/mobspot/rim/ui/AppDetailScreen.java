package com.mobspot.rim.ui;

import java.util.Vector;

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;

import com.blackberry.util.log.Logger;
import com.mobspot.rim.App;
import com.mobspot.rim.Mobspot;
import com.mobspot.rim.network.Connection;
import com.mobspot.rim.network.ConnectionListener;
import com.mobspot.rim.network.NetworkConstants;
import com.mobspot.rim.network.Request;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.extension.component.PictureScrollField;
import net.rim.device.api.ui.extension.component.PictureScrollField.HighlightStyle;
import net.rim.device.api.ui.extension.component.PictureScrollField.ScrollEntry;
//import net.rim.device.api.ui.extension.component.PictureScrollField;

public class AppDetailScreen extends MobspotScreen implements FieldChangeListener, ConnectionListener
{
	
	
	//RoundedManagers
	private RoundedManager _topRound;
	private RoundedManager _ratingRound;
	
	private RoundedManager _descriptionRound;
	
	//RoundedLists
	private RoundedList _downloadList;
	//private RoundedList _developerList;
	private RoundedList _reviewsList;
	private RoundedList _screenshotsList;
	
	//LabelFields	
	private MobspotField _platformField;
	private MobspotField _releaseDateField;
	private MobspotField _versionField;
	private MobspotField _priceField;
	private MobspotField _descriptionField;
	private MobspotField _developerField;	
	
	//BitmapFields
	private BitmapField _logoField;
	private BitmapField _ratingThisVersionField;
	private BitmapField _ratingOverallField;
	
	private NumericChoiceField _myRatingField;
		
	//button identifiers
	private static final int ITEM_DOWNLOAD = 0;
	private static final int ITEM_DEVELOPER = 1;
	private static final int ITEM_REVIEWS = 2;
	private static final int ITEM_SCREENSHOTS = 3;
	
	private App _app;
	
	private Vector _downloadedScreenshots;
	
	private Connection _connection;
	private String _url;
	
	protected Logger log = Logger.getLogger(getClass());
	
	public AppDetailScreen(App app)
	{
		super();
		
		_app = app;
		
		setTitle(_app.getName());
		
		_downloadedScreenshots = new Vector();
		
		_connection = new Connection("CONN_APPDETAIL ["+_app.getName()+"]");
		_connection.addListener(this);
		_connection.start();
		
		//load it straight away if data is already in memory
		if(_app.hasDetail())
			loadApp();
		
		//if it doesn't have a proper id, then
		//it was scanned from the device
		//try to download detail from server
		//by app name
		else if(_app.getMobappId() == -1)
		{
			_mainManager.setShowLoader(true);
			_mainManager.setStatusText("Loading App Detail");
			log.debug("loading app detail by name");
			
			try
			{
				_url = NetworkConstants.URL_APP_DETAIL_BY_NAME+"?name="+_app.getName()+"&fbuid="+_mobspot.fbc.getLoggedInUser().getId()+
					"&platform="+Mobspot.PLATFORM_ID;
				_url = URI.create(_url).toString();
				_connection.addRequest(new Request(_url));
				_connection.doRequests();
			}
			catch(Exception e)
			{
				log.error("error in AppDetail loading of detail: "+e.getMessage());
			}
		}
		//else, it does have a proper id
		//download detail from server by mobappversionid
		else
		{
			_mainManager.setShowLoader(true);
			_mainManager.setStatusText("Loading App Detail");
			log.debug("loading app detail by id");
			
			try
			{
				//_connection.fetch(URL_APP_DETAIL+_app.getId());
				_url = NetworkConstants.URL_APP_DETAIL_BY_MOBAPP_VERSION_ID+_app.getMobappVersionId()+"&fbuid="+_mobspot.fbc.getLoggedInUser().getId();
				_connection.addRequest(new Request(_url));
				_connection.doRequests();
			}
			catch(Exception e)
			{
				log.error("error in AppDetail loading of detail: "+e.getMessage());
			}
		}
	}
	
	public boolean onClose()
	{
		log.debug("AppDetailScreen for title="+_app.getName()+" closing");
		if(_connection != null)
		{
			_connection.stop();
			_connection = null;
		}
		//stopBitmapThread();
		close();
		return true;
	}
	
	public int getPreferredHeight()
	{
		return Display.getHeight();
	}

	public void fieldChanged(Field field, int context) 
	{
		if(field instanceof RoundedListItem)
		{
			RoundedListItem item = ((RoundedListItem)field);
			switch(item.getItemId())
			{
				case ITEM_DOWNLOAD:
					Browser.getDefaultSession().displayPage(_app.getDownloadURL());
					break;
				case ITEM_DEVELOPER:
					break;
				case ITEM_REVIEWS:
					fireAction(UIConstants.ACTION_APP_REVIEWS, _app);
					break;
				case ITEM_SCREENSHOTS:
					fireAction(UIConstants.ACTION_APP_SCREENSHOTS, _app);
					break;
			}
		}
		else if(field instanceof NumericChoiceField && context == ChoiceField.CONTEXT_CHANGE_OPTION)
		{
			NumericChoiceField myRating = (NumericChoiceField)field;
			log.debug("my rating field value changed to " + myRating.getSelectedValue()+" context="+context);
			_app.setMyRating(myRating.getSelectedValue());
			try
			{
				if(_app.getMobappId() != -1)
				{
					_connection.addRequest(new Request(NetworkConstants.URL_RATE_APP+"?mobapp_version_id="+_app.getMobappVersionId()+"&fbuid="+_mobspot.fbc.getLoggedInUser().getId()+"&rating="+myRating.getSelectedValue()));
					_connection.doRequests();
				}
				else
				{
					//TODO: fix server side function, currently not returning valid id
					
					/*String url = URI.create(URL_ADD_APP+"?developer_name="+_app.getDeveloper()+
							"&name="+_app.getName()+"&description="+_app.getDescription()+
							"&version="+_app.getVersion()+"&platform="+Mobspot.PLATFORM_ID).toString();
					Request r = Connection.doRequest(new Request(url));
					if(r.getResponse().getResponseCode() == 200)
					{
						JSONObject jsonResponse = new JSONObject(new JSONTokener(r.getResponse().getData().toString()));
						int mobappVersionId = jsonResponse.getInt("mobapp_version_id");
						_app.setMobappVersionId(mobappVersionId);
						Connection.doRequest(new Request(URL_RATE_APP+"?mobapp_version_id="+_app.getMobappVersionId()+"&fbuid="+_fbc.getLoggedInUser().getId()+"&rating="+myRating.getSelectedValue()));
					}*/
				}
			}
			catch(Throwable t)
			{
				log.error(t.getMessage());
			}
		}
	}
	
	private void parseApp(Request r)
	{
		//read JSON from response
    	try
    	{
    		JSONObject json = new JSONObject(new JSONTokener(r.getResponse().getData().toString()));
    		JSONArray apps = json.getJSONArray("mobapps");
    		
    		if(apps.length() > 0)
    		{
				//_app = new App(apps.getJSONObject(0));
				//_mobspot.getAppCache().updateAppWithSameName(_app);
    			_app.updateWithDetail(apps.getJSONObject(0));
    		}
    		else 
    		{
    			_app.setSavedInMobspot(false);
    			log.debug("No app detail for mobapp_version_id="+_app.getMobappVersionId());
    		}
    	}
    	catch(Exception e)
    	{
    		log.debug("exception reading response in AppDetailScreen: "+e.getMessage());
    	}
	}
	
	private void loadApp()
	{
		//1: top logo and details
		_topRound = new RoundedManager();
		_logoField = new BitmapField();
		_logoField.setMargin(UIConstants.MARGIN, UIConstants.MARGIN, UIConstants.MARGIN, UIConstants.MARGIN);
		if(_app.hasIcon())
			_logoField.setBitmap(_app.getIcon());
		else
			_logoField.setBitmap(Res.getBitmap(Res.BITMAP_DEFUALT_APP_ICON));
		
		_platformField = new MobspotField();
		_platformField.setText("Platform: Blackberry");
		
		if(_app.hasDetail())
		{
			_releaseDateField = new MobspotField();
			_priceField = new MobspotField();
			_releaseDateField.setText("Release Date: "+_app.getReleaseDate());
			_priceField.setText("Price: "+ _app.getPrice());
		}
		_versionField = new MobspotField();
		_versionField.setText("Version: "+_app.getVersion());
		
		_developerField = new MobspotField();
		_developerField.setText("By: "+_app.getDeveloper());
		
		MobspotField categoriesField = new MobspotField();
		categoriesField.setText("Categories: "+_app.getCategories());
		
		
		HorizontalFieldManager hm1 = new HorizontalFieldManager();
		if(_app.hasDetail())
			hm1.add(_logoField);
		VerticalFieldManager vm1 = new VerticalFieldManager();
		vm1.setMargin(UIConstants.PADDING_TOP, UIConstants.PADDING_RIGHT, UIConstants.PADDING_BOTTOM, UIConstants.PADDING_LEFT);
		vm1.add(_platformField);
		if(_app.hasDetail())
			vm1.add(_releaseDateField);
		vm1.add(_versionField);
		if(_app.hasDetail())
			vm1.add(_priceField);
		vm1.add(_developerField);
		if(_app.hasDetail())
			vm1.add(categoriesField);
		
		hm1.add(vm1);
		_topRound.add(hm1);
		_mainManager.add(_topRound);
		
		//2: description		
		_descriptionField = new MobspotField();
		_descriptionField.setPadding(new XYEdges(UIConstants.PADDING, UIConstants.PADDING, UIConstants.PADDING, UIConstants.PADDING));
		_descriptionRound = new RoundedManager();
		_descriptionRound.add(_descriptionField);
		_descriptionField.setText(_app.getDescription());
		MobspotField l3 = new MobspotField("Description");
		_mainManager.add(l3);
		_mainManager.add(_descriptionRound);
		
		//3: download link
		if(_app.hasDetail())
		{
			_downloadList = new RoundedList(this);
			_downloadList.addItem(new RoundedListItem("Download "+_app.getName(), ITEM_DOWNLOAD));
			_mainManager.add(_downloadList);
		}
	
		//4: ratings
		if(_app.hasDetail())
		{
			XYEdges ratingMargin = new XYEdges(4,4,4,4);
			MobspotField ratingLabel1 = new MobspotField("(Overall)", Mobspot.COLOR_TEXT, DrawStyle.LEFT);
			MobspotField ratingLabel2 = new MobspotField("(This Version)", Mobspot.COLOR_TEXT, DrawStyle.LEFT);
			ratingLabel1.setMargin(ratingMargin);
			ratingLabel2.setMargin(ratingMargin);
			_ratingThisVersionField = new BitmapField();
			_ratingThisVersionField.setMargin(ratingMargin);
			_ratingOverallField = new BitmapField();
			_ratingOverallField.setMargin(ratingMargin);
			_ratingRound = new RoundedManager(32);
			
			HorizontalFieldManager h1 = new HorizontalFieldManager();
			h1.add(_ratingOverallField);
			h1.add(ratingLabel1);
			_ratingRound.add(h1);
			
			HorizontalFieldManager h2 = new HorizontalFieldManager();
			h2.add(_ratingThisVersionField);
			h2.add(ratingLabel2);
			_ratingRound.add(h2);			
			
			_myRatingField = new NumericChoiceField("My Rating", 0, 5, 1);
			_myRatingField.setFont(Mobspot.FONT_MEDIUM);
			_myRatingField.setChangeListener(this);
			_ratingRound.add(_myRatingField);
			
			_ratingThisVersionField.setBitmap(Res.getRatingBitmap(_app.getRatingThisVersionInt()));
			_ratingOverallField.setBitmap(Res.getRatingBitmap(_app.getRatingOverallInt()));
			_myRatingField.setSelectedValue(_app.getMyRating());
			
			MobspotField l1 = new MobspotField("Ratings");
			_mainManager.add(l1);
			_mainManager.add(_ratingRound);
		}
		
		//5: screenshots
		if(_app.hasDetail())
		{
			_screenshotsList = new RoundedList(this);
			_screenshotsList.addItem("Screenshots ("+_app.getScreenShotURLs().size()+")", ITEM_SCREENSHOTS);
			_mainManager.add(_screenshotsList);
		}
		
		//6: developer
		/*_developerList = new RoundedList(this);
		MobspotField l4 = new MobspotField("Developed By"); 
		_mainManager.add(l4);
		_mainManager.add(_developerList);	
		_developerList.addItem(new RoundedListItem(_app.getDeveloper(), ITEM_DEVELOPER));*/
		
		//7: reviews
		if(_app.hasDetail())
		{
			_reviewsList = new RoundedList(this);
			_reviewsList.addItem("Reviews ("+_app.getReviews().size()+")", ITEM_REVIEWS);
			_mainManager.add(_reviewsList);
		}	
		
	}
	

	
	
	public void onResponse(final Request r) 
	{
    	_mobspot.invokeLater(new Runnable() 
        {
            public void run()
            {
            	if(r.getURL().equals(_url))
            	{	
            		log.debug("AppDetailScreen got response="+r.getResponse().getData().toString());
            		parseApp(r);
			        loadApp();
			        _mainManager.setShowLoader(false);
            	}
            	/*else if(r.getURL().equals(_app.getLogoURL()))
            	{
            		log.debug("got app detail logo, setting bitmap field");
            		Bitmap b = (Bitmap)r.getResponse().getData();
            		_logoField.setBitmap(b);
            	}
            	else
            	{
            		log.debug("got app detail screenshot, adding to picture scroller");
            		_downloadedScreenshots.addElement(r.getResponse().getData());
            		//updatePictureScroller();
            	}*/
            }
        });
	}
	
	
	
}