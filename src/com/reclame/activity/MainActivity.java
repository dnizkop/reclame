package com.reclame.activity;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.reclame.R;
import com.reclame.adapter.ReclameAdapter;
import com.reclame.data.ItemReclame;


public class MainActivity extends Activity implements OnClickListener {

	public ReclameAdapter reclameAdapter;
	public Context ctx;
	public ArrayList<ItemReclame> reclames = new ArrayList<ItemReclame>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btnName = (Button) findViewById(R.id.btnName);
		btnName.setOnClickListener(this);

		ctx = this;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, SplashScreen.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		
		Button start_post = (Button)findViewById(R.id.start_post);
		start_post.setVisibility(View.VISIBLE);
		
		String name = data.getStringExtra("name");
		Log.d("LOG", "Test DATA " + name);

		Gson gson = new GsonBuilder().create();
		ItemReclame[] reclame = gson.fromJson(name, ItemReclame[].class);

		for (ItemReclame temp : reclame) {
			Log.d("LOG", "Элемент " + temp.toString());
			reclames.add(temp);
		}

		reclameAdapter = new ReclameAdapter(ctx, reclames);
		ListView lvMain = (ListView) findViewById(R.id.lvMain);
		lvMain.setAdapter(reclameAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
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
