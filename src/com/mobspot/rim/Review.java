package com.mobspot.rim;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.blackberry.util.log.Logger;

public class Review 
{
	private String _author;
	private String _subject;
	private String _body;
	private String _date;
	private int _up_votes;
	private int _down_votes;
	private int _reply_count;
	
	protected Logger log = Logger.getLogger(getClass());
	
	public Review(JSONObject review)
	{
		try
		{
			_author = review.getString("author");
			_subject = review.getString("subject");
			_body = review.getString("body");
			_date = review.getString("date");
			_up_votes = review.getInt("up_votes");
			_down_votes = review.getInt("down_votes");
			_reply_count = review.getInt("reply_count");
		}
		catch(JSONException e)
		{
			log.error(e.getMessage());
		}
	}
	
	public String getAuthor()
	{
		return _author;
	}
	
	public String getSubject()
	{
		return _subject;
	}
	
	public String getBody()
	{
		return _body;
	}
	
	public String getDate()
	{
		return _date;
	}
	
	public int getUpVotes()
	{
		return _up_votes;
	}
	
	public int getDownVotes()
	{
		return _down_votes;
	}
	
	public int getReplyCount()
	{
		return _reply_count;
	}
}
