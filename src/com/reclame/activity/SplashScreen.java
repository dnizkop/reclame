package com.reclame.activity;



import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.reclame.R;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		new Thread(new Runnable() {
			public void run() {
				
				DefaultHttpClient hC = new DefaultHttpClient();
				ResponseHandler<String> res = new BasicResponseHandler();
				HttpGet http = new HttpGet("http://reclame.esy.es");
				String response = "";
				
				try {
					response = hC.execute(http, res);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Intent intent = new Intent();
				intent.putExtra("name", response);

				setResult(RESULT_OK, intent);
				finish();
			}
		}).start();

	}

}
