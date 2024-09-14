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

public class NFC_Write extends Activity {
	private static final String TAG="NFC_Write";
	TextView nfc_text;
	EditText nfc_edit;
	GCAndroidNFC mGcAndroidNFC = new GCAndroidNFC();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc__write);
		
		nfc_text= (TextView)findViewById(R.id.read_view_top);
		nfc_edit = (EditText)findViewById(R.id.write_text);

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
						 nfc_edit.setText(e);
						 Log.e(TAG,"Error:"+e);
					 }
				 }
				 break;
			 	//The NFC card reader is ready.
			 	case  GCAndroidNFC.GC_MSG_NFC_WAIT:
			 	{
//			 		Toast.makeText(NFC_Write.this, "NFC wait!" ,  2000).show();
			 	}
			 	break;
			 	
			 	//When the card is around the device
			 	case  GCAndroidNFC.GC_MSG_NFC_READY:
			 	{
			 		//mGcAndroidNFC.GCWriteMF02(nfc_edit.getText().toString().toCharArray());//Write the ultralight card, the max length is 44.
			 		mGcAndroidNFC.GCWriteMF0ALL(nfc_edit.getText().toString().toCharArray());
			 	}
			 	break;
			 	
			 	//The result of write ultralight card
			 	case  GCAndroidNFC.GC_MSG_WRITE_MF0:
			 	{
			 		boolean result = (boolean) msg.obj;
			 		
			 		if(result)
			 		{
			 			Toast.makeText(NFC_Write.this, "Ultralight (MF0) writen!" ,  500).show();
			 		}
			 		else
			 		{
			 			Toast.makeText(NFC_Write.this, "Ultralight (MF0) write erro!" ,  500).show();
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
	
	 protected void onDestroy() {
	 		mGcAndroidNFC.free();
	        super.onDestroy();
	        Log.v("GCPRN_LIB", "nfc write free");
	 };  
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nfc__write, menu);
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
