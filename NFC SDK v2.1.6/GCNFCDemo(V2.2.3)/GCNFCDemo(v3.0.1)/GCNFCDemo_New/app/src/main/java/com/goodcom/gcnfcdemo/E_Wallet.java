package com.goodcom.gcnfcdemo;
/*
 * * * * * * * * E wallet activity * * * * * * * * 
 * */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class E_Wallet extends Activity {

	Button btn_active = null;
	Button btn_balance = null;
	Button btn_charge = null;
	Button btn_payment = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_e__wallet);
		
		btn_active = (Button)findViewById(R.id.btn_active);
		btn_balance = (Button)findViewById(R.id.btn_balance);
		btn_charge = (Button)findViewById(R.id.btn_charge);
		btn_payment = (Button)findViewById(R.id.btn_payment);
		
		btn_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent=new Intent();            
                intent.setClass(E_Wallet.this,Active.class);
                startActivity(intent);
            	}
        });
		
		btn_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent=new Intent();               
                intent.setClass(E_Wallet.this,Balance.class);
                startActivity(intent);
            	}
        });
		
		btn_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent=new Intent();                
                intent.setClass(E_Wallet.this,Charge.class);
                startActivity(intent);
            	}
        });
		
		btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent=new Intent();                
                intent.setClass(E_Wallet.this,Payment.class);
                startActivity(intent);
            	}
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.e__wallet, menu);
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
