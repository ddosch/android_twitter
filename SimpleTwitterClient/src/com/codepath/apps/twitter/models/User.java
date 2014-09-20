package com.codepath.apps.twitter.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Users")
public class User extends Model {

	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "screenName")
	private String screenName;
	
	@Column(name = "profileImageUrl")
	private String profileImageUrl;
	
	public User() {
		super();
	}
	
	public static User fromJson(JSONObject json) {
		final User user = new User();
		try {
			user.name = json.getString("name");
			user.uid = json.getLong("id");
			user.screenName = json.getString("screen_name");
			user.profileImageUrl = json.getString("profile_image_url");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		user.save();
		return user;
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
	public JSONObject toJSONObject() {
		final JSONObject json = new JSONObject();
		try {
			json.put("id", uid);
			json.put("name", name);
			json.put("screen_name", screenName);
			json.put("profile_image_url", profileImageUrl);
		} catch (JSONException e) {
			return null;
		}
		return json;
	}
}
