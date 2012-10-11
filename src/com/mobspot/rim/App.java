package com.mobspot.rim;

import java.util.Vector;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.blackberry.util.log.Logger;
import com.mobspot.rim.network.Connection;
import com.mobspot.rim.ui.UIConstants;

import net.rim.device.api.system.Bitmap;

import me.regexp.RE;

public class App
{
	private int _mobapp_id;
	private int _mobapp_version_id;
	private String _name;
	private String _description;
	private double _rating_thisversion = 0;//default to 0 (unrated)
	private double _rating_overall = 0;
	private double _price;
	private String _releaseDate;
	private String _version;
	private String _developer;
	private String _downloadURL;
	private Vector _categories;
	private Vector _modules;
	private boolean _isHidden;
	private Bitmap _icon;
	private boolean _featured;
	private String _logoURL;
	private Vector _screenShotURLs = new Vector();
	private boolean _hasIconOnServer = true;//assume true until first fetch fails
	private boolean _scannedFromDevice = false;
	private boolean _savedInMobspot = false;
	private int _myRating = 0;
	private boolean _ratable = false;
	private Vector _reviews;
	
	
	private static final String URL_IMAGE_PREFIX = "http://";
	
	protected Logger log = Logger.getLogger(getClass());
	
	public App(JSONObject app)
	{
		updateWithDetail(app);
	}
	
	public App(
			int mobapp_id, 
			int mobapp_version_id, 
			boolean scanned,
			String  name, 
			String description, 
			String developer,
			String version,
			Vector modules
			)
	{
		_mobapp_id = mobapp_id;
		_mobapp_version_id = mobapp_id;
		_scannedFromDevice = scanned;
		_name = name;
		_description = description;
		_developer = developer;
		_version = version;
		_modules = modules;
		_categories = new Vector();
		
		//setIsHidden();
	}
	
	public void updateWithDetail(JSONObject app)
	{
		try
		{
			_mobapp_id = app.getInt("mobapp_id");
			_mobapp_version_id = app.getInt("mobapp_version_id");
			_name = app.getString("name");
			_description = app.getString("description");
			_rating_thisversion = app.getDouble("rating_thisversion");
			_rating_overall = app.getDouble("rating_overall");
			_price = app.getDouble("price");
			_featured = app.getBoolean("featured");
			
			for(int i=0; i<UIConstants.SCREENSHOT_INDICES.length; i++)
			{
				String sUrl = app.getString("screen_shot_"+UIConstants.SCREENSHOT_INDICES[i]+"_path");
				if(Connection.isValidURL(sUrl))
				{
					_screenShotURLs.addElement(URL_IMAGE_PREFIX+sUrl);
				}
			}
			
			_downloadURL = app.getString("download_url");
			_logoURL = app.getString("logo_path");
			_releaseDate = app.getString("release_date");
			_version = app.getString("version");
			_developer = app.getString("developer_name");
			_myRating = app.getInt("my_rating");
			
			_reviews = new Vector();
			JSONArray jsonReviews = app.getJSONArray("reviews");
			for(int i=0; i<jsonReviews.length(); i++)
			{
				_reviews.addElement(new Review(jsonReviews.getJSONObject(i)));
			}
			
			_categories = new Vector();
			JSONArray jsonCategories = app.getJSONArray("categories");
			for(int i=0; i<jsonCategories.length(); i++)
			{
				JSONObject obj = jsonCategories.getJSONObject(i);
				_categories.addElement(new Category(obj.getInt("id"), obj.getString("name")));
			}
			
			//setIsHidden();
		}
		catch(JSONException e)
		{
			//silently fail, assume that some queries will only get the first few attributes
			log.error(e.getMessage());
		}
	}
	
	public void setMobappId(int id)
	{
		_mobapp_id = id;
	}
	
	public int getMobappId()
	{ 
		return _mobapp_id;
	}
	
	public int getMobappVersionId()
	{ 
		return _mobapp_version_id;
	}
	

	public String getName() {
		return _name;
	}

	public String getDescription() {
		return _description;
	}

	public double getRatingThisVersion() {
		return _rating_thisversion;
	}
	
	public int getRatingThisVersionInt() {
		
		return (int)_rating_thisversion;
	}
	
	public double getRatingOverall() {
		return _rating_overall;
	}
	
	public int getRatingOverallInt() {
		
		return (int)_rating_overall;
	}

	public void setModules(Vector _modules) {
		this._modules = _modules;
	}

	public Vector getModules() {
		return _modules;
	}
	
	public boolean hasModules()
	{
		return _modules != null;
	}
	
	private void setIsHidden(boolean hidden)
	{
		_isHidden = hidden;
		
		/*
		for(int i=0; i<App._hiddenNames.size(); i++)
		{
			RE r = new RE((String)App._hiddenNames.elementAt(i));
			
			//check app title/name
			if(r.match(this.getName()))
			{
				_isHidden = true;
				return;
			}
			
			//check app modules
			//for(int j=0; j<this.getModules().size(); j++)
			//{
				//if(r.match(this.getModules().elementAt(j).toString()))
				//{
					//_isHidden = true;
					//return;
				//}
			//}
		}
		_isHidden = false;*/
	}
	
	public boolean isHidden()
	{
		return _isHidden;
	}
	
	public void setIcon(Bitmap b)
	{
		_icon = b;
	}
	
	public Bitmap getIcon()
	{
		return _icon;
	}
	
	public boolean hasIcon()
	{
		return _icon != null;
	}
	
	public boolean hasIconOnServer()
	{
		return _hasIconOnServer;
	}
	
	public void setHasIconOnServer(boolean hasIcon)
	{
		_hasIconOnServer = hasIcon;
	}

	public String getPrice() 
	{
		return _price == 0 ? "Free" : "US$"+_price;
	}
	
	public String getVersion()
	{
		return _version;
	}
	
	public void setVersion(String version)
	{
		_version = version;
	}
	
	public String getReleaseDate()
	{
		return _releaseDate;
	}
	
	public String getDeveloper()
	{
		return _developer;
	}
	
	public void setDeveloper(String developer)
	{
		_developer = developer;
	}
	
	public String getDownloadURL()
	{
		return _downloadURL;
	}
	
	public String getLogoURL()
	{
		return URL_IMAGE_PREFIX+_logoURL;
	}
	
	public Vector getScreenShotURLs()
	{
		return _screenShotURLs;
	}
	
	public boolean hasDetail()
	{
		return _logoURL != null;
	}

	public void setScannedFromDevice(boolean _scannedFromDevice) {
		this._scannedFromDevice = _scannedFromDevice;
	}

	public boolean isScannedFromDevice() {
		return _scannedFromDevice;
	}

	public void setMyRating(int _myRating) {
		this._myRating = _myRating;
	}

	public int getMyRating() {
		return _myRating;
	}

	public void setSavedInMobspot(boolean _savedInMobspot) {
		this._savedInMobspot = _savedInMobspot;
	}

	public boolean isSavedInMobspot() {
		return _savedInMobspot;
	}

	public void setRatable(boolean _ratable) {
		this._ratable = _ratable;
	}

	public boolean isRatable() {
		return _ratable;
	}
	
	public Vector getReviews()
	{
		return _reviews;
	}
	
	public String getCategories()
	{
		StringBuffer cats = new StringBuffer();
		for(int i=0; i<_categories.size(); i++)
		{
			cats.append(((Category)_categories.elementAt(i)).getName() + ", ");
		}
		if(cats.length() > 3)
		{
			cats.deleteCharAt(cats.length()-1);
			cats.deleteCharAt(cats.length()-1);
		}
		return cats.toString();
	}
	
}
