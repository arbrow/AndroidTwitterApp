package com.laurengariepy.android.mytwitterapp.activities;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.laurengariepy.android.mytwitterapp.helpers.AsyncTweetSave;
import com.laurengariepy.android.mytwitterapp.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeTweetActivity extends ActionBarActivity {

	private Button 	  mBtnCancel,
				   	  mBtnTweet;
	private ImageView mIvUserImage;
	private TextView  mTvUserName;
	private EditText  mEtNewTweet;
	
	private Typeface  mTypeface;
	private String    mFont = "Roboto-Light.ttf"; 
	
	private boolean   mAlreadyToasted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose_tweet);
		
		mTypeface = Typeface.createFromAsset(getAssets(), mFont);
		
		setupButtons();
		setupImageView();
		setupTextView();
		setupEditText();
	}

	private void setupButtons() {
		mBtnCancel = (Button) findViewById(R.id.btnCancel); 
		mBtnCancel.setTypeface(mTypeface); 
		mBtnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				setResult(RESULT_CANCELED, i);
				finish(); 
			}
		});
		
		mBtnTweet = (Button) findViewById(R.id.btnTweet);
		mBtnTweet.setTypeface(mTypeface); 
		mBtnTweet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String tweetBody = mEtNewTweet.getText().toString();
				tweet(tweetBody);
			}
		});	
	}
	
	private void setupImageView() {
		mIvUserImage = (ImageView) findViewById(R.id.ivUserImage);
		Log.d("DEBUG", "TimelineActivity.sScreenName is: " + TimelineActivity.getScreenName());
		Log.d("DEBUG", "TimelineActivity.sImageUrl just before execution of "
				     + "ImageLoader.getInstance().displayImage(mImageUrl, mIvUserImage) is: " 
				     + TimelineActivity.getUserImageUrl());
		ImageLoader.getInstance().displayImage(TimelineActivity.getUserImageUrl(), mIvUserImage);
	}
	
	private void setupTextView() {
		mTvUserName = (TextView) findViewById(R.id.tvUserName);
		mTvUserName.setTypeface(mTypeface); 
		mTvUserName.setText("@" + TimelineActivity.getScreenName()); 
	}
	
	private void setupEditText() {
		mEtNewTweet = (EditText) findViewById(R.id.etNewTweet); 
		mEtNewTweet.setTypeface(mTypeface); 
		
		// Show soft keyboard when EditText field requests focus
		if (mEtNewTweet.requestFocus()) {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.showSoftInput(mEtNewTweet, InputMethodManager.SHOW_IMPLICIT); 
		}
		
		mEtNewTweet.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!mAlreadyToasted && s.length() == 140) {
					Toast.makeText(ComposeTweetActivity.this, "You've reached the 140 character"
							+ " limit", Toast.LENGTH_LONG).show(); 
					mAlreadyToasted = true; 
				}
				else if (s.length() > 140) {
					mEtNewTweet.setTextColor(Color.RED); 
				} else {
					mEtNewTweet.setTextColor(Color.BLACK); 
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) { }
		});
		
	}
	
	private void tweet(String tweetBody) {
		MyTwitterApp.getRestClient().postTweet(tweetBody, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject jsonTweetResponse) {
				Log.d("DEBUG", "Called onSuccess() in tweet()."); 
				Tweet newTweet = Tweet.fromJson(jsonTweetResponse); 
				new AsyncTweetSave().execute(newTweet); 
				Intent i = new Intent();
				i.putExtra("new_tweet", newTweet);
				setResult(RESULT_OK, i);
				finish(); 
			}	
	
			@Override
			public void onFailure(Throwable e, JSONObject error) {
				Log.d("DEBUG", "Called onFailure() in getUserScreenName(). Failure message: " 
								+ AsyncHttpResponseHandler.FAILURE_MESSAGE);
				Log.e("ERROR", e.getMessage());
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.compose_tweet, menu);
		return true;
	}

//	// To be called manually when an async task has started
//	public void showProgressBar() {
//		setProgressBarIndeterminateVisibility(true);
//	}
//	
//	// To be called manually when an async task has finished
//	public void hideProgressBar() {
//		setProgressBarIndeterminateVisibility(false); 
//	}
	
}
