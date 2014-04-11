package com.laurengariepy.android.mytwitterapp.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Class acts as a Java-representation of user account settings retrieved as a JSONObject 
 * from the Twitter REST API v1.1. Fields are as specified in the API GET account/settings 
 * documentation. 
 */
@Table(name = "User_Account_Settings")
public class UserAccountSettings extends Model implements Serializable {

	@Column(name = "screen_name")
	private String mScreenName;
	
	@Column(name = "User")						// Created for ActiveAndroid to establish a direct  
	private User  mUser; 						// relationship with the User table
	
	public UserAccountSettings() {				// Empty-argument constructor required by ActiveAndroid
		super(); 
	}
	
	public static UserAccountSettings fromJson(JSONObject jsonObject) {
		UserAccountSettings settings = new UserAccountSettings();
		try {
			settings.mScreenName = jsonObject.getString("screen_name"); 
			Log.d("DEBUG", "Called UserAccountSettings.fromJson(...). mScreenName is: " + settings.mScreenName);
		} catch (JSONException e) {
			Log.d("DEBUG", "Thew exception in UserAccountSettings.fromJson(...)"); 
			e.printStackTrace();
		}
		return settings; 
	}

	public String getScreenName() {
		return mScreenName;
	}

	public void setScreenName(String screenName) {
		mScreenName = screenName;
	}
	
}
