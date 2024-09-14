package com.goodcom.gcnfcdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NFCActivity extends Activity {
	private static final String TAG="NFCActivity";
	Button btn_mf0;
	Button btn_mf1;
	Button btn_e;
	Button btn_manage;

	Button btn_mf3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
		
		btn_mf0 = (Button)findViewById(R.id.btn_mf0);
		btn_mf1 = (Button)findViewById(R.id.btn_mf1);
		btn_e = (Button)findViewById(R.id.btn_e);
		btn_manage = (Button)findViewById(R.id.btn_manage);

		btn_mf3= (Button)findViewById(R.id.btn_mf3);
		
		btn_mf0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent=new Intent();
                intent.setClass(NFCActivity.this,MF0Activity.class);
                startActivity(intent);
            	}
        });
		
		btn_mf1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent=new Intent();
                intent.setClass(NFCActivity.this,MF1Activity.class);
                startActivity(intent);
            	}
        });
		
		btn_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent=new Intent();
                intent.setClass(NFCActivity.this,E_Wallet.class);
                startActivity(intent);
            	}
        });
		
		btn_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {         	
            
            	Intent intent=new Intent();
                intent.setClass(NFCActivity.this,SmartCardActivity.class);
                startActivity(intent);
            	}
        });

		btn_mf3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(NFCActivity.this,MF3_Desfire_Activity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nfc, menu);
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
