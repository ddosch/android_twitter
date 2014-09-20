package com.codepath.apps.twitter;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codepath.apps.twitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends Activity {

	private static final int ACTIVITY_NUM_COMPOSE = 1;
	
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private ListView lvTweets;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApp.getRestClient();
		lvTweets = (ListView)findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
		    @Override
		    public void onLoadMore(int page, int totalItemsCount) {
		        loadOlderTweets();
		    }
		});
		populateTimeline("1", null);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }
	
	public void onCompose(MenuItem mi) {
	     startActivityForResult(new Intent(this, ComposeActivity.class), ACTIVITY_NUM_COMPOSE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTIVITY_NUM_COMPOSE && resultCode == RESULT_OK) {
			getNewestTweets();
		}
	}

	public void populateTimeline(String sinceId, String maxId) {
		client.getHomeTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray array) {
				aTweets.addAll(Tweet.fromJsonArray(array));
			}
			
			@Override
			public void onFailure(Throwable t, String s) {
				Log.d("debug", t.toString());
				Log.d("debug", s);
			}
		}, sinceId, maxId, isConnected());
	}
	
	public void getNewestTweets() {
		long maxId = Long.MIN_VALUE;
		for (Tweet tweet : tweets) {
			maxId = Math.max(tweet.getUid(), maxId);
		}
		final String sinceId = tweets.isEmpty() ? "1" : "" + maxId;
		client.getHomeTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray array) {
				final ArrayList<Tweet> tweets = Tweet.fromJsonArray(array);
				Collections.reverse(tweets);
				for (Tweet tweet : tweets) {
					aTweets.insert(tweet, 0);
				}
			}
			
			@Override
			public void onFailure(Throwable t, String s) {
				Log.d("debug", t.toString());
				Log.d("debug", s);
			}
		}, sinceId, null, isConnected());
	}
	
	private void loadOlderTweets() {
		long minId = Long.MAX_VALUE;
		for (Tweet tweet : tweets) {
			minId = Math.min(tweet.getUid(), minId);
		}
		final String maxId = tweets.isEmpty() ? null : "" + minId;
		populateTimeline("1", maxId);
	}
	
	private boolean isConnected() {
	    final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

	    if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
}
