package com.mobspot.rim.network;

import com.mobspot.rim.Mobspot;

public class NetworkConstants 
{
	public static final String URL_APP_DETAIL_BY_MOBAPP_VERSION_ID = Mobspot.URL_SERVER_ROOT+"remote_client/app_detail_by_mobapp_version_id/?platform="+Mobspot.PLATFORM_ID+"&mobapp_version_id=";
	public static final String URL_APP_DETAIL_BY_NAME = Mobspot.URL_SERVER_ROOT+"remote_client/app_detail_by_name/";
	public static final String URL_ADD_APP = Mobspot.URL_SERVER_ROOT+"remote_client/add_app/";
	public static final String URL_RATE_APP = Mobspot.URL_SERVER_ROOT+"remote_client/rate_app/";
	
	public static final String URL_FEATURED_APPS = Mobspot.URL_SERVER_ROOT+"remote_client/featured_apps/?platform="+Mobspot.PLATFORM_ID;
    public static final String URL_APPS_BY_CATEGORY = Mobspot.URL_SERVER_ROOT+"remote_client/apps_by_cat/?platform="+Mobspot.PLATFORM_ID+"&category=";
    public static final String URL_FRIENDS_APPS = Mobspot.URL_SERVER_ROOT+"remote_client/friends_apps/?platform="+Mobspot.PLATFORM_ID+"&fbuid=";
    
    public static final String URL_CATEGORIES = Mobspot.URL_SERVER_ROOT+"remote_client/category_appcnt/?platform="+Mobspot.PLATFORM_ID;
    
}
