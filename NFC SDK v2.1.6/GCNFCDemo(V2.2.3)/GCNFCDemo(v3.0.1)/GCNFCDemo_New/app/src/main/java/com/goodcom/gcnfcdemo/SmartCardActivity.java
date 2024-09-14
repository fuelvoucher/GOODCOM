package com.goodcom.gcnfcdemo;

import com.goodcom.gcnfcsdk.GCAndroidNFC;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SmartCardActivity extends Activity {
	
	TextView nfc_text;
	EditText nfc_edit;
	
	private static final  String TAG = "[GC NFC]";
	GCAndroidNFC mGcAndroidNFC = new GCAndroidNFC();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smart_card);
		
		nfc_text= (TextView)findViewById(R.id.read_view_top2);
		nfc_edit = (EditText)findViewById(R.id.icc_text);
		
		GcMsgHandler mGcMsgHandler = new GcMsgHandler(this.getMainLooper());
		mGcAndroidNFC.RegisterNFCHandler(mGcMsgHandler);
		String ver=mGcAndroidNFC.GetVersion();
		nfc_text.setText(ver);
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
			 	//The NFC card reader is ready.
			 	case  GCAndroidNFC.GC_MSG_NFC_WAIT:
			 	{
//			 		Toast.makeText(NFC_Read.this, "NFC wait!" ,  2000).show();
			 	}
			 	break;
			 
			 	//When the card is around the device
			 	case  GCAndroidNFC.GC_MSG_NFC_READY:
			 	{				 		
			 		char [] serial_no = (char[]) msg.obj;//The serial number of card
			 		String displayString = "Serial Number:";
			 		
			 		displayString += String.valueOf(serial_no);
			 		nfc_text.setText(displayString);//Display the serial number.

			 		mGcAndroidNFC.GCReadCPU();//Read the card number of smart card.
			 	}
			 	break;			 	
			 	
			 	//The message of reading card.
			 	case GCAndroidNFC.GC_MSG_READING_CARD:
			 	{
			 		nfc_edit.setText("Reading your card,please wait......");
			 	}
			 	break;			 	
			 	
			 	//The message of read smart card
			 	case  GCAndroidNFC.GC_MSG_SMART_CARD:
			 	{ 	
			 		Log.v(TAG, "GC_MSG_SMART_CARD ");
			 		char [] test = (char[]) msg.obj;
			 		nfc_edit.setText(String.valueOf(test));//Display the smart card number.
			 	}
			 	break;

				 case GCAndroidNFC.GC_MSG_ERROR:
				 {
					 if (msg.obj != null) {
						 String e = (String)msg.obj;
						 nfc_edit.setText(e);
						 Log.e(TAG,"Error:"+e);
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
	     };  
	
}
