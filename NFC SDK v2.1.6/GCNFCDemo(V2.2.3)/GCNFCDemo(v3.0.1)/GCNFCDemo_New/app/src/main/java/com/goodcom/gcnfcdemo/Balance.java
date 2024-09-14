package com.goodcom.gcnfcdemo;

import com.goodcom.gcnfcsdk.GCAndroidNFC;

import android.app.Activity;
import android.graphics.Color;
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
import android.widget.Toast;

public class Balance extends Activity {
	private static final String TAG="Balance";
	GCAndroidNFC mGcAndroidNFC = new GCAndroidNFC();
	TextView nfc_text;
	TextView nfc_edit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_balance);
		
		nfc_edit = (TextView)findViewById(R.id.nfc_text);
		nfc_text = (TextView) findViewById(R.id.read_view_top);
		nfc_text.setTextColor(Color.WHITE);
		GcMsgHandler mGcMsgHandler = new GcMsgHandler(this.getMainLooper());
		mGcAndroidNFC.RegisterNFCHandler(mGcMsgHandler);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.balance, menu);
		return true;
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
						 nfc_edit.setText(e);
						 Log.e(TAG,"Error:"+e);
					 }
				 }
				 break;
			 	//The NFC card reader is ready.
			 	case  GCAndroidNFC.GC_MSG_NFC_WAIT:
			 	{
//			 		Toast.makeText(Balance.this, "NFC wait!" ,  2000).show();
			 	}
			 	break;
			 
			 	//When the card is around the device
			 	case  GCAndroidNFC.GC_MSG_NFC_READY:
			 	{
			 		mGcAndroidNFC.GCDefaultAuth();//Use the default password to unlock the card.
			 	}
			 	break;
			 	
			 	//The message of authenticate.
			 	case  GCAndroidNFC.GC_MSG_NFC_AUTH:
			 	{
					int resultType = msg.arg2;
			 		boolean result = (boolean) msg.obj;
			 		
			 		if(resultType== GCAndroidNFC.GC_RESULT_TYPE_OK && result)
			 		{
			 			mGcAndroidNFC.GCGetValue();
			 		}
			 		else{
						nfc_edit.setText("Password Error!");
					}
			 	}
			 	break;
			 	
			 	//The message of get card value.
			 	case  GCAndroidNFC.GC_MSG_GET_MF1_VALUE:
			 	{
			 		int result = msg.arg2;
			 		int value = (int) msg.obj;
			 		Log.d("Balance","get:"+value);
			 		if (result == GCAndroidNFC.GC_RESULT_TYPE_OK){
						nfc_edit.setText(String.valueOf(value));//Get the value of card.
					}
			 		else{
						nfc_edit.setText("Error!");//Get the value of card.
					}
			 		//mGcAndroidNFC.GcNfcReset();
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
