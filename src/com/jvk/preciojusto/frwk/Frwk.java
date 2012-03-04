package com.jvk.preciojusto.frwk;

import java.util.ArrayList;
import org.apache.http.client.CookieStore;

import android.content.Context;

import com.jvk.preciojusto.frwk.dataModel.Bid;

public class Frwk {

	private static Frwk instance=null;
	public ArrayList<Bid> listBids;
	public String authToken=null;
	public CookieStore cookieStore=null;
	public String userId=null;
	public Context appContext;
	public DatabaseManager dbManager;
	public static Frwk getInstance()
	{
		if (instance==null)
		{
			instance=new Frwk();
			
		}
		return instance;
		
		
	}
	public void initDataBaseManager(Context context)
	{
		appContext=context;
		dbManager=new DatabaseManager(appContext);
		
	}
	
	public Frwk()
	{

	}
	
	
	
}
