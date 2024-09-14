package com.goodcom.gcnfcdemo;

import com.goodcom.gcnfcsdk.GCAndroidNFC;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Active extends Activity {
	private static final String TAG="Active";
	TextView nfc_text;
	TextView active_res;
	
	AlertDialog dialog;
	AlertDialog tipsDlg;
	
	GCAndroidNFC mGcAndroidNFC = new GCAndroidNFC();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_active);
		
		nfc_text = (TextView) findViewById(R.id.read_view_top);
		active_res  = (TextView) findViewById(R.id.active_result);
		active_res.setText("");
		nfc_text.setTextColor(Color.WHITE); 
		
		GcMsgHandler mGcMsgHandler = new GcMsgHandler(this.getMainLooper());
		mGcAndroidNFC.RegisterNFCHandler(mGcMsgHandler);
	}
	
	public class GcMsgHandler extends Handler{

		public GcMsgHandler(Looper L)
		{
			super(L);
		}
		
		 @Override
		 public void handleMessage(Message msg) 
		 {
			 switch(msg.what)
			 {
				 case GCAndroidNFC.GC_MSG_ERROR:
				 {
				 	if (msg.obj != null) {
					 	String e = (String)msg.obj;
						active_res.setText(e);
					 	Log.e(TAG,"Error:"+e);
				 	}
				 }
				 break;
			 	//The NFC card reader is ready.
			 	case  GCAndroidNFC.GC_MSG_NFC_WAIT:
			 	{
//			 		Toast.makeText(Active.this, "NFC wait!" ,  2000).show();
			 	}
			 	break;
			 	
			 	//When the card is around the device
			 	case  GCAndroidNFC.GC_MSG_NFC_READY:
			 	{
			 		mGcAndroidNFC.GCDefaultAuth();//Use the default password to unlock the card.
			 	}
			 	break;
			 	
			 	//The result of authenticate.
			 	case  GCAndroidNFC.GC_MSG_NFC_AUTH:
			 	{
					int resultType = msg.arg2;
					boolean result = (boolean) msg.obj;//If true, the password is right.

			 		if(resultType== GCAndroidNFC.GC_RESULT_TYPE_OK && result)
			 		{
			 			dialog = new AlertDialog.Builder(Active.this)
			 			.setIcon(R.drawable.ic_launcher)
			 		    .setTitle("Tips")
			 		    .setMessage("Will erase the e-wallet data")
			 		    //Set dialog button
			 		    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			 		        @Override
			 		        public void onClick(DialogInterface dialog, int which) {
			 		        	mGcAndroidNFC.GcNfcReset();
			 		            dialog.dismiss();
			 		        }
			 		    })
			 		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			 		        @Override
			 		        public void onClick(DialogInterface dialog, int which) {
			 		 			mGcAndroidNFC.GCSetValue(0);//Set value 0.			 		        	
			 		            dialog.dismiss();
			 		        }
			 		    }).create();					 			
			 			dialog.show();	
			 		}
			 		else
			 		{
			 			tipsDlg = new AlertDialog.Builder(Active.this)
			 			.setIcon(R.drawable.ic_launcher)
			 		    .setTitle("Tips")
			 		    .setMessage("The password is error!")
			 		    //Set dialog button
			 		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			 		        @Override
			 		        public void onClick(DialogInterface dialog, int which) {	 				 		        	
			 		        	dialog.dismiss();
			 		        	mGcAndroidNFC.GcNfcReset();
			 		        }
			 		    }).create();
			 			tipsDlg.show();
			 		}		
			 	}
			 	break;
			 	
			 	//The result of set value.
			 	case GCAndroidNFC.GC_MSG_SET_MF1_VALUE:
			 	{
			 		boolean result = (boolean) msg.obj;//If true,set value success.
			 		
			 		if(result)
			 		{
//			 			active_res.setText(R.string.tips_active_result);
			 			tipsDlg = new AlertDialog.Builder(Active.this)
			 			.setIcon(R.drawable.ic_launcher)
			 		    .setTitle("Tips")
			 		    .setMessage("The data of e-wallet was erase!")
			 		    //Set dialog button
			 		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			 		        @Override
			 		        public void onClick(DialogInterface dialog, int which) {	 				 		        	
			 		        	dialog.dismiss();
			 		        	mGcAndroidNFC.GcNfcReset();
			 		        }
			 		    }).create();
			 			tipsDlg.show();
			 		}
			 	}
			 	break;
			 	
			 	default:
			 		break;
			 }
		 }
	}
	
	 /*
	  * 
	  * * * Required, please do not modify or delete * * *
	  * 
	  * */
	 @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_F10){
	    	if(mGcAndroidNFC.GetICCReady())
	    	{
	    		  Log.v("idv", "Press down");
	    		  mGcAndroidNFC.GC_ReadICC();
	    	}
	     }
	     return super.onKeyDown(keyCode, event);
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.active, menu);
		return true;
	}

	protected void onDestroy() {
	 		mGcAndroidNFC.free();
	        super.onDestroy();
	};  
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
