package com.mobspot.rim.ui;

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;

import com.blackberry.util.log.Logger;
import com.mobspot.rim.Category;
import com.mobspot.rim.Mobspot;
import com.mobspot.rim.network.Connection;
import com.mobspot.rim.network.ConnectionListener;
import com.mobspot.rim.network.NetworkConstants;
import com.mobspot.rim.network.Request;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

public class CategoriesScreen extends MobspotScreen implements ConnectionListener, FieldChangeListener
{

	private RoundedList _list;
	private Connection _connection;
	
	
    protected Logger log = Logger.getLogger(getClass());
    
    public CategoriesScreen()
    {
    	super();
		
		setTitle("Categories");
		
		_list = new RoundedList(this);
    	_mainManager.add(_list);
		
		// Start the helper threads.
        _connection = new Connection("CONN_CATEGORIES");
		_connection.addListener(this);
		_connection.start();
    }
    
    public void fetchCategories()
    {
    	_list.deleteAll();
        //_connection.fetch(CATEGORIES_URL);
    	_connection.addRequest(new Request(NetworkConstants.URL_CATEGORIES));
    	_connection.doRequests();
        _mainManager.setShowLoader(true);
        _mainManager.setStatusText("Loading Categories");
    }
    
    public void close()
    {
    	log.debug("CategoriesScreen.close, stopping connection, closing superclass");
        _connection.stop();
        super.close();
    } 

	public void fieldChanged(Field field, int context) 
	{
		log.debug("CategoriesScreen category_id="+((RoundedListItem)field).getItemId());
		fireAction(UIConstants.ACTION_APPS_BY_CATEGORY, new Integer(((RoundedListItem)field).getItemId()));
	}
	
	public String getCategoryName(int categoryId)
	{
		String name = null;
		for(int i=0; i<_list.getFieldCount(); i++)
		{
			Category cat = ((RoundedListItem)_list.getField(i)).getCategory();
			if(cat.getId() == categoryId)
				name = cat.getName();
		}
		return name;
	}
	
	public void onResponse(Request r) 
	{
		final String responseText = r.getResponse().getData().toString();
		log.debug("CategoriesScreen got response="+responseText);
    	
    	_mobspot.invokeLater(new Runnable() 
        {
            public void run()
            {
		        //read JSON from response
		    	try
		    	{
		    		JSONObject json = new JSONObject(new JSONTokener(responseText));
		    		JSONArray cats = json.getJSONArray("catcnt");
		    		//add items to list
		    		for(int i=0; i<cats.length(); i++)
		    		{
		    			JSONObject cat = new JSONObject(new JSONTokener(cats.getString(i)));
		    			_list.addItem(new Category(cat.getInt("id"), cat.getString("name"), Integer.parseInt(cat.getString("appcount"))));
		    		}
		    		_mainManager.setShowLoader(false);
		    	}
		    	catch(Exception e)
		    	{
		    		log.debug("exception reading response in CategoriesScreen: "+e.getMessage());
		    	}
            }
        });
	}

}
