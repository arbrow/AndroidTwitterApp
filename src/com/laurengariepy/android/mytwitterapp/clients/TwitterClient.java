package com.laurengariepy.android.mytwitterapp.clients;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.laurengariepy.android.mytwitterapp.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/** 
* Class outfits an AsyncHttpClient to communicate with Twitter's REST API v1.1. AsyncHttpClient is 
* provided with OAuth constants required by the API as well as methods for making HTTP requests to the
* relevant API endpoints. 
* 
* To modify the class: 
* Specify the constants below to change the API being communicated with.
* 
* See a full list of supported API classes: 
*   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
* 
* Key and Secret are provided by resource owner on the developer site for the given API, e.g., 
* dev.twitter.com.
* 
* Add methods for each relevant endpoint in the API.
*/
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; 
    public static final String REST_URL = "https://api.twitter.com/1.1"; 
    public static final String REST_CONSUMER_KEY = "aaNBezHHPEGtRKTwE4B1Q";       
    public static final String REST_CONSUMER_SECRET = "Itd66mLMZYuYYxI9YPxcUwaeKljL94FuD7hn0e5850"; 
    public static final String REST_CALLBACK_URL = "oauth://mytwitterapp"; // Change this (here and in manifest)
   
    private static boolean sIsInitialRequest = true;        
    private Context		   mContext; 
    
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
        mContext = context; 
    }
    
    /* Method makes GET request for home timeline resource from Twitter REST API v1.1. Twitter returns  
     * a rate-limited json-formatted response consisting of a collection of the most recent tweets and 
     * re-tweets posted by the authenticated user and the users he/she follows. 
     * 
     * General approach to defining methods for API endpoints: 
     * 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
     * 2. Define the parameters to pass to the request (query or body)
     *    i.e RequestParams params = new RequestParams("foo", "bar");
     * 3. Define the request method and make a call to the client
     *    i.e client.get(apiUrl, params, handler);
     *    i.e client.post(apiUrl, params, handler);
     */
    public void getHomeTimeline(AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("statuses/home_timeline.json");
    	RequestParams params = new RequestParams();
    	params.put("count", "20");
    	
    	/*
    	 * To implement endless scrolling, update maxID (ie, the ID of the first tweet to be loaded) 
    	 * when BOTH of the following conditions hold: (i) you're making a follow-on request (ie, 
    	 * sIsInitialRequest == false) and (ii) the system isn't calling getHomeTimeline(...) 
    	 * after a change in runtime configuration, such as re-orienting the screen (ie, onPostDestroy 
    	 * == true)
    	 */
    	SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0); 
    	boolean postOnDestroy = prefs.getBoolean("postOnDestroy", false); 
    	if (!sIsInitialRequest && !postOnDestroy) {
    		Tweet.decrementMaxId();								// Avoids duplication of max-ID tweet
    		params.put("max_id", Tweet.getMaxIdAsString());
    	}
    	
    	client.get(url, params, handler); 
    	sIsInitialRequest = false;
    }
    
    /*
     * Method makes GET request to retrieve account settings for current user from Twitter REST API v1.1, 
     * e.g., to recover a screen_name. Twitter returns a rate-limited json-formatted response.
     */
    public void getAccountSettings(AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("account/settings.json"); 
    	client.get(url, handler); 
    }
    
    /*
     * Method makes GET request to retrieve information about specified user from Twitter REST API v1.1, 
     * e.g., to recover a profile_image_url. Twitter returns a rate-limited json-formatted response.
     */
    public void getUserInformation(String screenName, AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("users/show.json");
    	RequestParams params = new RequestParams();
    		// Next line added because asycn request in getAccountSettings() does not return quickly enough 
    		// to supply a value for the screenName parameter. Documentation for AsyncHttpResponseHandler
			// indicates a setUseSynchronousMode(boolean), but the method appears to be broken.
    	screenName = "knickknacking"; 				
    	params.put("screen_name", screenName);
    	client.get(url, params, handler); 
    }
    
    /*
     * Method issues POST command for submitting a tweet via Twitter REST API v1.1. Tweet text typically 
     * limited to 140 characters.  
     */
    public void postTweet(String tweetBody, AsyncHttpResponseHandler handler) { 
    	String url = getApiUrl("statuses/update.json");
    	RequestParams params = new RequestParams();
    	params.put("status", tweetBody);
    	client.post(url, params, handler); 
    }
    
    /*
     * --- NOTE: USE OF METHOD DISCOURAGED ---
     * Method makes GET request to verify account credentials with Twitter REST API v1.1, e.g., to recover
     * a profile_image_url. Twitter returns a rate-limited json-formatted response consisting of
     * key-value pairs representing the requesting user's current settings. 
     */
    public void getAccountCredentials(AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("account/verify_credentials.json");
    	client.get(url, handler);
    }
    
}