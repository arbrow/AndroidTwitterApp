package com.laurengariepy.android.mytwitterapp.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;

import com.laurengariepy.android.mytwitterapp.adapters.TweetsAdapter;
import com.laurengariepy.android.mytwitterapp.helpers.AsyncTweetsSave;
import com.laurengariepy.android.mytwitterapp.helpers.EndlessScrollListener;
import com.laurengariepy.android.mytwitterapp.models.Tweet;
import com.laurengariepy.android.mytwitterapp.models.UserAccountSettings;
import com.laurengariepy.android.mytwitterapp.models.UserInfo;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends ActionBarActivity {
	
	private static final int COMPOSE_TWEET_REQUEST = 1;
	
	private static String    sScreenName;
	private static String    sUserImageUrl;
	private static boolean   sFirstTimeCalled = true;
	
	private ArrayList<Tweet> mTweetsArray; 
	private ListView      	 mLvTweets; 
	private TweetsAdapter 	 mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_timeline);
		
		mTweetsArray = new ArrayList<Tweet>(); 
		mAdapter = new TweetsAdapter(TimelineActivity.this, mTweetsArray);
		mAdapter.clear(); 
		
		mLvTweets = (ListView) findViewById(R.id.lvTweets); 
		mLvTweets.setAdapter(mAdapter);
		mAdapter.clear(); 
		
		createHomeTimeline();
		
		if (sFirstTimeCalled) {
			getUserScreenName();			// Methods' results needed for ComposeTweetActivity. (Initiated here 
			getUserImageUrl(sScreenName); 	// because of async delay.) 
			sFirstTimeCalled = false;		// Assumes no intervening update of screen name or profile image.
		}
		
	}
	
	private void createHomeTimeline() {
		MyTwitterApp.getRestClient().getHomeTimeline(new TwitterListViewHandler());
		showProgressBar(); 
	}
	
	private class TwitterListViewHandler extends JsonHttpResponseHandler {
		@Override 
		public void onSuccess(JSONArray jsonTweets) {
			ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
			mTweetsArray.addAll(tweets); 
			mAdapter.notifyDataSetChanged(); 
			
			mLvTweets.setOnScrollListener(new EndlessScrollListener() {
				@Override
				public void onLoadMore(int page, int totalItemsCount) {
					createHomeTimeline();
					mAdapter.notifyDataSetChanged(); 	// Note: Unlike setAdapter(...), notifyDataSetChanged() will
														// not reset the user's position to the top of the ListView
				}
			});
			hideProgressBar(); 
		}

		@Override
		public void onFailure(Throwable e, JSONArray error) {
			Log.e("ERROR", e.getMessage()); 
		}
	}

	private void getUserScreenName() {
		MyTwitterApp.getRestClient().getAccountSettings(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject jsonSettings) {
				sScreenName = UserAccountSettings.fromJson(jsonSettings).getScreenName(); 
			}
	
			@Override
			public void onFailure(Throwable e, JSONObject error) {
				Log.e("ERROR", e.getMessage()); 
			}
		});
	}
	
	private void getUserImageUrl(String screenName) {
		MyTwitterApp.getRestClient().getUserInformation(screenName, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject jsonUserInfo) {
				sUserImageUrl = UserInfo.fromJson(jsonUserInfo).getProfileImageUrl(); 
			}
	
			@Override
			public void onFailure(Throwable e, JSONObject error) {
				Log.e("ERROR", e.getMessage()); 
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop(); 
		if (mTweetsArray != null) {
			new AsyncTweetsSave().execute(mTweetsArray); 
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("postOnDestroy", true); 
		editor.commit(); 
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_compose_tweet:
			Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
			startActivityForResult(i, COMPOSE_TWEET_REQUEST); 
			break;
		}
		return true; 
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == COMPOSE_TWEET_REQUEST) {
			Tweet tweet = (Tweet) data.getSerializableExtra("new_tweet"); 
			mTweetsArray.add(0, tweet);
			mAdapter.notifyDataSetChanged(); 
		} 
	}
	
	// To be called manually when an async task has started
	public void showProgressBar() {
		setProgressBarIndeterminateVisibility(true);
	}
	
	// To be called manually when an async task has finished
	public void hideProgressBar() {
		setProgressBarIndeterminateVisibility(false); 
	}
	
	public static String getScreenName() {
		return sScreenName;
	}

	public static String getUserImageUrl() {
		return sUserImageUrl;
	}
	
	
}
