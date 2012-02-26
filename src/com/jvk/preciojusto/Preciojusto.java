package com.jvk.preciojusto;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.CookieStore;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dreasyLib.comm.Comm;
import com.dreasyLib.comm.Comm.OnCommEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jvk.preciojusto.frwk.CommManager;
import com.jvk.preciojusto.frwk.dataModel.Bid;
import com.jvk.preciojusto.frwk.dataModel.LocalError;
import com.jvk.preciojusto.frwk.dataModel.TimeStamp;
import com.jvk.preciojusto.frwk.dataModel.ImageUploadModels.ImgUrl;

public class Preciojusto extends Activity implements OnCommEvent {
	/** Called when the activity is first created. */
	public static final String TAG="precioJusto";
	public String Token="";
	public CookieStore cookieStore=null;
	public String photoUrl="";
	public TextView photoTextView;
	public String userNick;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.d(TAG, getString(R.string.url_server));
		photoTextView=(TextView) findViewById(R.id.photoUrl);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 

	        if (resultCode == RESULT_OK) {

	                if (requestCode == 1) {

	                        // currImageURI is the global variable I'm using to hold the content:// URI of the image
	                       Uri  currImageURI = data.getData();
	                        String [] proj={MediaStore.Images.Media.DATA};
	                        Cursor cursor = managedQuery( currImageURI,
	                                        proj, // Which columns to return
	                                        null,       // WHERE clause; which rows to return (all rows)
	                                        null,       // WHERE clause selection arguments (none)
	                                        null); // Order-by clause (ascending by name)
	                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	                        cursor.moveToFirst();
	                        photoTextView.setText(cursor.getString(column_index));
	                        photoUrl=cursor.getString(column_index);
	                }
	        }
	}

	public void getPhoto(View target)
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
	}

	
	public void getBids()
	{
		OnCommEvent commEvent=new OnCommEvent() {
			
			public void OnProcess(int percent, String data) {
				Log.d(TAG, "commEvent :OnProcess:"+data+"  p:"+String.valueOf(percent) );
				
			}
			
			public void OnMessage(String string) {
				Log.d(TAG, "commEvent :OnProcess:"+string );
				
			}
			
			public void OnError(Error error) {
				Log.d(TAG, "commEvent :OnError:"+error.getMessage() );
				
			}
			
			public void OnComplete(Object response) {
				Log.d(TAG, "commEvent :OnComplete:"+(String)response);
				Gson gson=new Gson();
				Type listType = new TypeToken<ArrayList<Bid>>() {}.getType();
				List<Bid> list = gson.fromJson((String)response, listType);
				Log.d(TAG, "num elements:"+String.valueOf(list.size()));
				for (int i=0;i<list.size();i++)
					Log.d(TAG,"--->"+list.get(i).description);
			}
		};
		CommManager.getBids(null,null,null, commEvent);
	}
	
	
	public void getBids2()
	{
		OnCommEvent commEvent=new OnCommEvent() {
			
			public void OnProcess(int percent, String data) {
				Log.d(TAG, "commEvent :OnProcess:"+data+"  p:"+String.valueOf(percent) );
				
			}
			
			public void OnMessage(String string) {
				Log.d(TAG, "commEvent :OnProcess:"+string );
				
			}
			
			public void OnError(Error error) {
				Log.d(TAG, "commEvent :OnError:"+error.getMessage() );
				
			}
			
			public void OnComplete(Object response) {
				Log.d(TAG, "commEvent :OnComplete:"+(String)response);
				Gson gson=new Gson();
				Type listType = new TypeToken<ArrayList<Bid>>() {}.getType();
				List<Bid> list = gson.fromJson((String)response, listType);
				Log.d(TAG, "num elements:"+String.valueOf(list.size()));
				for (int i=0;i<list.size();i++)
					Log.d(TAG,"--->"+list.get(i).description);
			}
		};
		if (cookieStore!=null)
			CommManager.getBids(userNick,cookieStore,"newBids", commEvent);
		else
			Log.d(TAG,"primero haz el login!");
			
	}
	
	
	
	public void sendPhotoToServer(String photo)
	{
		OnCommEvent myCommEvent=new OnCommEvent() {
			
			public void OnProcess(int percent, String data) {
				Log.d(TAG, "upload:OnProcess:"+data+"  p:"+String.valueOf(percent) );
				
			}
			
			public void OnMessage(String string) {
				Log.d(TAG, "upload:OnMessage:"+string );				
			}
			
			public void OnError(Error error) {
				Log.d(TAG, "upload:OnError:"+error.getMessage() );				
			}
			
			public void OnComplete(Object response) {
				Log.d(TAG, "upload:OnComplete:"+(String)response );	
				Gson gson=new Gson();
				ImgUrl responseUrl=gson.fromJson((String)response,ImgUrl.class);
				Log.d(TAG, "Image response hash:"+responseUrl.upload.image.deletehash);
				Log.d(TAG, "url to show:"+responseUrl.upload.links.large_thumbnail);
				createAndSendBidToServer(responseUrl.upload.links.large_thumbnail);
			}
		};
		CommManager.UploadPhoto(photo, CommManager.PhotoServers.IMGURL, myCommEvent);
		
		
	}
	
	public void createAndSendBidToServer(String StringUrl)
	{
		final Bid newBid=new Bid();
		newBid.urlPhoto=StringUrl;
		newBid.description="Descripcion de la foto";
		newBid.price=1.2f;
		newBid.timeStamp=Calendar.getInstance().getTimeInMillis();
		newBid.urlData="http://carrefour.es";
		newBid.userPropietary=userNick;
		final OnCommEvent sendBidcommEvent=new OnCommEvent() {
			public void OnProcess(int percent, String data) {
				Log.d(TAG,"OnProcess:"+data);
			}
			public void OnMessage(String string) {
				Log.d(TAG,"OnMessage:"+string);
			}
			public void OnError(Error error) {
				Log.d(TAG,"OnError:");
			}
			public void OnComplete(Object response) {
				Log.d(TAG,"OnComplete:"+(String)response);
			}
		};
		if (cookieStore==null)
		{
			Hashtable hashParams=new Hashtable();
			hashParams.put(Comm.PARAM_AUTH_TOKEN, Token);
			Comm.AUTH_GAE((String) getText(R.string.url_server), hashParams, new OnCommEvent() {
				
				public void OnComplete(Object response) {
					Log.d(TAG,"recibida cooki store");
					cookieStore=(CookieStore) response;
					Log.d(TAG,cookieStore.toString());
					
					CommManager.sendPhotoData(newBid,cookieStore, sendBidcommEvent);
				}
				
				public void OnMessage(String string) {}
				
				public void OnProcess(int percent, String data) {}
				
				public void OnError(Error error) {
					Log.e(TAG, "Error getting cookie:"+error.getMessage());
				}
			});
		}else
		{
			CommManager.sendPhotoData(newBid,cookieStore, sendBidcommEvent);
		}
		
		
	}
	

	public void sendPhoto(View target)
	{
		if (!photoUrl.equals(""))
		{
			sendPhotoToServer(photoUrl);
		
		}else{
			Log.d(TAG, "No photo selected");
		}
	}
	
	public void StartQuery(View target) {
		getBids2();
		
		
		/*String urlCompleta;
		if (!Token.equals(""))
		{
			urlCompleta = getText(R.string.url_server)+"/getBids?testall";
			Log.d(TAG, "URL:::"+urlCompleta);
			if (cookieStore==null)
			{
				Hashtable hashParams=new Hashtable();
				hashParams.put(Comm.PARAM_AUTH_TOKEN, Token);
				Comm.AUTH_GAE((String) getText(R.string.url_server), hashParams, new OnCommEvent() {
					
					public void OnComplete(Object response) {
						Log.d(TAG,"recibida cooki store");
						cookieStore=(CookieStore) response;
						Log.d(TAG,cookieStore.toString());
						sendQuery();
					}
					
					public void OnMessage(String string) {}
					
					public void OnProcess(int percent, String data) {}
					
					public void OnError(Error error) {
						Log.e(TAG, "Error getting cookie:"+error.getMessage());
					}
				});
			}else
			{
				sendQuery();
			}
		}
		else
		{
			Log.d(TAG, "Please, login first");
		}*/
	}

	private void sendQuery() {
		Hashtable hash=new Hashtable();
		hash.put(Comm.PARAM_AUTH_COOKIE, cookieStore);
		Comm.GET((String)getString(R.string.url_server)+"/getBids?testall", hash, this);
		
	}

	public void	LoginAppEngine(View target) {
		Log.d(TAG, "try to login in appengine");

		AccountManager mgr = AccountManager.get(this); 
		Account[] accts = mgr.getAccountsByType("com.google"); 
		Account acct = accts[0];
		userNick=acct.name;
		AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(acct, "ah", null, this, null, null);
		Bundle authTokenBundle;
		try {
			authTokenBundle = accountManagerFuture.getResult();
			Token = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();   
			Hashtable hashParams=new Hashtable();
			hashParams.put(Comm.PARAM_AUTH_TOKEN, Token);
			Comm.AUTH_GAE((String) getText(R.string.url_server), hashParams, new OnCommEvent() {
				
				public void OnComplete(Object response) {
					Log.d(TAG,"recibida cooki store");
					cookieStore=(CookieStore) response;
					Log.d(TAG,cookieStore.toString());
				}
				
				public void OnMessage(String string) {}
				
				public void OnProcess(int percent, String data) {}
				
				public void OnError(Error error) {
					Log.e(TAG, "Error getting cookie:"+error.getMessage());
				}
			});
			Log.d(TAG, "Token:"+Token);
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	
	public void OnMessage(String string) {
		Log.d(TAG,"on msg:"+string);
	}



	
	public void OnProcess(int percent, String data) {
		Log.d(TAG,"OnProcess:"+String.valueOf(percent)+"% dt:"+data);

	}

	
	public void OnError(Error error) {
		Log.d(TAG,"OnError:"+error.getMessage());

	}

	
	public void OnComplete(Object response) {
		Log.d(TAG,"on msg:"+response);
		Gson gson=new Gson();
		TimeStamp timstamp=gson.fromJson((String)response, TimeStamp.class);
		if (timstamp.timestamp==null)
		{
			LocalError err= gson.fromJson((String)response, LocalError.class);
			Log.e(TAG, "Err:"+err.error);
		}else{
			Log.d(TAG, "TimestampData:"+timstamp.timestamp);
		}

	}






}

