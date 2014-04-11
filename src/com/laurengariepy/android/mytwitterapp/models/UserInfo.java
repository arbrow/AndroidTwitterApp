package com.laurengariepy.android.mytwitterapp.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Class acts as a Java-representation of user information retrieved as a JSONObject 
 * from the Twitter REST API v1.1. Fields are as specified in the API GET users/show 
 * documentation. 
 */
@Table(name = "User_Info")
public class UserInfo extends Model implements Serializable {

	@Column(name = "profile_image_url")
	private String mProfileImageUrl;
	
	@Column(name = "User")						// Created for ActiveAndroid to establish a direct  
	private User  mUser; 						// relationship with the User table
	
	public UserInfo() {							// Empty-argument constructor required by ActiveAndroid
		super();
	}
	
	public static UserInfo fromJson(JSONObject jsonObject) {
		UserInfo info = new UserInfo();
		try {
			info.mProfileImageUrl = jsonObject.getString("profile_image_url");
			Log.d("DEBUG", "Called UserInfo.fromJson(...). mProfileImageUrl is: " + info.mProfileImageUrl);
		} catch (JSONException e) {
			Log.d("DEBUG", "Thew exception in UserInfo.fromJson(...)"); 
			e.printStackTrace();
		}
		return info;
	}

	public String getProfileImageUrl() {
		return mProfileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		mProfileImageUrl = profileImageUrl;
	}
	
}
