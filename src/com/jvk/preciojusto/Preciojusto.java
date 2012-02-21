package com.jvk.preciojusto;

import java.util.Hashtable;

import org.apache.http.client.CookieStore;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dreasyLib.comm.Comm;
import com.dreasyLib.comm.Comm.OnCommEvent;
import com.google.gson.Gson;
import com.jvk.preciojusto.frwk.LocalError;
import com.jvk.preciojusto.frwk.TimeStamp;

public class Preciojusto extends Activity implements OnCommEvent {
	/** Called when the activity is first created. */
	public static final String TAG="precioJusto";
	public String Token="";
	public CookieStore cookieStore=null;
	public String photoUrl="";
	public TextView photoTextView;
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


	public void sendPhoto(View target)
	{
		if (!photoUrl.equals(""))
		{
		Log.d(TAG, "Sending photo:"+photoUrl);
		}else{
			Log.d(TAG, "No photo selected");
		}
	}
	
	public void StartQuery(View target) {
		String urlCompleta;
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
		}
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
		AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(acct, "ah", null, this, null, null);
		Bundle authTokenBundle;
		try {
			authTokenBundle = accountManagerFuture.getResult();
			Token = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();   

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

