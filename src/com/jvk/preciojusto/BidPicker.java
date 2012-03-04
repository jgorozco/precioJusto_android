package com.jvk.preciojusto;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.dreasyLib.comm.Comm.OnCommEvent;
import com.google.gson.Gson;
import com.jvk.preciojusto.frwk.CommManager;
import com.jvk.preciojusto.frwk.Frwk;
import com.jvk.preciojusto.frwk.dataModel.Bid;
import com.jvk.preciojusto.frwk.dataModel.ImageUploadModels.ImgUrl;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class BidPicker extends Activity {

	private ImageButton photoSelected;
	private EditText textoPrecio;
	private Button saveOrRemove;
	private EditText textoUrl;
	private EditText textoDetalles;
	private String photoUrl;
	public OnCommEvent commEvent;
	public boolean alreadySaved;
	private Uri imageUri;
	public static String TAG="BidPicker";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bidpicker);	
		assignXMLElements();
		alreadySaved=false;
		createCommEvent();
		if (getIntent().getExtras()!=null)
		{
			String data=(String) getIntent().getExtras().get("urlPhoto");
			alreadySaved=true;
			Bid bid=Frwk.getInstance().dbManager.getBid(data);
			textoDetalles.setText(bid.description);
			textoPrecio.setText(String.valueOf(bid.price));
			textoUrl.setText(bid.urlData);
			photoUrl=bid.urlPhoto;
			photoSelected.setImageURI(Uri.fromFile(new File(photoUrl)));
			photoSelected.setAdjustViewBounds(true);
			saveOrRemove.setText("Remove");
		}

	}

	public void createCommEvent()
	{
		commEvent=new OnCommEvent() {

			public void OnProcess(int percent, final String data) {
				BidPicker.this.runOnUiThread(new Runnable() {

					public void run() {
						Log.d(TAG, "OnProcess:"+data);
					}
					});
				}

				public void OnMessage(final String string) {
					BidPicker.this.runOnUiThread(new Runnable() {

						public void run() {
							Log.d(TAG, "OnMessage:"+string);
						}
					});
				}

				public void OnError(final Error error) {
					BidPicker.this.runOnUiThread(new Runnable() {

						public void run() {
							Log.d(TAG, "OnError:"+error.getMessage());
						}
					});


				}

				public void OnComplete(final Object response) {
					BidPicker.this.runOnUiThread(new Runnable() {

						public void run() {
							Log.d(TAG, "OnComplete:"+(String)response);
							Toast t=Toast.makeText(getApplicationContext(), "ENVIADO", Toast.LENGTH_LONG);
							t.show();						
						}
					});

				}
			};


		}

		public void assignXMLElements()
		{
			photoSelected=(ImageButton) findViewById(R.id.bp_image);
			textoPrecio=(EditText) findViewById(R.id.bp_txt_price);
			textoUrl=(EditText) findViewById(R.id.bp_txt_url);
			textoDetalles=(EditText) findViewById(R.id.bp_txt_details);
			saveOrRemove=(Button) findViewById(R.id.bidpick_btn_save);


		}

		public void onClickFromGallery(View target)
		{
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);

		}

		public void onClickStoreDraft(View target)
		{
			if (alreadySaved)
			{
				Bid bidEnviar=new Bid();
				bidEnviar.description=textoDetalles.getText().toString();
				bidEnviar.price=Float.parseFloat(textoPrecio.getText().toString());
				bidEnviar.urlData=textoUrl.getText().toString();
				bidEnviar.urlPhoto=photoUrl;
				bidEnviar.userPropietary=Frwk.getInstance().userId;
				Frwk.getInstance().dbManager.removeBid(bidEnviar);

			}else
			{
				if (allDataChecked())
				{
					Bid bidEnviar=new Bid();
					bidEnviar.description=textoDetalles.getText().toString();
					bidEnviar.price=Float.parseFloat(textoPrecio.getText().toString());
					bidEnviar.urlData=textoUrl.getText().toString();
					bidEnviar.urlPhoto=photoUrl;
					bidEnviar.userPropietary=Frwk.getInstance().userId;
					Frwk.getInstance().dbManager.addElement(bidEnviar);
				}
			}

		}

		public void clickSelectPhoto(View target)
		{

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmm");
			Date d=new Date();
			String s = formatter.format(d);
			Random r=new Random();
			String vall=String.valueOf(r.nextInt());
			File dir=new File(Environment.getExternalStorageDirectory()+"/.bidPhotos");
			dir.mkdirs();
			File photo = new File(Environment.getExternalStorageDirectory()+"/.bidPhotos",vall+".jpg");
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(photo));
			photoUrl=photo.getAbsolutePath();
			imageUri = Uri.fromFile(photo);
			startActivityForResult(intent, 2);
		}

		public void clickSend(View target)
		{
			Log.d("TAAG", "--->"+Frwk.getInstance().userId);
			if ((Frwk.getInstance().userId!=null)&&(allDataChecked()))
			{
				Toast t=Toast.makeText(getApplicationContext(), "subiendo foto....", Toast.LENGTH_LONG);
				t.show();
				final Bid bidEnviar=new Bid();
				bidEnviar.description=textoDetalles.getText().toString();
				bidEnviar.price=Float.parseFloat(textoPrecio.getText().toString());
				bidEnviar.urlData=textoUrl.getText().toString();
				bidEnviar.urlPhoto=photoUrl;
				bidEnviar.userPropietary=Frwk.getInstance().userId;
				OnCommEvent localCommEvent=new OnCommEvent() {
					
					public void OnProcess(int percent, String data) {
						Log.d("UP", "Subiendo:["+String.valueOf(percent)+"]"+data);
						
					}
					
					public void OnMessage(String string) {
						Log.d("UP", "OnMessage:["+string);
						
					}
					
					public void OnError(Error error) {
						Log.d("UP", "OnError:["+error.getLocalizedMessage());
						
					}
					
					public void OnComplete(Object response) {
						Gson gson=new Gson();
						ImgUrl responseUrl=gson.fromJson((String)response,ImgUrl.class);
						Log.d(TAG, "Image response hash:"+responseUrl.upload.image.deletehash);
						Log.d(TAG, "url to show:"+responseUrl.upload.links.large_thumbnail);
						bidEnviar.urlPhoto=responseUrl.upload.links.large_thumbnail;
						CommManager.sendPhotoData(bidEnviar, commEvent);
					}
				};
				CommManager.UploadPhotoImgurl(bidEnviar.urlPhoto, localCommEvent);
				

			}else
			{
				Toast t=Toast.makeText(getApplicationContext(), "revisa los datos o haz login", Toast.LENGTH_LONG);
				t.show();
			}
		}



		private boolean allDataChecked() {
			if ((textoPrecio.getText().length()>1)
					&&(photoUrl!=null))
				return true;
			Toast t=Toast.makeText(getApplicationContext(),
					"Revise el precio o la foto", 
					Toast.LENGTH_SHORT);
			t.show();
			return false;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) { 
			Log.d(TAG, "requestCode :"+String.valueOf(requestCode)+"   resultCode:"+String.valueOf(resultCode));
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				case 1:
					Log.d(TAG,"recogiendo:"+String.valueOf(requestCode));
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
					photoUrl=cursor.getString(column_index);
					Log.d(TAG, photoUrl);
					//        Drawable drawable=Drawable.createFromPath(photoUrl);

					//	                        photoSelected.setImageDrawable(drawable);
					photoSelected.setImageURI(Uri.fromFile(new File(photoUrl)));
					//Girar foto!

					//       photoSelected.setb
					//	photoSelected.setImageDrawable()
					break;
				case 2:
					photoSelected.setImageURI(imageUri);
					break;

				}
				photoSelected.setAdjustViewBounds(true);

			}
		}	


	}
