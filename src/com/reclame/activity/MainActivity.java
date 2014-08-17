package com.reclame.activity;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.reclame.db.DBHelper;

public class MainActivity extends Activity {

	public ReclameAdapter reclameAdapter;
	public ArrayList<ItemReclame> reclames = new ArrayList<ItemReclame>();
	
	private DBHelper dbHelper;

	final String LOG = "LOG";
	
	
	final String url = "http://reclame.esy.es";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbHelper = new DBHelper(getBaseContext());

		AsynhLoadJSON load = new AsynhLoadJSON();
		load.execute();
	}
	
	private void pushToDB(ItemReclame[] reclame){
	
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		for (ItemReclame temp : reclame) {				

			// создаем объект для данных
			ContentValues cv = new ContentValues();

			long ID = temp.getID();
			String _name = temp.getName();
			String description = temp.getDescription();
			String url_picture = temp.getUrl_picture();

			Cursor c = db.query("news", new String[] { "ID" }, "ID=?",
					new String[] { String.valueOf(ID) }, null,

					null, null);
			
			if (!c.moveToFirst()) {
				cv.put("ID", ID);
				cv.put("name", _name);
				cv.put("description", description);
				cv.put("url_picture", url_picture);
				cv.put("checked", false);
				cv.put("is_posting", false);
				

				long rowID = db.insert("news", null, cv);
				Log.d("LOG", "row inserted, ID = " + rowID);
			}							
			
		}
		
	}
	
	class AsynhLoadJSON extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... values) {						

			DefaultHttpClient hC = new DefaultHttpClient();
			ResponseHandler<String> res = new BasicResponseHandler();
			HttpGet http = new HttpGet(url);
			String response = "";

			try {
				response = hC.execute(http, res);
				Log.d(LOG, response);
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

			/*	
			 * get json text
			 * */
			String jsonText = values[0];

			Log.d(LOG, jsonText);
			
			/*	
			 * deserialize json
			 * */

			Gson gson = new GsonBuilder().create();
			ItemReclame[] reclame = gson.fromJson(jsonText, ItemReclame[].class);

			pushToDB(reclame);
			
			/*	
			 * Add to ArrayList
			 * */
			for (ItemReclame temp : reclame) {	
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

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		for (ItemReclame item : reclameAdapter.getBox()) {
			if (item.box) {
				ContentValues cv = new ContentValues();

				result += "\n" + item.toString();

				String id = String.valueOf(item.getID());
				cv.put("checked", true);

				// обновляем по id
				int updCount = db.update("news", cv, "id = ?",
						new String[] { id });
				Log.d("LOG", "updated rows count = " + updCount);
			}

		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();

		Intent intent = new Intent(this, LoginActivity.class);		
		startActivity(intent);
	}

}
