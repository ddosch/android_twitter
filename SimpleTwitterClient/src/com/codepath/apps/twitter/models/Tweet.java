package com.codepath.apps.twitter.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name="Tweets")
public class Tweet extends Model {

	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
	
	@Column(name = "body")
	private String body;
	
	@Column(name = "createdAt")
	private String createdAt;
	
	@Column(name = "relativeCreated")
	private String relativeCreated;
	
	@Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	private User user;
	
	@Column(name = "retweeted")
	private boolean retweeted;
	
	@Column(name = "retweetCount")
	private long retweetCount;
	
	@Column(name = "favorited")
	private boolean favorited;
	
	@Column(name = "favoriteCount")
	private long favoriteCount;
	
	public Tweet() {
		super();
	}
	
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
		tweet.save();
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
	
	public JSONObject toJSONObject() {
		final JSONObject json = new JSONObject();
		try {
			json.put("id", uid);
			json.put("text", body);
			json.put("created_at", createdAt);
			json.put("retweeted", retweeted);
			json.put("retweet_count", retweetCount);
			json.put("favorited", favorited);
			json.put("favorite_count", favoriteCount);
			if (user != null) {
				json.put("user", user.toJSONObject());
			}
		} catch (JSONException e) {
			return null;
		}
		return json;
	}
	
	public static List<Tweet> recentItems(Long id) {
		if (id == null) {
			return new Select().from(Tweet.class).orderBy("uid DESC").limit("25").execute();
		}
		return new Select().from(Tweet.class).where("uid < ?", id).orderBy("uid DESC").limit("25").execute();
	}
	
	public static JSONArray recentItemsAsJSONArray(Long id) {
		final List<Tweet> tweets = recentItems(id);
		final JSONArray array = new JSONArray();
		for (Tweet tweet : tweets) {
			array.put(tweet.toJSONObject());
		}
		return array;
	}
}
