package com.jvk.preciojusto;

import java.util.ArrayList;
import java.util.HashMap;

import com.jvk.preciojusto.frwk.dataModel.Bid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter implements OnClickListener{
	public ArrayList<Bid> listado;
	public Activity activit;
	
	
	
	public MyListAdapter(ArrayList<Bid> listado,Activity activity) {
		this.listado = listado;
		this.activit=activity;
	}

	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return listado.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listado.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 1;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout rowlayout;
        if (convertView==null)
        {
                rowlayout = generateRowView(parent,position);
                
        }
        else
        {
                rowlayout=(RelativeLayout) convertView;
                
        }
        return rowlayout;

	}
	
    public void onClick(View view) {
    	Bid bid=(Bid) view.getTag();
		Intent intent = new Intent(activit, BidPicker.class);   
		intent.putExtra("urlPhoto", bid.urlPhoto);
		activit.startActivity(intent);

    }
	

	private RelativeLayout generateRowView(ViewGroup parent, int position) {
		RelativeLayout rowlayout=(RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.cellview, parent,false);
		 TextView nombre= (TextView) rowlayout.findViewById(R.id.cellview_text);
        nombre.setText(listado.get(position).urlPhoto);
        rowlayout.setTag(listado.get(position));
        rowlayout.setOnClickListener(this);
		return rowlayout;
	}

	public int getViewTypeCount() {
		 Log.d("TID_EXAMPLE","getViewTypeCount");
		return 1;
	}

	public boolean hasStableIds() {
		 Log.d("TID_EXAMPLE","hasStableIds");
		return false;
	}

	public boolean isEmpty() {
		 Log.d("TID_EXAMPLE","isEmpty");
		return false;
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		 Log.d("TID_EXAMPLE","registerDataSetObserver");
		 super.registerDataSetObserver(observer);

	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		 Log.d("TID_EXAMPLE","unregisterDataSetObserver");
		 super.unregisterDataSetObserver(observer);
	}

	 
}
