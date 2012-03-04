package com.jvk.preciojusto;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.jvk.preciojusto.frwk.Frwk;
import com.jvk.preciojusto.frwk.dataModel.Bid;

public class BidDraftList extends Activity {
	public ListView list;
	public MyListAdapter adapter;
	public ArrayList<Bid> elements;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.biddraftlist);
		list=(ListView) findViewById(R.id.biddraftlist);
		elements=Frwk.getInstance().dbManager.refreshElements();
		adapter=new MyListAdapter(elements,this);

		list.setAdapter(adapter);

	}
	
	
	
}
