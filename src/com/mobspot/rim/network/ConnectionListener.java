package com.mobspot.rim.network;

import net.rim.device.api.system.Bitmap;

public interface ConnectionListener 
{
	/*void updateStatus(String status);
	void updateResponseCode(int responseCode);
	void updateResponse(String response);
	void updateResponse(Bitmap image);
	void stateChanged(int state);*/
	
	void onResponse(Request r);
}
