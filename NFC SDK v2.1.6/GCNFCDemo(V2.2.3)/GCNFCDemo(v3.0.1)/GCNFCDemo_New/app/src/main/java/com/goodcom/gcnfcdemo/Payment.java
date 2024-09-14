package com.goodcom.gcnfcdemo;

import com.goodcom.gcprinter.GCAndroidPrint;
import com.goodcom.gcnfcsdk.GCAndroidNFC;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Payment extends Activity {
	private static final String TAG="Payment";
	TextView nfc_text;
	EditText nfc_edit;
	Button btn_prn;
	AlertDialog dialog;
	AlertDialog tipsDlg;
	
	int step = 0;
	int balance = -1;
	
	boolean bwalkpaper = false;
	
	GCAndroidNFC mGcAndroidNFC = new GCAndroidNFC();
	GCAndroidPrint mGcAndroidPrint = new GCAndroidPrint();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		
		mGcAndroidPrint.GcPRNInit();

		nfc_text= (TextView)findViewById(R.id.read_view_top);
		nfc_edit = (EditText)findViewById(R.id.write_text);
		btn_prn = (Button) findViewById(R.id.btn_prn);
		nfc_text.setTextColor(Color.WHITE); 
		
		GcMsgHandler mGcMsgHandler = new GcMsgHandler(this.getMainLooper());
		mGcAndroidNFC.RegisterNFCHandler(mGcMsgHandler);
		mGcAndroidPrint.GcSetMsgHandler(mGcMsgHandler);
		
		btn_prn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(balance != -1)
            	{
     				bwalkpaper = false;
     				if(nfc_edit.getText().length() == 0)
     				{
     					mGcAndroidPrint.GcDrawText("Deduct:".toCharArray(),0, "10".toCharArray(), 0, null,0);
    	 				mGcAndroidPrint.GcDrawText("Balance:".toCharArray(),0, (balance+"").toCharArray(), 0, null,0);
    			 		mGcAndroidPrint.GcPrintText(true);
     				}else
     				{
     					mGcAndroidPrint.GcDrawText("Deduct:".toCharArray(),0, nfc_edit.getText().toString().toCharArray(), 0, null,0);
    	 				mGcAndroidPrint.GcDrawText("Balance:".toCharArray(),0, (balance+"").toCharArray(), 0, null,0);
    			 		mGcAndroidPrint.GcPrintText(true);
     				}
            	}
            	else
            	{
            		Toast.makeText(Payment.this, "No need to print!" ,  500).show();
            	}

            }
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.payment, menu);
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
			 		  Log.v("123", "GC_MSG_NFC_WAIT");
			 		 step = 0;
//			 		Toast.makeText(Payment.this, "NFC wait!" ,  500).show();
//			 		mGcAndroidPrint.GcDrawText("name".toCharArray(),0,"number".toCharArray(), 0, "price".toCharArray(),0);
//			 		mGcAndroidPrint.GcPrintText(true);
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
			 			mGcAndroidNFC.GCGetValue();
			 			step++;
			 		}
			 	}
			 	break;
			 	
			 	//The message of get card value.
			 	case  GCAndroidNFC.GC_MSG_GET_MF1_VALUE:
			 	{
			 		int result = msg.arg2;
			 		int value = (int) msg.obj;
			 		balance = value;
			 		if(value >= 0)
			 		{	
			 			switch(step)
			 			{
				 			case 1://compare balance and reduce
				 			{
						 		if(nfc_edit.getText().length() == 0)
						 		{
						 			if(value >= 10)
						 			{
						 				ShowDefaultDeductDialog();						 		
						 			}else
						 			{
						 				ShowTipsDialog();
						 			}
						 		}
						 		else
						 		{				 			
						 			if(value >= Integer.parseInt( nfc_edit.getText().toString() ) )
						 			{
						 				ShowCusDeductDialog();
						 			}else
						 			{
						 				ShowTipsDialog();
						 			}
						 		}
				 			}
				 			break;
				 			
				 			case 2://print balance
				 			{
				 				mGcAndroidNFC.GcNfcReset();
				 			}
				 			break;
			 			}			 			
			 		}
			 	}
			 	break;
			 				 	
			 	//The result of reduce the card value.
			 	case GCAndroidNFC.GC_MSG_MF1_VALUE_REDUCE:
			 	{
			 		boolean result = (boolean) msg.obj;//The result of reduce card value.
			 		
			 		if(result)
			 		{
			 			Toast.makeText(Payment.this, "Payment success!" ,  500).show();
			 			mGcAndroidNFC.GCGetValue();
			 			step++;
			 		}
			 	}
			 	break;
			 	
	             case  GCAndroidPrint.GC_MSG_PAPER:
	             {
	            	 Toast.makeText(Payment.this, "No paper!", 2000).show();
	             }
	             break;                  
                 			 	
//			 	case GCAndroidPrint.GC_MSG_FINISH:
//			 	{
//			 		if(bwalkpaper == false)
//			 		{
//			 			mGcAndroidPrint. GcWalkPaper(16);
//			 			bwalkpaper = true;
//			 		}
//			 		
//			 	} 
//			 	break;
			 	
			 	default:
			 		break;
			 }
		 }
	}

	private void ShowDefaultDeductDialog()
	{
			dialog = new AlertDialog.Builder(Payment.this)
			.setIcon(R.drawable.ic_launcher)
		    .setTitle("Tips")//Dialog title
		    .setMessage("The card will deduct 10")//Dialog message
		    //Set dialog button
		    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {//Cancel button
		        @Override
		        public void onClick(DialogInterface dialog, int which) {				 		        	
		            dialog.dismiss();
		            mGcAndroidNFC.GcNfcReset();
		        }
		    })
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {//OK button
		        @Override
		        public void onClick(DialogInterface dialog, int which) {				 		        	
		            dialog.dismiss();
		            mGcAndroidNFC.GCReduceValue(10);
		        }
		    }).create();
			dialog.show();
	}
	
	private void ShowCusDeductDialog()
	{
			dialog = new AlertDialog.Builder(Payment.this)
			.setIcon(R.drawable.ic_launcher)
		    .setTitle("Tips")//Dialog title
		    .setMessage("The card will deduct " + nfc_edit.getText().toString())//Dialog message
		    //Set dialog button
		    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {//Cancel button
		        @Override
		        public void onClick(DialogInterface dialog, int which) {				 		        	
		            dialog.dismiss();
		            mGcAndroidNFC.GcNfcReset();
		        }
		    })
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {//OK button
		        @Override
		        public void onClick(DialogInterface dialog, int which) {				 		        	
		            dialog.dismiss();
		            mGcAndroidNFC.GCReduceValue(Integer.valueOf(nfc_edit.getText().toString()).intValue());
		        }
		    }).create();
			dialog.show();
	}
	
	private void ShowTipsDialog()
	{
			dialog = new AlertDialog.Builder(Payment.this)
			.setIcon(R.drawable.ic_launcher)
		    .setTitle("Tips")//Dialog title
		    .setMessage("The balance is not enough!")//Dialog message
		    //Set dialog button
		    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {//Cancel button
		        @Override
		        public void onClick(DialogInterface dialog, int which) {	 		        	
		        	dialog.dismiss();
		            mGcAndroidNFC.GcNfcReset();		            
		        }
		    })
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {//OK button
		        @Override
		        public void onClick(DialogInterface dialog, int which) {				        	
		            dialog.dismiss();
		            mGcAndroidNFC.GcNfcReset();
		        }
		    }).create();
			dialog.show();
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
	 		mGcAndroidPrint.GcPRNFree();
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
