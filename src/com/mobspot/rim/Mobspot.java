package com.mobspot.rim;

import com.blackberry.facebook.ExtendedPermission;
import com.blackberry.facebook.FacebookContext;
import com.blackberry.facebook.FacebookSettings;

import com.blackberry.facebook.ui.Action;
import com.blackberry.facebook.ui.ActionListener;
import com.blackberry.facebook.ui.LoginScreen;
import com.blackberry.facebook.ui.PermissionScreen;

import com.blackberry.util.log.AppenderFactory;

import com.blackberry.util.network.CookieManager;
import com.blackberry.util.network.HttpConnectionFactory;

import com.mobspot.rim.ui.*;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
//import net.rim.device.api.ui.UiEngineInstance;
import net.rim.device.api.ui.component.Dialog;

public class Mobspot extends UiApplication implements ActionListener
{
	public static final String URL_SERVER_ROOT = "http://dev.mobspot.com:8188/";
	public static final String URL_SERVER_IMAGES = "http://dev-images.mobspot.com/mobapp_logos/";
	
	//public static final String URL_SERVER_ROOT = "http://www.mobspot.com/";
	//public static final String URL_SERVER_IMAGES = "http://images.mobspot.com/mobapp_logos/";
	
	public static final String PLATFORM_ID = "5";
	
	//resources
	private Res res;
	
	//fonts
	public static final Font FONT_SMALL = Font.getDefault().derive(Font.getDefault().getStyle(), 12);
	public static final Font FONT_MEDIUM = Font.getDefault().derive(Font.getDefault().getStyle(), 14);
	public static final Font FONT_LARGE = Font.getDefault().derive(Font.getDefault().getStyle(), 22);
    
	//screens
	private WelcomeScreen _welcomeScreen;
	private HomeScreen _homeScreen;
	private MyAppsScreen _myAppsScreen;
	private AppListScreen _appListScreen;
	private CategoriesScreen _categoriesScreen;
	private AppDetailScreen _appDetailScreen;
	private ReviewsScreen _reviewsScreen;
	private ScreenshotsScreen _screenShotsScreen;
	
	//transitions
	/*private TransitionContext transitionContextSlideInLeft; 
	private TransitionContext transitionContextSlideOutRight;
	private TransitionContext transitionContextSlideDown;
	private TransitionContext transitionContextSlideUp;
	private static final int TRANSITION_DURATION = 300;*/
   
	// screen identifiers
	/*public static final int WELCOME_SCREEN = 0;
	public static final int LOGIN_SCREEN = 1;
	public static final int MY_APPS_SCREEN = 2;
	public static final int FEATURED_APPS_SCREEN = 3;
	public static final int CATEGORIES_SCREEN = 4;
	public static final int APPS_BY_CATEGORY_SCREEN = 5;
	public static final int APP_DETAIL_SCREEN = 6;
	public static final int ERROR_SCREEN = 7;*/
	
	//colors
	public static final int COLOR_DESCRIPTION = 0xffcccccc;//light gray
	public static final int COLOR_TITLE = 0xffffffff;//white
	public static final int COLOR_BACKGROUND = 0xff777777;//grayish
	public static final int COLOR_UNFOCUSED1 = 0xffafafaf;  
	public static final int COLOR_UNFOCUSED2 = 0xff777777;
	public static final int COLOR_FOCUSED1 = 0xffbeeb0e;  
	public static final int COLOR_FOCUSED2 = 0xff7cad09;
	public static final int COLOR_TEXT = 0xffffffff;//white
	
	private AppCache _appCache; 
	
	private final String REST_URL = "http://api.facebook.com/restserver.php"; // As per Facebook.
	private final String GRAPH_URL = "https://graph.facebook.com"; // As per Facebook.
	private final String NEXT_URL = "http://www.facebook.com/connect/login_success.html"; // Your successful URL.
	private final String APPLICATION_KEY = ""; // Your Facebook Application Key. 
	private final String APPLICATION_SECRET = ""; // Your Facebook Application Secret.
	private final String APPLICATION_ID = ""; // Your Facebook Application ID.
	private final long persistentObjectId = 0x8374595399387583L; //

	private PersistentObject store;
	private HttpConnectionFactory connFactory;

