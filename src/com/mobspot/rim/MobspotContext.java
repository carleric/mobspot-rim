package com.mobspot.rim;

import java.util.Vector;

import me.regexp.RE;

import org.json.me.JSONArray;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;

import com.blackberry.facebook.FacebookContext;
import com.blackberry.facebook.User;
import com.blackberry.util.log.Logger;
import com.mobspot.rim.network.Connection;
import com.mobspot.rim.network.Request;

public class MobspotContext
{
	private static final String URL_ADD_OR_UPDATE_USER = Mobspot.URL_SERVER_ROOT+"remote_client/add_or_update_user/?";
	private static final String URL_HIDDEN_NAMES = Mobspot.URL_SERVER_ROOT+"remote_client/hidden_names";
	
	private boolean _haveUserInMobspot = false;
	private int _mobspotAccountId;
	
	private Vector _hiddenAppNames;
	
	protected Logger log = Logger.getLogger(getClass());
	
	public MobspotContext()
	{
		_hiddenAppNames = new Vector();
	}
	
	public boolean addOrUpdateUser(FacebookContext fbc)
	{
		if(haveUserInMobspot())
			return true;
		boolean success = false;
		try
		{
			//_connection.run();
			User user = fbc.getLoggedInUser();
			Request r = Connection.doRequest(new Request(URL_ADD_OR_UPDATE_USER+"fbuid="+user.getId()+
					"&email="+user.getEmail()+"&first_name="+user.getFirstName()+
					"&last_name="+user.getLastName()));
			
			if(r.getResponse().getResponseCode() == 200)
			{
				try
		    	{
		    		JSONObject json = new JSONObject(new JSONTokener(r.getResponse().getData().toString()));
		    		String idConfirmed = json.getString("fbuid");
		    		if(idConfirmed.equals(fbc.getLoggedInUser().getId()))
		    		{
		    			log.info("Confirmed user "+idConfirmed+" exists in Mobspot, ok to proceed with user specific queries");
		    			_mobspotAccountId = json.getInt("id");
		    			_haveUserInMobspot = true;
		    		}
		    	}
		    	catch(Exception e)
		    	{
		    		log.debug("exception reading response in MobspotContext: "+e.getMessage());
		    	}
		    	success = true;
			}
		}
		catch(Throwable t)
		{
			log.error(t.getMessage());
		}
		finally{
			return success;
		}
	}
	
	public void updateHiddenAppNames()
	{
		try
		{
			Request r = Connection.doRequest(new Request(URL_HIDDEN_NAMES));
			
			if(r.getResponse().getResponseCode() == 200)
			{
				try
		    	{
		    		JSONObject json = new JSONObject(new JSONTokener(r.getResponse().getData().toString()));
		    		JSONArray hidden_names = json.getJSONArray("hidden_names");
		    		//add items to list
		    		for(int i=0; i<hidden_names.length(); i++)
		    		{
		    			log.debug("adding hidden_name="+hidden_names.getString(i));
		    			_hiddenAppNames.addElement(hidden_names.getString(i));
		    		}
		    	}
		    	catch(Exception e)
		    	{
		    		log.debug("exception reading response in MobspotContext: "+e.getMessage());
		    	}
			}
		}
		catch(Throwable t)
		{
			log.error(t.getMessage());
		}
	}

	public boolean isAppNameHidden(String name)
	{
		boolean isHidden = false;
		for(int i=0; i<_hiddenAppNames.size(); i++)
		{
			RE r = new RE((String)_hiddenAppNames.elementAt(i));
			
			if(r.match(name))
			{
				isHidden = true;
				break;
			}
		}
		return isHidden;
	}
	
	public boolean haveUserInMobspot()
	{
		return _haveUserInMobspot;
	}
}
