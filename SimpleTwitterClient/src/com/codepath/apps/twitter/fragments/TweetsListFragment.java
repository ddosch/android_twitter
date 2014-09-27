package com.codepath.apps.twitter.fragments;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codepath.apps.twitter.EndlessScrollListener;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TweetArrayAdapter;
import com.codepath.apps.twitter.TwitterApp;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public abstract class TweetsListFragment extends Fragment {

	protected ArrayList<Tweet> tweets;
	protected ArrayAdapter<Tweet> aTweets;
	protected ListView lvTweets;
	protected SwipeRefreshLayout swipeView;
	protected TwitterClient client;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterApp.getRestClient();
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(getActivity(), tweets);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
		lvTweets = (ListView)view.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(aTweets);
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
		    @Override
		    public void onLoadMore(int page, int totalItemsCount) {
		        loadOlderTweets();
		    }
		});
		
		swipeView = (SwipeRefreshLayout)view.findViewById(R.id.swipe);
		swipeView.setColorSchemeColors(android.R.color.holo_blue_dark,   android.R.color.holo_blue_light, 
									   android.R.color.holo_green_light, android.R.color.holo_green_light);
		swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewestTweets();
            }
        });
		
		return view;
	}
	
	protected boolean isConnected() {
	    final ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

	    if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
	
	protected void getNewestTweets() {
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
	
	protected void loadOlderTweets() {
		long minId = Long.MAX_VALUE;
		for (Tweet tweet : tweets) {
			minId = Math.min(tweet.getUid(), minId);
		}
		final String maxId = tweets.isEmpty() ? null : "" + minId;
		populateTimeline("1", maxId);
	}
	
	protected abstract void populateTimeline(String sinceId, String maxId);
}
