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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Mifare_Write extends Activity {

	TextView nfc_text;
	EditText nfc_edit;
	EditText ed_startBlock;
	EditText ed_writeBlocks;
	
	AlertDialog dialog;
	AlertDialog tipsDlg;
	
	private static final boolean USE_NEW_API = false;
	
	GCAndroidNFC mGcAndroidNFC = new GCAndroidNFC();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		if(USE_NEW_API)
		{
			setContentView(R.layout.activity_mifare__write_new);
			ed_startBlock = (EditText)findViewById(R.id.startBlock);
			ed_writeBlocks = (EditText)findViewById(R.id.writeBlocks);
			nfc_text= (TextView)findViewById(R.id.read_view_top2);
		}
		else
		{
			setContentView(R.layout.activity_mifare__write);
			nfc_text= (TextView)findViewById(R.id.read_view_top);
		}
		
		
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
			 	//The NFC card reader is ready.
			 	case  GCAndroidNFC.GC_MSG_NFC_WAIT:
			 	{
//			 		Toast.makeText(NFC_Write.this, "NFC wait!" ,  2000).show();
			 	}
			 	break;
			 
			 	//When the card is around the device
			 	case  GCAndroidNFC.GC_MSG_NFC_READY:
			 	{
			 		char [] serial_no = (char[]) msg.obj;//The serial number of card
			 		String displayString = "Serial Number:";
			 		
			 		displayString += String.valueOf(serial_no);//Display the serial number.
			 		nfc_text.setText(displayString);
			 		
					if(USE_NEW_API)
					{
						if(ed_startBlock.getText().length() > 0)
						{
							
							
//					 		char secBlock = 11;//the secret block,such as 3/7/11/.......
//					 		
//		    		 		char[] curPwd = {0x21, 0x64, 0x39, 0x45, 0x28, 0x32};//Your new password
//		    		 		
//					 		mGcAndroidNFC.GCUserAuth((char)0x60, secBlock, curPwd);//if KeyA use 0x60, and KeyB use 0x61
					 		
					 		
					 		
							mGcAndroidNFC.GCDefaultAuth((char) Integer.parseInt(ed_startBlock.getText().toString()));
						}
						else
						{
				 			tipsDlg = new AlertDialog.Builder(Mifare_Write.this)
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
					}else
					{
						mGcAndroidNFC.GCDefaultAuth();//Use the default password to unlock the card.
					}			 		
			 	}
			 	break;
			 	
//			 	case  GCAndroidNFC.GC_MSG_NFC_AUTH:
//			 	{
//			 		boolean result = (boolean) msg.obj;
//			 		if(result)
//			 		{
//			 			Log.v("test","NFC auth!");
//				 		char[] newPwd = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
//				 		mGcAndroidNFC.GCSetPwd(newPwd);
//			 		}
//			 		else
//			 		{
//			 			Log.v("test","NFC auth failed!");
//			 		}
//			 	}
//			 	break;
			 	
			 	
			 	//The result of authenticate.
			 	case  GCAndroidNFC.GC_MSG_NFC_AUTH:
			 	{
					int resultType = msg.arg2;
					boolean result = (boolean) msg.obj;//If true, the password is right.

					if(resultType== GCAndroidNFC.GC_RESULT_TYPE_OK && result)
			 		{
			 			dialog = new AlertDialog.Builder(Mifare_Write.this)
			 			.setIcon(R.drawable.ic_launcher)
			 		    .setTitle("Tips")
			 		    .setMessage("Will write to the card")
			 		    //Set dialog button
			 		    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			 		        @Override
			 		        public void onClick(DialogInterface dialog, int which) {			 		        	
			 		            dialog.dismiss();
			 		           mGcAndroidNFC.GcNfcReset();
			 		        }
			 		    })
			 		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			 		        @Override
			 		        public void onClick(DialogInterface dialog, int which) {			 		        
			 		        	dialog.dismiss();			 		        	
								if(USE_NEW_API)
								{
									if(ed_startBlock.getText().length()>0 && ed_writeBlocks.getText().length()>0 && nfc_edit.getText().length()>0)
									{
										/*2018/10/29 new add, use new API to write mifare card*/
					 			 		mGcAndroidNFC.GCWriteMF1((char) Integer.parseInt(ed_startBlock.getText().toString()),
					 			 				(char) Integer.parseInt(ed_writeBlocks.getText().toString()),
					 			 				nfc_edit.getText().toString().toCharArray(), true);//Use new API to write mifare card
									}else
									{
										Toast.makeText(Mifare_Write.this, "Incomplete input parameters!" ,  Toast.LENGTH_SHORT).show();
										mGcAndroidNFC.GcNfcReset();
									}
								}
								else
								{
									if(nfc_edit.getText().length()>0)
									{
										mGcAndroidNFC.GCWriteMF1(nfc_edit.getText().toString().toCharArray());//write mifare card
									}
									else
									{
										Toast.makeText(Mifare_Write.this, "Missing input!" ,  Toast.LENGTH_SHORT).show();
										mGcAndroidNFC.GcNfcReset();
									}
								}			 		        
			 		        }
			 		    }).create();
				 		dialog.show();
			 		}
			 		else
			 		{
			 			tipsDlg = new AlertDialog.Builder(Mifare_Write.this)
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
			 	
			 	case GCAndroidNFC.GC_MSG_SECTOR_FINISH:
			 	{
			 		boolean result = (boolean) msg.obj;
			 		if(result)
			 		{
			 			Log.v("Test","ok,next sector can be write");
//			 			mGcAndroidNFC.GCDefaultAuth((char) 13);
			 		}
			 	}
			 	break;
			 	
			 	//The message of write mifare card.
			 	case GCAndroidNFC.GC_MSG_WRITE_MF1:
			 	{
			 		boolean result = (boolean) msg.obj;
			 		
			 		if(result)
			 		{
//			 			Toast.makeText(Mifare_Write.this, "Mifare writen!" ,  500).show();
			 			tipsDlg = new AlertDialog.Builder(Mifare_Write.this)
			 			.setIcon(R.drawable.ic_launcher)
			 		    .setTitle("Tips")
			 		    .setMessage("Mifare writen!")
			 		    //Set dialog button
			 		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			 		        @Override
			 		        public void onClick(DialogInterface dialog, int which) {	 	
			 		        	mGcAndroidNFC.GCFinishSector();//test
			 		        	dialog.dismiss();
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
	
	 protected void onDestroy() {
	 		mGcAndroidNFC.free();
	 		Log.v("GCPRN_LIB", "nfc write free");
	        super.onDestroy();
	 };  
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mifare__write, menu);
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
