package com.codepath.apps.twitter.models;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {

	private String body;
	private long uid;
	private String createdAt;
	private String relativeCreated;
	private User user;
	private boolean retweeted;
	private long retweetCount;
	private boolean favorited;
	private long favoriteCount;
	
	public static Tweet fromJson(JSONObject json) {
		final Tweet tweet = new Tweet();
		try {
			tweet.body = json.getString("text");
			tweet.uid = json.getLong("id");
			tweet.createdAt = json.getString("created_at");
			tweet.relativeCreated = parseCreated(tweet.createdAt);
			tweet.retweeted = json.optBoolean("retweeted");
			tweet.retweetCount = json.optLong("retweet_count");
			tweet.favorited = json.optBoolean("favorited");
			tweet.favoriteCount = json.optLong("favorite_count");
			tweet.user = User.fromJson(json.getJSONObject("user"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return tweet;
	}
	
	private static String parseCreated(String str) {
		if (str == null) {
			return null;
		}
		// Tue Aug 28 21:08:15 +0000 2012
		try {
			final String[] formats = new String[] { "EEE MMM d HH:mm:ss Z yyyy" };
			final Date date = DateUtils.parseDate(str, formats);
			
			long seconds = (new Date().getTime() - date.getTime()) / 1000;
			if (seconds < 60) {
				return seconds + "s";
			}
			long minutes = seconds / 60;
			if (minutes < 60) {
				return minutes + "m";
			}
			long hours = minutes / 60;
			if (hours < 24) {
				return hours + "h";
			}
			long days = hours / 24;
			return days + "d";
		} catch (DateParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<Tweet> fromJsonArray(JSONArray array) {
		final ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		for (int i = 0; i < array.length(); i++) {
			try {
				final JSONObject jsonObj = array.getJSONObject(i);
				final Tweet tweet = Tweet.fromJson(jsonObj);
				if (tweet != null) {
					tweets.add(tweet);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return tweets;
	}

	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}

	public String getRelativeCreated() {
		return relativeCreated;
	}

	public boolean isRetweeted() {
		return retweeted;
	}

	public long getRetweetCount() {
		return retweetCount;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public long getFavoriteCount() {
		return favoriteCount;
	}
}
