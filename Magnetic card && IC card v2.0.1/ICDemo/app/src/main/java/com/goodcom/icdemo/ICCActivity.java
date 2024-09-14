/*Author: GoodCom Software Copyright (c) 2018-2019 GoodCom Ltd*/
package com.goodcom.icdemo;
import com.goodcom.gcsdk.GCAndroidICC;
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

public class ICCActivity extends Activity {
	
	private static final String TAG = "GCICC";
	static {
		System.loadLibrary("i2c_jni");
	}
	
	//IC card and magnetic card related processing class
	GCAndroidICC mGcAndroidICC = new GCAndroidICC();
	EditText icc_text;
	String version;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_icc);
	
		icc_text = (EditText) findViewById(R.id.icc_text);
		
		//Receive card reading data by using Handler message mechanism
		GcICCMsgHandler mGcMsgHandler = new GcICCMsgHandler(this.getMainLooper());
		mGcAndroidICC.RegisterICCHandler(mGcMsgHandler);
		mGcAndroidICC.GcOpenCardReader();
		version=mGcAndroidICC.GetVersion();
		icc_text.setText(version);
	}

	//Message receiving and processing class
	public class GcICCMsgHandler extends Handler{

		public GcICCMsgHandler(Looper L)
		{
			super(L);
		}
		
		 @Override
		 public void handleMessage(Message msg) 
		 {
			 switch(msg.what)
			 {
			 	//Magnetic card message for obtaining the magnetic card number
			 	case  GCAndroidICC.GC_MSG_MAG_CARD:
			 	{ 	
			 		Log.v(TAG, "GC_MSG_MAG_CARD ");
			 		char [] test = (char[]) msg.obj;
			 		icc_text.setText(String.valueOf(test));
			 	}
			 	break;
			 	
			 	case GCAndroidICC.GC_MSG_IC_CARD_STATUS:
			 	{
			 		mGcAndroidICC.GcSetICCPowerON();     
			 	}
			 	break;
			 	
			 	case GCAndroidICC.GC_MSG_ICC_POWER_ON:
			 	{
			 		icc_text.setText("Reading your card,please wait.......");
			 		mGcAndroidICC.GcReadICCard();
			 	}
			 	break;
			 	
			 	//IC card message for obtaining the IC card number
			 	case  GCAndroidICC.GC_MSG_IC_CARD:
			 	{ 				 		
			 		char [] test = (char[]) msg.obj;
			 		icc_text.setText(String.valueOf(test));
			 	}
			 	break;
			 	
			 	case GCAndroidICC.GC_MSG_IC_CARD_NO_INSERT:
			 	{
			 		icc_text.setText("IC card no insert");
			 	}
			 	break;
			 	
			 	//Read IC card error
			 	case  GCAndroidICC.GC_MSG_CARD_ERROR:
			 	{
			 		icc_text.setText("Read card error");
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
	    	if(mGcAndroidICC.GetICCReady())
	    	{
	    		  mGcAndroidICC.GC_ReadICC();
	    	}
	     }
	     return super.onKeyDown(keyCode, event);
	  }

	@Override
	 protected void onDestroy() {
	 		mGcAndroidICC.GcCloseCardReader();//Release hardware resources, turn off the power
	 		Log.v(TAG, "free");
	        super.onDestroy();
	 };   
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.icc, menu);
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
