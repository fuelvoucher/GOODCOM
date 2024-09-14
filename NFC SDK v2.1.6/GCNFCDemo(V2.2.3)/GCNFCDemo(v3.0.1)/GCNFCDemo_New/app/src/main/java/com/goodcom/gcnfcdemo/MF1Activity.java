package com.goodcom.gcnfcdemo;
/*
 * * * * * * * *  mifare card control activity * * * * * * * *
 * */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MF1Activity extends Activity {
	
	Button btn_read_tag = null;
	Button btn_write_tag = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mf1);
		
		btn_read_tag = (Button) findViewById(R.id.btn_read_tag);
		btn_write_tag = (Button) findViewById(R.id.btn_write_tag);
		
		btn_read_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent=new Intent();                
                intent.setClass(MF1Activity.this, Mifare_Read.class);
                startActivity(intent);
            	}
        });
		
		btn_write_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent=new Intent();                
                intent.setClass(MF1Activity.this,Mifare_Write.class);
                startActivity(intent);
            	}
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mf1, menu);
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
