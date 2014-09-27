package com.codepath.apps.twitter;

import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileActivity extends FragmentActivity {

	private TwitterClient client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		client = TwitterApp.getRestClient();
		loadProfile();
	}

	private void loadProfile() {
		client.getProfile(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject obj) {
				final User user = User.fromJson(obj);
				getActionBar().setTitle("@" + user.getScreenName());
				populateProfileHeader(user);
			}

			@Override
			public void onFailure(Throwable t, String s) {
				Log.d("debug", t.toString());
				Log.d("debug", s);
			}
		}, isConnected());
	}
	
	private void populateProfileHeader(User user) {
		final TextView tvName = (TextView)findViewById(R.id.tvName);
		tvName.setText(user.getName());
		
		final TextView tvTagLine = (TextView)findViewById(R.id.tvTagLine);
		tvTagLine.setText(user.getTagLine());
		
		final TextView tvFollowers = (TextView)findViewById(R.id.tvFollowers);
		tvFollowers.setText(user.getFollowers() + " followers");
		
		final TextView tvFollowing = (TextView)findViewById(R.id.tvFollowing);
		tvFollowing.setText(user.getFollowing() + " following");
		
		final ImageView ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
		ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfileImage);
	}
	
	protected boolean isConnected() {
	    final ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

	    if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
}
