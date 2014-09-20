package com.codepath.apps.twitter;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

	private TwitterClient client;
	
	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
		client = TwitterApp.getRestClient();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Tweet tweet = getItem(position);
		View v;
		if (convertView == null) {
			final LayoutInflater inflater = LayoutInflater.from(getContext());
			v = inflater.inflate(R.layout.tweet_item, parent, false);
		} else {
			v = convertView;
		}
		
		loadProfileImage(v, tweet);
		loadUserName(v, tweet);
		loadScreenName(v, tweet);
		loadBody(v, tweet);
		loadRelativeCreated(v, tweet);
		loadRetweets(v, tweet);
		loadFavorites(v, tweet);
		return v;
	}
	
	private void loadProfileImage(View v, Tweet tweet) {
		final ImageView ivProfileImage = (ImageView)v.findViewById(R.id.ivProfileImage);
		ivProfileImage.setImageResource(android.R.color.transparent);
		final ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
	}
	
	private void loadUserName(View v, Tweet tweet) {
		final TextView tvUserName = (TextView)v.findViewById(R.id.tvUserName);
		tvUserName.setText("@" + tweet.getUser().getScreenName());
	}
	
	private void loadScreenName(View v, Tweet tweet) {
		final TextView tvScreenName = (TextView)v.findViewById(R.id.tvScreenName);
		tvScreenName.setText(tweet.getUser().getName());
	}
	
	private void loadBody(View v, Tweet tweet) {
		final TextView tvBody = (TextView)v.findViewById(R.id.tvBody);
		tvBody.setText(tweet.getBody());
	}
	
	private void loadRelativeCreated(View v, Tweet tweet) {
		final TextView tvRelativeCreated = (TextView)v.findViewById(R.id.tvRelativeCreated);
		tvRelativeCreated.setText(tweet.getRelativeCreated());
	}
	
	private void loadRetweets(View v, final Tweet tweet) {
		final TextView tvRetweets = (TextView)v.findViewById(R.id.tvRetweets);
		tvRetweets.setText("" + tweet.getRetweetCount());
		tvRetweets.setTextColor(tweet.isRetweeted() ? Color.parseColor("#177245") : Color.parseColor("#888888"));
		
		final ImageView ivRetweet = (ImageView)v.findViewById(R.id.ivRetweet);
		ivRetweet.setImageResource(tweet.isRetweeted() ? R.drawable.ic_retweet_highlighted : R.drawable.ic_retweet);
		ivRetweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				client.retweet(new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject obj) {
						final TimelineActivity ta = (TimelineActivity)getContext();
						ta.getNewestTweets();
						tvRetweets.setTextColor(Color.parseColor("#177245"));
						ivRetweet.setImageResource(R.drawable.ic_retweet_highlighted);
					}
					
					@Override
					public void onFailure(Throwable t, String s) {
						Log.d("debug", t.toString());
						Log.d("debug", s);
					}
				}, "" + tweet.getUid());
			}
		});
	}
	
	private void loadFavorites(View v, final Tweet tweet) {
		final TextView tvStars = (TextView)v.findViewById(R.id.tvStars);
		tvStars.setText("" + tweet.getFavoriteCount());
		tvStars.setTextColor(tweet.isFavorited() ? Color.parseColor("#F28500") : Color.parseColor("#888888"));
		
		final ImageView ivStar = (ImageView)v.findViewById(R.id.ivStar);
		ivStar.setImageResource(tweet.isFavorited() ? R.drawable.ic_star_highlighted : R.drawable.ic_star);
		ivStar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				client.favorite(new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject obj) {
						final TimelineActivity ta = (TimelineActivity)getContext();
						ta.getNewestTweets();
						tvStars.setTextColor(Color.parseColor("#F28500"));
						ivStar.setImageResource(R.drawable.ic_star_highlighted);
					}
					
					@Override
					public void onFailure(Throwable t, String s) {
						Log.d("debug", t.toString());
						Log.d("debug", s);
					}
				}, "" + tweet.getUid());
			}
		});
	}
}
