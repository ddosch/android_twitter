package com.codepath.apps.twitter;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;

public class ComposeActivity extends Activity {

	private TwitterClient client;
	private EditText etTweet;
	private Menu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		client = TwitterApp.getRestClient();
		
		etTweet = (EditText)findViewById(R.id.etTweet);
		
		etTweet.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (etTweet.getText().toString().length() > 140) {
					etTweet.setText(etTweet.getText().toString().substring(0, 140));
				}
				menu.findItem(R.id.miRemaining).setTitle("" + (140 - etTweet.getText().toString().length()));
			}
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tweet, menu);
        this.menu = menu;
        return true;
    }
	
	public void onTweet(MenuItem mi) {
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
