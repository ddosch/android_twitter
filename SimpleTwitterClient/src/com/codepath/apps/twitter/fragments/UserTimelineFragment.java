package com.codepath.apps.twitter.fragments;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetsListFragment {	
	
	private User user;
	
	public static UserTimelineFragment newInstance(User user) {
	    final UserTimelineFragment f = new UserTimelineFragment();
	    final Bundle args = new Bundle();
	    args.putParcelable("user", user);
	    f.setArguments(args);
	    f.user = user;
	    return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		populateTimeline(null, null);
	}
	
	@Override
	protected void populateTimeline(String sinceId, String maxId) {
		client.getUserTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray array) {
				aTweets.addAll(Tweet.fromJsonArray(array));
			}
			
			@Override
			public void onFailure(Throwable t, String s) {
				Log.d("debug", t.toString());
				Log.d("debug", s);
			}
		}, sinceId, maxId, isConnected(), user == null ? null : user.getScreenName());
	}
	
	@Override
	protected void getNewestTweets() {
		long maxId = Long.MIN_VALUE;
		for (Tweet tweet : tweets) {
			maxId = Math.max(tweet.getUid(), maxId);
		}
		final String sinceId = tweets.isEmpty() ? "1" : "" + maxId;
		client.getUserTimeline(new JsonHttpResponseHandler() {
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
		}, sinceId, null, isConnected(), user.getScreenName());
	}
}
