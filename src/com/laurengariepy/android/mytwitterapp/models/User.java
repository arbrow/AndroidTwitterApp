package com.laurengariepy.android.mytwitterapp.models;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Class acts as a Java-representation of a single user retrieved as a JSONObject from the 
 * Twitter REST API v1.1. Fields are as specified in the API Users object documentation. 
 */
@Table(name = "Users")
public class User extends Model implements Serializable {
	
	@Column(name = "user_name")
	private String mName;
	@Column(name = "user_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long   mUserId;						// User ID
	@Column(name = "screen_name")
	private String mScreenName;
	@Column(name = "profile_image_url")
	private String mProfileImageUrl;
	@Column(name = "profile_background_image_url")
	private String mProfileBackgroundImageUrl;				
	@Column(name = "tweet_count")
	private int    mTweetCount;					// Referred to as statuses_count in Twitter API
	@Column(name = "followers_count")
	private int    mFollowersCount;
	@Column(name = "friends_count") 
	private int    mFriendsCount;
	
	@Column(name = "Tweet")						// Created for ActiveAndroid to establish a direct  
	private Tweet  mTweet; 						// relationship with the Tweets table
	
	public User() {								// Empty-argument constructor required by ActiveAndroid
		super(); 
	}
	
    public String getName() {
        return mName;
    }

    public long getUserId() {
        return mUserId;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public String getProfileImageUrl() {
    	return mProfileImageUrl;
    }
    
    public String getProfileBackgroundImageUrl() {
        return mProfileBackgroundImageUrl;
    }

    public int getNumTweets() {
        return mTweetCount;
    }

    public int getFollowersCount() {
        return mFollowersCount;
    }

    public int getFriendsCount() {
        return mFriendsCount;
    }

    // Optional helper method for ActiveAndroid to establish a direct relationship with the 
    // UserAccountSettings table
  	public List<UserAccountSettings> getUserAccountSettings() {
  		return getMany(UserAccountSettings.class, "Users");
  	}
  	
  	// Optional helper method for ActiveAndroid to establish a direct relationship with the 
    // UserInfo table
  	public List<UserInfo> getUserInfo() {
  		return getMany(UserInfo.class, "Users");
  	}
    
    public static User fromJson(JSONObject jsonObject) {
        User u = new User();
        try {
        	u.mName 			 	 	 = jsonObject.getString("name");
        	u.mUserId 						 = jsonObject.getLong("id");
        	u.mScreenName 	    		 = jsonObject.getString("screen_name");
        	u.mProfileImageUrl   		 = jsonObject.getString("profile_image_url");
        	u.mProfileBackgroundImageUrl = jsonObject.getString("profile_background_image_url");
        	u.mTweetCount 				 = jsonObject.getInt("statuses_count");
        	u.mFollowersCount 			 = jsonObject.getInt("followers_count");
        	u.mFriendsCount 	 		 = jsonObject.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

}

