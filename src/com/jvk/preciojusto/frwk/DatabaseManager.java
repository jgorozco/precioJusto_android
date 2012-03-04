package com.jvk.preciojusto.frwk;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jvk.preciojusto.frwk.dataModel.Bid;


public class DatabaseManager {
	public DatabaseHelper helper;

	public DatabaseManager(Context appContext) {
		helper=new DatabaseHelper(appContext, "bid_drafts.db", null, 1);
	}


	public void addElement(Bid bidToAdd)
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values=new ContentValues();

		values.put("description", bidToAdd.description);
		values.put("urlData", bidToAdd.urlData);
		values.put("urlPhoto", bidToAdd.urlPhoto);
		values.put("userPropietary", bidToAdd.userPropietary);
		values.put("price", bidToAdd.price);
		values.put("timeStamp", bidToAdd.timeStamp);
		long idd=db.insert(DatabaseHelper.TABLE_NAME, null, values);
		Log.d("TAAG","Insert:"+String.valueOf(idd));
	}
	public Bid getBid(String url)
	{
		ArrayList<Bid>bids=refreshElements();
		for (Bid bid : bids) {
			if (bid.urlPhoto.equals(url))
				return bid;
			
		}
		return null;
		
	}
	
	public boolean removeBid(Bid removed)
	{

    	String name=removed.urlPhoto;
    	if ((name!=null)||(name.length()>1))
    	{
        	SQLiteDatabase db = helper.getWritableDatabase();
        	db.delete(DatabaseHelper.TABLE_NAME, "urlPhoto='"+name+"'",null);

    	}
    	return true;
		
	}
	
	public ArrayList<Bid> refreshElements()
	{
		ArrayList<Bid> bids=new ArrayList<Bid>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c=db.query(DatabaseHelper.TABLE_NAME,null,null,null,null,null,null);
		Bid tempBid;
		while (c.moveToNext()) {
			int cols=c.getColumnCount();
			tempBid=new Bid();
			for (int i=0;i<cols;i++)
			{
				if (c.getColumnName(i).equals("description"))
					tempBid.description=c.getString(i);
				else
					if (c.getColumnName(i).equals("urlData"))
						tempBid.urlData=c.getString(i);
					else						
						if (c.getColumnName(i).equals("urlPhoto"))
							tempBid.urlPhoto=c.getString(i);
						else	
							if (c.getColumnName(i).equals("userPropietary"))
								tempBid.userPropietary=c.getString(i);
							else
								if (c.getColumnName(i).equals("price"))
									tempBid.price=c.getFloat(i);
								else	
									if (c.getColumnName(i).equals("timeStamp"))
										tempBid.timeStamp=c.getLong(i);
										
			}
			bids.add(tempBid);
		}

		return bids;
	}


}
