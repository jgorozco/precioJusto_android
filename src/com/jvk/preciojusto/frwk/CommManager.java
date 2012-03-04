package com.jvk.preciojusto.frwk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import org.apache.http.client.CookieStore;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.dreasyLib.comm.Comm;
import com.dreasyLib.comm.Comm.OnCommEvent;
import com.dreasyLib.comm.ExtendedInputStream;
import com.dreasyLib.persistence.ShareObjectManager;
import com.google.gson.Gson;
import com.jvk.preciojusto.frwk.dataModel.Bid;
import com.jvk.preciojusto.frwk.dataModel.BidResult;

public class CommManager {
	//therightpriceapp.appspot.com
	private static final String SERVER = "therightpriceapp.appspot.com";
	public static String AUTH_TOKEN="auth_token";



	public enum PhotoServers{
		IMGURL,PICASA,TWITPIC
	}

	public static void UploadPhoto(String photoUrl,PhotoServers server,OnCommEvent commEvent)
	{
		switch (server) {
		case IMGURL:
			UploadPhotoImgurl(photoUrl, commEvent);
			break;

		default:
			commEvent.OnError(new Error("UPLOAD_SERVER_NOT_IMPLEMENTED"));
			break;
		}


	}

	public static void UploadPhotoImgurl(String photoUrl,OnCommEvent commEvent)
	{
		String urlUpload="http://api.imgur.com/2/upload.json";
		String dev_key="8fa56c86424ccda4dee0107ffbe4a1b1";
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(photoUrl);
			File fd=new File(photoUrl);
			Log.d("sending", "uploading:"+fd.getName()+" with size:"+String.valueOf(fd.length()));
			ExtendedInputStream exin=new ExtendedInputStream(fileInputStream, commEvent, fd.length());
			MultipartEntity mpe=new MultipartEntity();
			mpe.addPart("key", new StringBody(dev_key));
			mpe.addPart("image",new InputStreamBody(exin,"photo.jpg"));	
			Hashtable dataParams=new Hashtable();
			dataParams.put(Comm.DATA_PARAMS_MULTIPART,mpe);
			Comm.UPLOAD(urlUpload, dataParams, commEvent);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}

	public static void sendPhotoData(Bid bid,OnCommEvent mycommEvent)
	{
		if (Frwk.getInstance().cookieStore==null)
		{
			mycommEvent.OnError(new Error("NO_LOGIN_LOGIN_REQUIRED"));
		}else{
			Hashtable hash=new Hashtable();
			hash.put(Comm.PARAM_AUTH_COOKIE, Frwk.getInstance().cookieStore);
			Gson gson=new Gson();
			hash.put(Comm.PARAM_POST_CONTENT, gson.toJson(bid));
			Comm.POST("http://"+SERVER+"/uploadBid", hash, mycommEvent);
		}


	}

	public static void sendBidResult(BidResult bidResult,OnCommEvent commEvent)
	{



	}

	public static void getUserAchievements(String userId,OnCommEvent commEvent)
	{



	}

	public static void getRanking(OnCommEvent commEvent)
	{



	}

	public static void loginUser(Activity activity,OnCommEvent commEvent)
	{
		AccountManager mgr = AccountManager.get(activity); 
		Account[] accts = mgr.getAccountsByType("com.google"); 
		Account acct = accts[0];
		AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(acct, "ah", null, activity, null, null);
		Bundle authTokenBundle;
		try {
			authTokenBundle = accountManagerFuture.getResult();
			String token = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();   
			Frwk.getInstance().authToken=token;
			commEvent.OnComplete(token);
		} catch (Exception e) {
			commEvent.OnError(new Error("AUTH_NOT_COMPLETE"));
		} 


	}



	public static void getBids(String user,String args,OnCommEvent commEvent)
	{
		Hashtable hash=new Hashtable();
		if (Frwk.getInstance().cookieStore!=null)
			hash.put(Comm.PARAM_AUTH_COOKIE, Frwk.getInstance().cookieStore);
		String addedArg="";
		if (args!=null)
			addedArg="?"+args;
		Comm.GET("http://"+SERVER+"/getBids"+addedArg, hash, commEvent);		


	}



}
