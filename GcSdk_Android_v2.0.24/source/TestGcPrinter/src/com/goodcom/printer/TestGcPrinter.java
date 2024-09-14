package com.goodcom.printer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.goodcom.gcprinter.GCAndroidPrint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class TestGcPrinter extends Activity {

	TextView ed_text;
	Button bt_print;
	Button bt_bmp;
	Button btn_s_bmp;
	Spinner spinner1;
	Spinner spinner2;
	ProgressDialog pd; 
	
	private int align;
	private int fontSz;
	
	private String TAG = "GCPRN";
	
	private RadioGroup  FontSzGroup;  
    private RadioButton BigSizeRB;  
    private RadioButton MidRB;  
    private RadioButton SmallRB;  
    
    private RadioGroup  AlignGroup;  
    private RadioButton LeftAlign;  
    private RadioButton CenterAlign;  
    private RadioButton RightAlign; 
    
	GCAndroidPrint mGcAndroidPrint = new GCAndroidPrint();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gc_printer);
		
		
		ed_text = (TextView) findViewById(R.id.ed_text);
		bt_print = (Button) findViewById(R.id.btn_print);
		bt_bmp = (Button) findViewById(R.id.btn_bmp);
		btn_s_bmp = (Button) findViewById(R.id.btn_s_bmp);
		
		FontSzGroup = (RadioGroup)findViewById(R.id.FontSizeGroup);  
		BigSizeRB = (RadioButton)findViewById(R.id.Big_font_Rb);
		MidRB = (RadioButton)findViewById(R.id.Mid_font_Rb);
		SmallRB=(RadioButton)findViewById(R.id.Small_font_Rb);  
		
		AlignGroup = (RadioGroup)findViewById(R.id.AlignGroup);  
		LeftAlign = (RadioButton)findViewById(R.id.LeftAlign);
		CenterAlign = (RadioButton)findViewById(R.id.CenterAlign);
		RightAlign=(RadioButton)findViewById(R.id.RightAlign);  
		
		GcMsgHandler mGcMsgHandler = new GcMsgHandler(this.getMainLooper());
		mGcAndroidPrint.GcPRNInit();
		
		mGcAndroidPrint.GcSetMsgHandler(mGcMsgHandler);
		mGcAndroidPrint.GcSeLanguageType(GCAndroidPrint.GC_LANG_ARAB,0);
		mGcAndroidPrint.GcSetBarcodeType(GCAndroidPrint.GC_BARCODE_CODE93);
		
		pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setTitle("Bmp Test");
		
		FontSzGroup.setOnCheckedChangeListener(new FontSzGroupListener());  
		
		AlignGroup.setOnCheckedChangeListener(new AlignGroupListener());  
		
		bt_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            	if(mGcAndroidPrint.GcGetPrnStatus() != GCAndroidPrint.GC_PRN_IDLE)
            	{
            		Toast.makeText(TestGcPrinter.this, "Busy ,please wait.", 2000).show();
            	}else
            	{
            		String str=ed_text.getText().toString();
            		if(align ==  mGcAndroidPrint.GC_ALIGN_Left){
          			mGcAndroidPrint.GcDrawText(str.toCharArray(), fontSz, null, 0, null, 0);
            		}
            		else if(align == mGcAndroidPrint.GC_ALIGN_Right){
            			mGcAndroidPrint.GcDrawText(null,0,null,0,str.toCharArray(), fontSz);
            		}
            		else{
            			mGcAndroidPrint.GcDrawText(null,0,str.toCharArray(), fontSz, null,0);
            		}
            		mGcAndroidPrint.GcPrintText(true);
            	}
            }
        });
		
		bt_bmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	pd.show();
                Thread thread = new Thread()
                {
			@Override
                    public void run()
                    {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                       startActivityForResult(intent, 2);
                        pd.dismiss();                    
                    }
                };
                thread.start();
            }
        });
		btn_s_bmp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
               startActivityForResult(intent, 1);
			}
		});
		
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner1.setSelection(9);
        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, 
                    int pos, long id) {
           
                String[] languages = getResources().getStringArray(R.array.speed);       
                mGcAndroidPrint.GcSetSpeed(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		spinner2.setSelection(3);
        spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, 
                    int pos, long id) {
           
                String[] languages = getResources().getStringArray(R.array.density);             
                mGcAndroidPrint.GcSetDensity(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
	}

	class FontSzGroupListener implements RadioGroup.OnCheckedChangeListener{  
        @Override  
        public void onCheckedChanged(RadioGroup group, int checkedId){  
            if (checkedId==BigSizeRB.getId()){  
            	fontSz = GCAndroidPrint.GC_FONT_Big;
//            	Log.v(TAG,"Big font");
            }else if (checkedId==MidRB.getId()){  
            	fontSz = GCAndroidPrint.GC_FONT_Medium;
//            	Log.v(TAG,"Mid font");
            }else if (checkedId==SmallRB.getId()){  
            	fontSz = GCAndroidPrint.GC_FONT_Small;
//            	Log.v(TAG,"Small font");
            }    
        }  
    }  
	
	class AlignGroupListener implements RadioGroup.OnCheckedChangeListener{  
        @Override  
        public void onCheckedChanged(RadioGroup group, int checkedId){  
            if (checkedId==LeftAlign.getId()){  
            	 align =  GCAndroidPrint.GC_ALIGN_Left;
//              Log.v(TAG,"Left");
            }else if (checkedId==CenterAlign.getId()){  
            	align =  GCAndroidPrint.GC_ALIGN_Center;
//            	Log.v(TAG,"Center");
            }else if (checkedId==RightAlign.getId()){  
            	align =  GCAndroidPrint.GC_ALIGN_Right;
//            	Log.v(TAG,"Right");
            }    
        }  
    }
	
	
	
	public class GcMsgHandler extends Handler{

		public GcMsgHandler(Looper L)
		{
			super(L);
		}
	    public void defaultMediaPlayer() throws Exception {
	        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        Ringtone r = RingtoneManager.getRingtone(TestGcPrinter.this, notification);
	        r.play();
	    }		
		 @Override
		 public void handleMessage(Message msg) 
		 {
			 switch(msg.what)
			 {
			 	case  GCAndroidPrint.GC_MSG_PAPER:
				try {
					defaultMediaPlayer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(TestGcPrinter.this, "No paper!", Toast.LENGTH_SHORT).show();
			 		break;
			 	case GCAndroidPrint.GC_MSG_HOT:
				try {
					defaultMediaPlayer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(TestGcPrinter.this, "Too hot!", Toast.LENGTH_SHORT).show();
			 		break;
			 	case GCAndroidPrint.GC_MSG_FINISH:
			 		break;
			 	default:
			 		break;
			 }
		 }
	}
	 protected void onDestroy() {
 		 mGcAndroidPrint.GcPRNFree();
         super.onDestroy();
     }
	

    String path;
         @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        	 switch(requestCode){
	        	 case 1:
	                 if (resultCode == Activity.RESULT_OK) {
	                     Uri uri = data.getData();
	                    if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
	                         path = uri.getPath();
	                     }
	                     if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
	                         path = getPath(this, uri);
	                     } else {
	                         path = getRealPathFromURI(uri);
	                     }
	                    	
	                    	if(path!=null){
	                    		Bitmap bmp;
	                    		bmp = getLoacalBitmap(path); 
		                    	if(mGcAndroidPrint.GcPrintBitmap(bmp, GCAndroidPrint.GC_ALIGN_Center,GCAndroidPrint.GC_BMP_COLOR_BITMAP, true)==false){
		                    		Toast.makeText(TestGcPrinter.this, "Busy ,please wait.", 2000).show();
		                    	}
		                    	bmp.recycle();
		                    	bmp=null;
	                    	}

	                 }
	        		 break;
	        	 case 2:
	        		 if (resultCode == Activity.RESULT_OK) {
	                     Uri uri = data.getData();
	                    if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
	                         path = uri.getPath();
	                     }
	                     if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
	                         path = getPath(this, uri);
	                     } else {
	                         path = getRealPathFromURI(uri);
	                     }
	                     Bitmap bmp;
	                    	if(path!=null){
	                    		bmp = getLoacalBitmap(path); 
		                    	if(mGcAndroidPrint.GcPrintBitmap(bmp)==false){
		                    		Toast.makeText(TestGcPrinter.this, "Busy ,please wait.", 2000).show();
		                    	}
	                    	}
	                    	

	                 }
	        		 break;
        	 }

         }
         public String getRealPathFromURI(Uri contentUri) {
             String res = null;
             String[] proj = { MediaStore.Images.Media.DATA };
             Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
           if(null!=cursor&&cursor.moveToFirst()){;
                 int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
                 cursor.close();
             }
             return res;
         }
         @SuppressLint("NewApi")
        public String getPath(final Context context, final Uri uri) {
     
             final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
     
            // DocumentProvider
             if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                 // ExternalStorageProvider
                 if (isExternalStorageDocument(uri)) {
                     final String docId = DocumentsContract.getDocumentId(uri);
                     final String[] split = docId.split(":");
                     final String type = split[0];
     
                     if ("primary".equalsIgnoreCase(type)) {
                         return Environment.getExternalStorageDirectory() + "/" + split[1];
                     }
                 }
                 // DownloadsProvider
                 else if (isDownloadsDocument(uri)) {
    
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
    
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                   final String docId = DocumentsContract.getDocumentId(uri);
                   final String[] split = docId.split(":");
                    final String type = split[0];
   
                   Uri contentUri = null;
                   if ("image".equals(type)) {
                       contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                   } else if ("audio".equals(type)) {
                       contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
    
                    final String selection = "_id=?";
                   final String[] selectionArgs = new String[]{split[1]};
   
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }
   
        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        public String getDataColumn(Context context, Uri uri, String selection,
                                    String[] selectionArgs) {
    
            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};
    
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }
    
       /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        public boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }
   
        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }
    
        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }
        public static Bitmap getLoacalBitmap(String url) {
             try {
                  FileInputStream fis = new FileInputStream(url);
                  return BitmapFactory.decodeStream(fis);
             } catch (FileNotFoundException e) {
                  e.printStackTrace();
                  return null;
             }
        }
}