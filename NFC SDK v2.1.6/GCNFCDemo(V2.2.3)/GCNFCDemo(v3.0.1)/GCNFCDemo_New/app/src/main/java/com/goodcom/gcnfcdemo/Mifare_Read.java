package com.goodcom.gcnfcdemo;

import com.goodcom.gcnfcsdk.GCAndroidNFC;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class Mifare_Read extends Activity {
	private static final String TAG="Mifare_Read";
	TextView nfc_text;
	EditText nfc_edit;
	
	EditText ed_startBlock;
	EditText ed_readBlocks;
	
	AlertDialog tipsDlg;
	
	private static final boolean USE_NEW_API = false;
	
	GCAndroidNFC mGcAndroidNFC = new GCAndroidNFC();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(USE_NEW_API)
		{
			setContentView(R.layout.activity_mifare__read_new);
			ed_startBlock = (EditText)findViewById(R.id.startBlock);
			ed_readBlocks = (EditText)findViewById(R.id.readBlocks);
			nfc_text= (TextView)findViewById(R.id.read_view_top2);
		}
		else
		{
			setContentView(R.layout.activity_mifare__read);
			nfc_text= (TextView)findViewById(R.id.read_view_top);
		}
		
		nfc_edit = (EditText)findViewById(R.id.nfc_text);
		
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
			 	case  GCAndroidNFC.GC_MSG_NFC_WAIT:
			 	{
			 		//The NFC card reader is ready.
			 	}
			 	break;
			 
			 	//When the card is around the device
			 	case  GCAndroidNFC.GC_MSG_NFC_READY:
			 	{
			 		char [] serial_no = (char[]) msg.obj;//The serial number of card
			 		String displayString = "Serial Number:";
			 		
			 		displayString += String.valueOf(serial_no);
			 		nfc_text.setText(displayString);//Display the serial number.
			 		
			 		if(USE_NEW_API)
			 		{
			 			if(ed_startBlock.getText().length() > 0)
			 			{
//					 		char secBlock = 11;
////					    char[] curPwd = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
//		    		 		char[] curPwd = {0x21, 0x64, 0x39, 0x45, 0x28, 0x32};
//					 		mGcAndroidNFC.GCUserAuth((char)0x61, secBlock, curPwd);
			 				mGcAndroidNFC.GCDefaultAuth((char) Integer.parseInt(ed_startBlock.getText().toString()));
			 				
			 			}
			 			else
			 			{
				 			tipsDlg = new AlertDialog.Builder(Mifare_Read.this)
				 			.setIcon(R.drawable.ic_launcher)
				 		    .setTitle("Tips")
				 		    .setMessage("Incomplete input parameters!")
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
			 		else
			 		{
				 		mGcAndroidNFC.GCDefaultAuth();//Use the default password to unlock the card.
				 		//char[] newPwd = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
				 		//mGcAndroidNFC.GCUserAuth((char)0x60, (char)7, newPwd);
			 		}
			 	}
			 	break;
			 	
			 	//The result of authenticate.
			 	case  GCAndroidNFC.GC_MSG_NFC_AUTH:
			 	{
					int resultType = msg.arg2;
					boolean result = (boolean) msg.obj;//If true, the password is right.

					if(resultType== GCAndroidNFC.GC_RESULT_TYPE_OK && result)
			 		{
			 			if(USE_NEW_API)
			 			{
			 				if(ed_startBlock.getText().length() > 0 && ed_readBlocks.getText().length() > 0)
			 				{
					 			mGcAndroidNFC.GCReadMF1((char) Integer.parseInt(ed_startBlock.getText().toString()),
						 				(char) Integer.parseInt(ed_readBlocks.getText().toString()));
			 				}else
			 				{
					 			tipsDlg = new AlertDialog.Builder(Mifare_Read.this)
					 			.setIcon(R.drawable.ic_launcher)
					 		    .setTitle("Tips")
					 		    .setMessage("Incomplete parameters!")
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
			 			else
			 			{
				 			mGcAndroidNFC.GCReadMF1();// Read mifare card
			 			}
			 		}
			 		else
			 		{
			 			tipsDlg = new AlertDialog.Builder(Mifare_Read.this)
			 			.setIcon(R.drawable.ic_launcher)
			 		    .setTitle("Tips")
			 		    .setMessage("Password is error")
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
			 	
			 	//The message of read mifare card.
			 	case  GCAndroidNFC.GC_MSG_READ_MF1:
			 	{
			 		char [] test = (char[]) msg.obj;			 		
			 		nfc_edit.setText(String.valueOf(test));
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
	 protected void onDestroy() {
	 		if (mGcAndroidNFC != null) {
			mGcAndroidNFC.free();
			mGcAndroidNFC=null;
		}
	 	super.onDestroy();
	 };  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mifare__read, menu);
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