	private CookieManager cookieManager = new CookieManager();
	private LoginScreen loginScreen;
	private PermissionScreen permissionScreen;

	public FacebookContext fbc;
	public MobspotContext msc;
	
	public static void main(String[] args)
    {
		Mobspot app = new Mobspot();
        app.enterEventDispatcher();
    }
	
	public Mobspot() 
	{	
		res = new Res();
    	initResources();
    	checkPermissions();
		init();
		
		if ((fbc != null) && fbc.hasSession()) 
		{
			_homeScreen = new HomeScreen();
			_homeScreen.addActionListener(this);
			pushScreen(_homeScreen);

		} else {

			_welcomeScreen = new WelcomeScreen();
			_welcomeScreen.addActionListener(this);
			pushScreen(_welcomeScreen);
		}

	}
	
	public void onAction(final Action event) 
	{
		if(event.getAction() == UIConstants.ACTION_ERROR)
		{
			this.invokeLater(new Runnable(){
				public void run()
				{
					Dialog.alert("Error from " +event.getSource()+ " :"+ event.getData());
				}
			});			
		}
		
		if (event.getSource() == _welcomeScreen) 
		{
			if(event.getAction() == UIConstants.ACTION_LOG_ME_IN)
			{
				try {
					popScreen(_welcomeScreen);
				} catch (IllegalArgumentException e) {
				}

				try {
					loginScreen = new LoginScreen(fbc, cookieManager);
					loginScreen.addActionListener(this);
					loginScreen.login();
					pushScreen(loginScreen);

				} catch (Throwable t) {
					t.printStackTrace();
					Dialog.alert("Error: " + t.getMessage());
				}
			}
		}
		else if (event.getSource() == loginScreen) 
		{
			if (event.getAction().equals(LoginScreen.ACTION_LOGGED_IN)) {
				try {
					popScreen(loginScreen);
				} catch (IllegalArgumentException e) {
				}

				try {
					fbc.getSession((String) event.getData());
					fbc.upgradeSession();

					permissionScreen = new PermissionScreen(fbc, cookieManager);
					permissionScreen.addActionListener(this);
					permissionScreen.requestPermissions(new String[] { 
							ExtendedPermission.OFFLINE_ACCESS, 
							ExtendedPermission.PUBLISH_STREAM,
							ExtendedPermission.EMAIL
					});
					pushScreen(permissionScreen);

				} catch (Throwable t) {
					t.printStackTrace();
					Dialog.alert("Error: " + t.getMessage());
				}

			} else if (event.getAction().equals(LoginScreen.ACTION_ERROR)) {
				Dialog.alert("Error: " + event.getData());
			}

		} else if (event.getSource() == permissionScreen) {
			if (event.getAction().equals(PermissionScreen.ACTION_GRANTED)) {
				try {
					popScreen(permissionScreen);
				} catch (IllegalArgumentException e) {
				}

				
				try {
					if (_homeScreen == null) 
					{
						_homeScreen = new HomeScreen();
						_homeScreen.addActionListener(this);
					}
					pushScreen(_homeScreen);
					//Dialog.inform("Hello " + fbc.getLoggedInUser().getEmail() + "!");

				} catch (Exception e) {
					e.printStackTrace();
					Dialog.alert("Error: " + e.getMessage());
				}
			} else if (event.getAction().equals(PermissionScreen.ACTION_ERROR)) {
				Dialog.alert("Error: " + event.getData());
			}

		} 
		else if(event.getSource() == _homeScreen)
		{
			if(event.getAction() == UIConstants.ACTION_MY_APPS)
			{
				_myAppsScreen = new MyAppsScreen();
				_myAppsScreen.addActionListener(this);
				_myAppsScreen.readInstalledApps();
				pushScreen(_myAppsScreen);
			}
			else if(event.getAction() == UIConstants.ACTION_FEATURED_APPS)
			{
				_appListScreen = new AppListScreen();
				_appListScreen.addActionListener(this);
				_appListScreen.fetchFeaturedApps();
				pushScreen(_appListScreen);
			}
			else if(event.getAction() == UIConstants.ACTION_CATEGORIES)
			{
				_categoriesScreen = new CategoriesScreen();
				_categoriesScreen.addActionListener(this);
				_categoriesScreen.fetchCategories();
				pushScreen(_categoriesScreen);
			}
			else if(event.getAction() == UIConstants.ACTION_FRIENDS_APPS)
			{
				try
				{
					_appListScreen = new AppListScreen();
					_appListScreen.addActionListener(this);
					_appListScreen.fetchFriendsApps(fbc.getLoggedInUser().getId());
					pushScreen(_appListScreen);
				}
				catch(Throwable t)
				{
					//TODO: handle fb failure
				}
			}
			else if(event.getAction() == UIConstants.ACTION_LOGIN_FAILED)
			{
				try {
					popScreen(_homeScreen);
				} catch (IllegalArgumentException e) {
				}
				_welcomeScreen = new WelcomeScreen();
				_welcomeScreen.addActionListener(this);
				pushScreen(_welcomeScreen);
			}
		}
		else if(event.getSource() == _myAppsScreen
				|| event.getSource() == _appListScreen)
		{
			if(event.getAction() == UIConstants.ACTION_APP_DETAIL)
			{
				App app = (App)event.getData();
				_appDetailScreen = new AppDetailScreen(app);
				_appDetailScreen.addActionListener(this);
				pushScreen(_appDetailScreen);
			}
		}
		else if(event.getSource() == _categoriesScreen)
		{
			if(event.getAction() == UIConstants.ACTION_APPS_BY_CATEGORY)
			{
				int cat = ((Integer)event.getData()).intValue();
				_appListScreen = new AppListScreen();
				_appListScreen.addActionListener(this);
				_appListScreen.fetchAppsByCategory(cat);
				pushScreen(_appListScreen);
			}
		}
		else if(event.getSource() == _appDetailScreen)
		{
			if(event.getAction() == UIConstants.ACTION_APP_REVIEWS)
			{
				_reviewsScreen = new ReviewsScreen();
				_reviewsScreen.addActionListener(this);
				_reviewsScreen.showReviews((App)event.getData());
				pushScreen(_reviewsScreen);
			}
			else if(event.getAction() == UIConstants.ACTION_APP_SCREENSHOTS)
			{
				_screenShotsScreen = new ScreenshotsScreen();
				_screenShotsScreen.addActionListener(this);
				_screenShotsScreen.showScreenshots((App)event.getData());
				pushScreen(_screenShotsScreen);
			}
		}
		
	}
	
