package com.reclame.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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

		return view;
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
