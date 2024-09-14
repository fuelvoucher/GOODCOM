package com.goodcom.gcnfcdemo;

import com.goodcom.gcnfcsdk.GCAndroidNFC;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class NFC_Read extends Activity {
	private static final String TAG="NFC_Read";
	TextView nfc_title;
	EditText nfc_text;	
	GCAndroidNFC mGcAndroidNFC = new GCAndroidNFC();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc__read);
		
		nfc_title = (TextView) findViewById(R.id.read_view_top);
		nfc_text = (EditText) findViewById(R.id.nfc_text);
		
		//Regist message handler, this handler will receive message from NFC reader by GoodCom library
		GcMsgHandler mGcMsgHandler = new GcMsgHandler(this.getMainLooper());
		mGcAndroidNFC.RegisterNFCHandler(mGcMsgHandler);
	}

	//Message handler
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
						 nfc_text.setText(e);
						 Log.e(TAG,"Error:"+e);
					 }
				 }
				 break;
			 	//The NFC card reader is ready.
			 	case  GCAndroidNFC.GC_MSG_NFC_WAIT:
			 	{
//			 		Toast.makeText(NFC_Read.this, "NFC wait!" ,  2000).show();
			 	}
			 	break;
			 
			 	//When the card is around the device
			 	case  GCAndroidNFC.GC_MSG_NFC_READY:
			 	{
			 		char [] serial_no = (char[]) msg.obj;
			 		String displayString = "Serial Number:";
			 		
			 		displayString += String.valueOf(serial_no);
			 		nfc_title.setText(displayString);
			 		
			 		//mGcAndroidNFC.GCReadMF0();
			 		//mGcAndroidNFC.GCReadMF0(0, 4);
			 		mGcAndroidNFC.GCReadMF0ALL();// read ultralight card
			 	}
			 	break;
			 	
			 	//The message of read ultralight card
			 	case  GCAndroidNFC.GC_MSG_READ_MF0:
			 	{
			 		char [] data = (char[]) msg.obj;
			 		nfc_text.setText(String.valueOf(data)); // Display the ultralight card data
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
	
	 protected void onDestroy() {
	 		mGcAndroidNFC.free();
	         super.onDestroy();
	 };  
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nfc__read, menu);
		return true;
	}

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
