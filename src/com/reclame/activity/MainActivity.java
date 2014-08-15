package com.reclame.activity;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.reclame.R;
import com.reclame.adapter.ReclameAdapter;
import com.reclame.data.ItemReclame;

public class MainActivity extends Activity {

	public ReclameAdapter reclameAdapter;
	public ArrayList<ItemReclame> reclames = new ArrayList<ItemReclame>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AsynhLoadJSON load = new AsynhLoadJSON();
		load.execute();
	}

	class AsynhLoadJSON extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			DefaultHttpClient hC = new DefaultHttpClient();
			ResponseHandler<String> res = new BasicResponseHandler();
			HttpGet http = new HttpGet("http://reclame.esy.es");
			String response = "";

			try {
				response = hC.execute(http, res);
				Log.d("LOG", response);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			publishProgress(response);

			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			Button start_post = (Button) findViewById(R.id.start_post);
			start_post.setVisibility(View.VISIBLE);

			String name = values[0];
			
			Log.d("LOG", name);

			
			Gson gson = new GsonBuilder().create();
			ItemReclame[] reclame = gson.fromJson(name, ItemReclame[].class);

			
			for (ItemReclame temp : reclame) {
				Log.d("LOG", "Элемент " + temp.toString());
				reclames.add(temp);
			}

			
			reclameAdapter = new ReclameAdapter(getBaseContext(), reclames);
			ListView lvMain = (ListView) findViewById(R.id.lvMain);
			lvMain.setAdapter(reclameAdapter);
			
		}

	}

	// выводим информацию о корзине
	public void showResult(View v) {
		String result = "Постить:";
		for (ItemReclame item : reclameAdapter.getBox()) {
			if (item.box)
				result += "\n" + item.toString();
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();

		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("name", result);
		startActivity(intent);
	}

}
