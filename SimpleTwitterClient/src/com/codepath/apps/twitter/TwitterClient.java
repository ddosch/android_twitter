package com.codepath.apps.twitter;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.apps.twitter.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "Hj50WlO4LwjNt4WKem3hYKiwb";       // Change this
	public static final String REST_CONSUMER_SECRET = "1hgHGWc1CLxq9AQcHyrqIBzljo5tXGOV3hDOSUO228NZnL25F8"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}
	
	public void getHomeTimeline(JsonHttpResponseHandler handler, String sinceId, String maxId, boolean connected) {
		getTimeline("statuses/home_timeline.json", handler, sinceId, maxId, connected);
	}
	
	public void getMentionsTimeline(JsonHttpResponseHandler handler, String sinceId, String maxId, boolean connected) {
		getTimeline("statuses/mentions_timeline.json", handler, sinceId, maxId, connected);
	}
	
	public void getUserTimeline(JsonHttpResponseHandler handler, String sinceId, String maxId, boolean connected) {
		getTimeline("statuses/user_timeline.json", handler, sinceId, maxId, connected);
	}
	
	private void getTimeline(String url, JsonHttpResponseHandler handler, String sinceId, String maxId, boolean connected) {
		if (connected) {
			final String apiUrl = getApiUrl(url);
			final RequestParams params = new RequestParams();
			if (sinceId != null) {
				params.put("since_id", sinceId);
			}
			if (maxId != null) {
				params.put("max_id", maxId);
			}
			final boolean haveParams = !(sinceId == null && maxId == null);
			client.get(apiUrl, haveParams ? params : null, handler);
		} else {
			handler.onSuccess(Tweet.recentItemsAsJSONArray(maxId == null ? null : Long.parseLong(maxId)));
		}
	}
	
	public void getProfile(JsonHttpResponseHandler handler, boolean connected) {
		if (connected) {
			final String apiUrl = getApiUrl("account/verify_credentials.json");
			client.get(apiUrl, handler);
		} else {
//			handler.onSuccess(Tweet.recentItemsAsJSONArray(maxId == null ? null : Long.parseLong(maxId)));
		}
	}
	
	public void postTweet(AsyncHttpResponseHandler handler, String status) {
		final String apiUrl = getApiUrl("statuses/update.json");
		final RequestParams params = new RequestParams();
		params.put("status", status);
		client.post(apiUrl, params, handler);
	}
	
	public void retweet(AsyncHttpResponseHandler handler, String id) {
		final String apiUrl = getApiUrl("statuses/retweet/" + id + ".json");
		client.post(apiUrl, handler);
	}
	
	public void favorite(AsyncHttpResponseHandler handler, String id) {
		final String apiUrl = getApiUrl("favorites/create.json");
		final RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, handler);
	}
}