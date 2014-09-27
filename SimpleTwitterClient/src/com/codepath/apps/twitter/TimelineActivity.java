package com.codepath.apps.twitter;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.twitter.fragments.HomeTimelineFragment;
import com.codepath.apps.twitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.twitter.listeners.FragmentTabListener;

public class TimelineActivity extends FragmentActivity {

	private static final int ACTIVITY_NUM_COMPOSE = 1;
	private static final int ACTIVITY_NUM_PROFILE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		setupTabs();
	}

	private void setupTabs() {
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		final Tab tab1 = actionBar
			.newTab()
			.setText("Home")
			.setIcon(R.drawable.ic_home)
			.setTag("HomeTimelineFragment")
			.setTabListener(new FragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this, "home", HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		final Tab tab2 = actionBar
			.newTab()
			.setText("Mentions")
			.setIcon(R.drawable.ic_mentions)
			.setTag("MentionsTimelineFragment")
			.setTabListener(new FragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this, "mentions", MentionsTimelineFragment.class));

		actionBar.addTab(tab2);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }
	
	public void onCompose(MenuItem mi) {
	     startActivityForResult(new Intent(this, ComposeActivity.class), ACTIVITY_NUM_COMPOSE);
	}
	
	public void onProfile(MenuItem mi) {
		startActivityForResult(new Intent(this, ProfileActivity.class), ACTIVITY_NUM_PROFILE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTIVITY_NUM_COMPOSE && resultCode == RESULT_OK) {
//			getNewestTweets();
		}
	}
}
