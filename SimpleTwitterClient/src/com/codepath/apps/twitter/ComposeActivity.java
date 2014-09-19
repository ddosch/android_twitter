package com.codepath.apps.twitter;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;

public class ComposeActivity extends Activity {

	private TwitterClient client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		client = TwitterApp.getRestClient();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tweet, menu);
        return true;
    }
	
	public void onTweet(MenuItem mi) {
	     final EditText etTweet = (EditText)findViewById(R.id.etTweet);
	     client.postTweet(new JsonHttpResponseHandler() {
	    	 @Override
			 public void onSuccess(JSONObject obj) {
	    		 setResult(RESULT_OK);
	    		 finish();
			 }
			
			 @Override
			 public void onFailure(Throwable t, String s) {
				 Log.d("debug", t.toString());
				 Log.d("debug", s);
			 }
	     }, etTweet.getText().toString());
	}
	
	
}
