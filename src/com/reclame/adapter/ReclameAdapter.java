package com.reclame.adapter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.reclame.R;
import com.reclame.data.ItemReclame;

public class ReclameAdapter extends BaseAdapter {

	Context ctx;
	LayoutInflater lInflater;
	ArrayList<ItemReclame> objects;

	public ReclameAdapter(Context context, ArrayList<ItemReclame> reclames) {
		ctx = context;
		objects = reclames;
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// кол-во элементов
	@Override
	public int getCount() {
		return objects.size();
	}

	// элемент по позиции
	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.item_reclame, parent, false);
		}

		ItemReclame item = getReclame(position);

		((TextView) view.findViewById(R.id.tvID)).setText(String.valueOf(item
				.getID()));
		((TextView) view.findViewById(R.id.tvDescr)).setText(item.toString());
		//
		CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox); //
		cbBuy.setOnCheckedChangeListener(myCheckChangList);
		cbBuy.setTag(position);
		cbBuy.setChecked(item.box);

		ImageView ivImage = (ImageView) view.findViewById(R.id.ivImage);

		Pair p = new Pair(item.getUrl_picture(), ivImage);

		AsynhLoadImage load = new AsynhLoadImage();
		load.execute(p);

		return view;
	}

	class AsynhLoadImage extends AsyncTask<Pair, Pair, Void> {

		@Override
		protected Void doInBackground(Pair... params) {

			Pair p = params[0];

			String url = (String) p.first;
			ImageView im = (ImageView) p.second;

			Bitmap b = null;

			Log.d("LOG", "url " + url);

			try {
				URL newurl = new URL(url);
				b = BitmapFactory.decodeStream(newurl.openConnection()
						.getInputStream());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			publishProgress(new Pair(im,b));

			return null;
		}

		@Override
		protected void onProgressUpdate(Pair... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);

			Pair p = values[0];
			
			ImageView im = (ImageView)p.first;
			Bitmap b = (Bitmap)p.second;

			im.setImageBitmap(b);
		}

	}

	// товар по позиции
	ItemReclame getReclame(int position) {
		return ((ItemReclame) getItem(position));
	}

	// содержимое корзины
	public ArrayList<ItemReclame> getBox() {
		ArrayList<ItemReclame> box = new ArrayList<ItemReclame>();
		for (ItemReclame i : objects) {
			// если в корзине
			if (i.box)
				box.add(i);
		}
		return box;
	}

	// обработчик для чекбоксов

	OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) { // меняем данные товара (в корзине или нет)
			getReclame((Integer) buttonView.getTag()).box = isChecked;
		}
	};

	public void update(ArrayList<ItemReclame> objects) {
		this.objects.clear();
		this.objects = objects;
		notifyDataSetChanged();
	}
}