    public void popActiveScreen()
    {
    	popScreen(getActiveScreen());
    }
	
	
    

    
	public String getCategoryName(int categoryId)
	{
		return _categoriesScreen.getCategoryName(categoryId);
	}
	
	public AppCache getAppCache()
	{
		return _appCache;
	}
		
	private void initResources()
	{
		_appCache = new AppCache();
    	
    	
		
		/*UiEngineInstance engine = Ui.getUiEngineInstance();
		
		transitionContextSlideInLeft = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transitionContextSlideInLeft.setIntAttribute(TransitionContext.ATTR_DURATION, TRANSITION_DURATION);
		transitionContextSlideInLeft.setIntAttribute(TransitionContext.ATTR_DIRECTION, TransitionContext.DIRECTION_LEFT); 
		transitionContextSlideInLeft.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_IN);
	                        
		transitionContextSlideOutRight = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transitionContextSlideOutRight.setIntAttribute(TransitionContext.ATTR_DURATION, TRANSITION_DURATION);
		transitionContextSlideOutRight.setIntAttribute(TransitionContext.ATTR_DIRECTION, TransitionContext.DIRECTION_RIGHT);  
		transitionContextSlideOutRight.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_OUT);   
		
		transitionContextSlideUp = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transitionContextSlideUp.setIntAttribute(TransitionContext.ATTR_DURATION, TRANSITION_DURATION);
		transitionContextSlideUp.setIntAttribute(TransitionContext.ATTR_DIRECTION, TransitionContext.DIRECTION_UP);  
		transitionContextSlideUp.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_OUT);   
		
		transitionContextSlideDown = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transitionContextSlideDown.setIntAttribute(TransitionContext.ATTR_DURATION, TRANSITION_DURATION);
		transitionContextSlideDown.setIntAttribute(TransitionContext.ATTR_DIRECTION, TransitionContext.DIRECTION_DOWN);  
		transitionContextSlideDown.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_IN); */ 
		
		//engine.setTransition(null, loginScreen, UiEngineInstance.TRIGGER_PUSH, transitionContextSlideDown);
		//engine.setTransition(loginScreen, null, UiEngineInstance.TRIGGER_POP, transitionContextSlideUp);
	                                     
		//engine.setTransition(_homeScreen, _appListScreen, UiEngineInstance.TRIGGER_PUSH, transitionContextSlideInLeft);
		
		//engine.setTransition(_appListScreen, _appDetailScreen, UiEngineInstance.TRIGGER_PUSH, transitionContextSlideInLeft);
		//engine.setTransition(_homeScreen, _categoriesScreen, UiEngineInstance.TRIGGER_PUSH, transitionContextSlideInLeft);
		//engine.setTransition(_categoriesScreen, _appListScreen, UiEngineInstance.TRIGGER_PUSH, transitionContextSlideInLeft);
		//engine.setTransition(_homeScreen, null, UiEngineInstance.TRIGGER_POP, transitionContextSlideOutRight);
		
		
		
	}

