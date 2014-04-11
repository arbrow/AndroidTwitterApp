package com.laurengariepy.android.mytwitterapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Class acts as a Java-representation of a single tweet retrieved as a JSONObject from the 
 * Twitter REST API v1.1. Fields are as specified in the API Tweets object documentation. 
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
	
	@Column(name = "max_id")
	private static long sMaxId; 	// ID of the last (ie, earliest-timestamped) tweet to be processed in the current JSONArray
	
	@Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long    	mTweetId;	// Tweet ID
	@Column(name = "created_at")
	private String  	mCreatedAt; 
	@Column(name = "tweet_body")
	private String  	mBody;
	@Column(name = "favorited")
	private boolean 	mFavorited;
	@Column(name = "retweeted")
	private boolean 	mRetweeted;
	@Column(name = "user")
    private User    	mUser;

	public Tweet() {				// Empty-argument constructor required by ActiveAndroid
		super(); 
	}	
	
    public static long getMaxId() {
    	return sMaxId;
    }
    
    public static String getMaxIdAsString() {
    	return String.valueOf(sMaxId);
    }
    
    public static void decrementMaxId() {
    	sMaxId -= 1; 
    }
    
    public long getTweetId() {
        return mTweetId;
    }

    public String getCreatedAt() {
    	return mCreatedAt;
    }
    
    public String getBody() {
        return mBody;
    }

    public boolean isFavorited() {
        return mFavorited;
    }

    public boolean isRetweeted() {
        return mRetweeted;
    }

    public User getUser() {
        return mUser;
    }
    
    // Optional helper method for ActiveAndroid to establish a direct relationship with the Users table
 	public List<User> getUsers() {
 		return getMany(User.class, "Tweets");
 	}
    
    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
        	tweet.mTweetId 		 = jsonObject.getLong("id");
        	tweet.mCreatedAt = jsonObject.getString("created_at");
        	tweet.mBody 	 = jsonObject.getString("text");
        	tweet.mFavorited = jsonObject.getBoolean("favorited");
        	tweet.mRetweeted = jsonObject.getBoolean("retweeted");
            tweet.mUser 	 = User.fromJson(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJson(tweetJson);
            if (tweet != null) {
            	sMaxId = tweet.getTweetId();
                tweets.add(tweet);
            }
            
        }
        return tweets;
    }
    
    public static void saveTweet(Tweet tweet) {
    	tweet.mUser.save();
    	tweet.save();
    }
    
    public static void saveTweets(ArrayList<Tweet> tweets) {
    	ActiveAndroid.beginTransaction();
    	try {
	    	for (int i = 0; i < tweets.size(); i++) {
	    		Tweet t = tweets.get(i);
	    		Log.d("DEBUG", "Inside saveTweets(ArrayList<Tweet>), current tweet is: "  + t.toString());
	    		if (t != null) {
	    			if (t.mUser != null) {
	    				t.mUser.save();
	    			} 
	    			t.save(); 
	    		}
	    	}
	    	ActiveAndroid.setTransactionSuccessful();
    	} finally {
    		ActiveAndroid.endTransaction(); 
    	}
    }
    
}