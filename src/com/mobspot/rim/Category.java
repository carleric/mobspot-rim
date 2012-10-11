package com.mobspot.rim;

public class Category 
{
	private int _id;
	private String _name;
	private int _appcnt;
	
	public Category(int id, String name)
	{
		_id = id;
		_name = name;
	}
	
	public Category(int id, String name, int appCount)
	{
		this(id, name);
		_appcnt = appCount;
	}
	
	public void setId(int _id) {
		this._id = _id;
	}
	public int getId() {
		return _id;
	}
	public void setName(String _name) {
		this._name = _name;
	}
	public String getName() {
		return _name;
	}
	public void setAppCount(int _appcnt) {
		this._appcnt = _appcnt;
	}
	public int getAppCount() {
		return _appcnt;
	}
}