	private void checkPermissions() {

		ApplicationPermissionsManager apm = ApplicationPermissionsManager.getInstance();
		ApplicationPermissions original = apm.getApplicationPermissions();

		if ((original.getPermission(ApplicationPermissions.PERMISSION_INPUT_SIMULATION) == ApplicationPermissions.VALUE_ALLOW) && (original.getPermission(ApplicationPermissions.PERMISSION_DEVICE_SETTINGS) == ApplicationPermissions.VALUE_ALLOW) && (original.getPermission(ApplicationPermissions.PERMISSION_CROSS_APPLICATION_COMMUNICATION) == ApplicationPermissions.VALUE_ALLOW) && (original.getPermission(ApplicationPermissions.PERMISSION_INTERNET) == ApplicationPermissions.VALUE_ALLOW) && (original.getPermission(ApplicationPermissions.PERMISSION_SERVER_NETWORK) == ApplicationPermissions.VALUE_ALLOW) && (original.getPermission(ApplicationPermissions.PERMISSION_EMAIL) == ApplicationPermissions.VALUE_ALLOW)) {
			return;
		}

		ApplicationPermissions permRequest = new ApplicationPermissions();
		permRequest.addPermission(ApplicationPermissions.PERMISSION_INPUT_SIMULATION);
		permRequest.addPermission(ApplicationPermissions.PERMISSION_DEVICE_SETTINGS);
		permRequest.addPermission(ApplicationPermissions.PERMISSION_CROSS_APPLICATION_COMMUNICATION);
		permRequest.addPermission(ApplicationPermissions.PERMISSION_INTERNET);
		permRequest.addPermission(ApplicationPermissions.PERMISSION_SERVER_NETWORK);
		permRequest.addPermission(ApplicationPermissions.PERMISSION_EMAIL);

		boolean acceptance = ApplicationPermissionsManager.getInstance().invokePermissionsRequest(permRequest);

		if (acceptance) {
			// User has accepted all of the permissions.
			return;
		} else {
		}
	}

	private void init() 
	{

		connFactory = new HttpConnectionFactory();
		store = PersistentStore.getPersistentObject(persistentObjectId);
		synchronized (store) 
		{
			if (store.getContents() == null) 
			{
				store.setContents(new FacebookSettings(REST_URL, GRAPH_URL, NEXT_URL, APPLICATION_KEY, APPLICATION_SECRET, APPLICATION_ID));
				store.commit();
			}
		}

		try 
		{
			fbc = new FacebookContext(((FacebookSettings) store.getContents()).getApplicationSettings(), connFactory);
			msc = new MobspotContext();
			//msc.addOrUpdateUser(fbc);
			msc.updateHiddenAppNames();

		} catch (Throwable t) {
			t.printStackTrace();
			exit();
		}
		
		
	}

	private void saveSettings(FacebookSettings settings) {
		synchronized (store) {
			store.setContents(settings);
			store.commit();
		}
	}

	public void logoutAndExit() {
		saveSettings(null);
		exit();
	}

	public void saveAndExit() {
		saveSettings(new FacebookSettings(fbc.getApplicationSettings()));
		exit();
	}

	private void exit() {
		AppenderFactory.close();
		System.exit(0);
	}
	
	


}
