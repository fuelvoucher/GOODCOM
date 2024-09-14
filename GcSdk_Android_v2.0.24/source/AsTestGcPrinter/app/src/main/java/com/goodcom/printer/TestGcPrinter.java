package com.goodcom.printer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.goodcom.gcprinter.GCAndroidPrint;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class TestGcPrinter extends Activity {
    private static final String TEST_STR="Test";

    TextView mEdText;
    Button mBtnBmp;
    Button mBtnSBmp;
    Spinner mSpinner1;
    Spinner mSpinner2;
	Spinner spinnerFontType;
	Spinner spinnerFontSize;
    ProgressDialog mPd;
	int mPrintSpeed =8;
	int mPrintDensity =4;
	int mFontType =0;

    private int mAlign;
    private int mFontSz;

    private RadioButton mLeftAlign;
    private RadioButton mCenterAlign;
    private RadioButton mRightAlign;

    GCAndroidPrint mGcAndroidPrint = new GCAndroidPrint();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mEdText = findViewById(R.id.ed_text);
        Button btPrint = findViewById(R.id.btn_print);
        mBtnBmp = findViewById(R.id.btn_bmp);
        mBtnSBmp = findViewById(R.id.btn_s_bmp);


        RadioGroup alignGroup = findViewById(R.id.AlignGroup);
        mLeftAlign = findViewById(R.id.LeftAlign);
        mCenterAlign = findViewById(R.id.CenterAlign);
        mRightAlign = findViewById(R.id.RightAlign);

        GcMsgHandler mGcMsgHandler = new GcMsgHandler(this.getMainLooper());
        mGcAndroidPrint.GcPRNInit();

        mGcAndroidPrint.GcSetMsgHandler(mGcMsgHandler);
        mGcAndroidPrint.GcSeLanguageType(GCAndroidPrint.GC_LANG_ARAB,0);
        mGcAndroidPrint.GcSetBarcodeType(GCAndroidPrint.GC_BARCODE_CODE93);
        mGcAndroidPrint.GcSetFontType(GCAndroidPrint.GC_FONT_TYPE_GC_FONT_II);

        mPd = new ProgressDialog(this);
        mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPd.setCancelable(false);
        mPd.setCanceledOnTouchOutside(false);
        mPd.setTitle("Bmp Test");
        mEdText.setText(TEST_STR);


        alignGroup.setOnCheckedChangeListener(new AlignGroupListener());

        btPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mGcAndroidPrint.GcGetPrnStatus() != GCAndroidPrint.GC_PRN_IDLE)
                {
                    Toast.makeText(TestGcPrinter.this, "Busy ,please wait.", Toast.LENGTH_SHORT).show();
                }else
                {
                    mGcAndroidPrint.GcDrawText(null, 0, "Order".toCharArray(), GCAndroidPrint.GC_FONT_Big, null, 0);
                    mGcAndroidPrint.GcPrintText(true);
                    mGcAndroidPrint.GcDrawText("-------------------------------".toCharArray(), 0, null, 0, null, 0);
                    mGcAndroidPrint.GcDrawText("print speed".toCharArray(), 0, null, 0,("" + (mPrintSpeed + 1)).toCharArray(), 0);
                    mGcAndroidPrint.GcDrawText("print Density".toCharArray(), 0, null, 0,("" + (mPrintDensity + 1)).toCharArray(), 0);
                    String strVer=mGcAndroidPrint.GetPRNLIBVer();
                    if(strVer.length()>0){
                        mGcAndroidPrint.GcDrawText("PRN Ver:".toCharArray(),GCAndroidPrint.GC_FONT_Small,null,0,strVer.toCharArray(),GCAndroidPrint.GC_FONT_Small);
                    }
                    mGcAndroidPrint.GcDrawText("-------------------------------".toCharArray(), 0, null, 0, null, 0);
                    mGcAndroidPrint.GcDrawText("Name".toCharArray(), GCAndroidPrint.GC_FONT_SmallBold, "Number".toCharArray(), GCAndroidPrint.GC_FONT_SmallBold,"Amount".toCharArray(), GCAndroidPrint.GC_FONT_SmallBold);
                    mGcAndroidPrint.GcDrawText("food".toCharArray(), 0, "1".toCharArray(), 0,"6.00".toCharArray(), GCAndroidPrint.GC_FONT_Medium);
                    mGcAndroidPrint.GcDrawText("rice".toCharArray(), 0,"2".toCharArray(), 0, "2.00".toCharArray(), GCAndroidPrint.GC_FONT_Medium);
                    mGcAndroidPrint.GcDrawText("-------------------------------".toCharArray(), 0, null, 0, null, 0);
                    String str= mEdText.getText().toString();
                    if(mAlign == GCAndroidPrint.GC_ALIGN_Left){
                        mGcAndroidPrint.GcDrawText(str.toCharArray(), mFontSz, null, 0, null, 0);
                    }
                    else if(mAlign == GCAndroidPrint.GC_ALIGN_Right){
                        mGcAndroidPrint.GcDrawText(null,0,null,0,str.toCharArray(), mFontSz);
                    }
                    else{
                        mGcAndroidPrint.GcDrawText(null,0,str.toCharArray(), mFontSz, null,0);
                    }
                    mGcAndroidPrint.GcDrawBarcode("1234567890".toCharArray(), GCAndroidPrint.GC_ALIGN_Center);
                    mGcAndroidPrint.GcDrawText(null,0,"1234567890".toCharArray(), 0, null,0);

                    mGcAndroidPrint.GcDrawText("-------------------------------".toCharArray(), 0, null, 0, null, 0);
                    mGcAndroidPrint.GcDrawText("Thanks!".toCharArray(), 0, null, 0, null, 0);

                }
            }
        });

        mBtnBmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPd.show();
                Thread thread = new Thread()
                {
                    @Override
                    public void run()
                    {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, 2);
                        mPd.dismiss();
                    }
                };
                thread.start();
            }
        });
        mBtnSBmp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });

        mSpinner1 = findViewById(R.id.spinner1);
        mSpinner1.setSelection(mPrintSpeed);
        mSpinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

		mPrintSpeed =pos;
                mGcAndroidPrint.GcSetSpeed(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        mSpinner2 = findViewById(R.id.spinner2);
        mSpinner2.setSelection(mPrintDensity);
        mSpinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

				mPrintDensity =pos;
                mGcAndroidPrint.GcSetDensity(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
		spinnerFontType = (Spinner) findViewById(R.id.spFontType);
		mFontType = GCAndroidPrint.GC_FONT_TYPE_GC_FONT_I;
		spinnerFontType.setSelection(mFontType);
		spinnerFontType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int pos, long id) {

				mFontType =pos;
				mGcAndroidPrint.GcSetFontType(pos);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Another interface callback
			}
		});
		spinnerFontSize = (Spinner) findViewById(R.id.spFontSize);
		mFontSz = GCAndroidPrint.GC_FONT_Small;
		spinnerFontSize.setSelection(0);
		spinnerFontSize.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int pos, long id) {

				switch(pos) {
					case 1:
                        mFontSz=GCAndroidPrint.GC_FONT_SmallBold;
						break;
					case 2:
                        mFontSz=GCAndroidPrint.GC_FONT_Medium;
						break;
					case 3:
                        mFontSz=GCAndroidPrint.GC_FONT_Big;
						break;
					case 4:
                        mFontSz=GCAndroidPrint.GC_FONT_DoubleHeight;
						break;
					case 5:
                        mFontSz=GCAndroidPrint.GC_FONT_DoubleWidth;
						break;
					default:
                        mFontSz=GCAndroidPrint.GC_FONT_Small;
						break;
				}

			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Another interface callback
			}
		});
    }


    class AlignGroupListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId){
            if (checkedId== mLeftAlign.getId()){
                mAlign =  GCAndroidPrint.GC_ALIGN_Left;
//              Log.v(TAG,"Left");
            }else if (checkedId== mCenterAlign.getId()){
                mAlign =  GCAndroidPrint.GC_ALIGN_Center;
//            	Log.v(TAG,"Center");
            }else if (checkedId== mRightAlign.getId()){
                mAlign =  GCAndroidPrint.GC_ALIGN_Right;
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
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case  GCAndroidPrint.GC_MSG_PAPER:
                    try {
                        defaultMediaPlayer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(TestGcPrinter.this, "No paper!", Toast.LENGTH_SHORT).show();
                    break;
                case GCAndroidPrint.GC_MSG_HOT:
                    try {
                        defaultMediaPlayer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(TestGcPrinter.this, "Too hot!", Toast.LENGTH_SHORT).show();
                    break;
                case GCAndroidPrint.GC_MSG_FINISH:
			Toast.makeText(TestGcPrinter.this, "Finish!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(TestGcPrinter.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TestGcPrinter.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    protected void onDestroy() {
        mGcAndroidPrint.GcPRNFree();
        super.onDestroy();
    }


    String path;
    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bmp=null;
                    path=null;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                        bmp=readBmpApiQ(this,uri);
                    }
                    else {
                        if ("file".equalsIgnoreCase(uri.getScheme())) {
                            path = uri.getPath();
                        }
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            path = getPath(this, uri);
                        } else {
                            path = getRealPathFromURI(uri);
                        }
                    }
                    if(path!=null) {
                        bmp = getLoacalBitmap(path);
                    }
                    if(bmp!=null) {
                        if(!mGcAndroidPrint.GcPrintBitmap(bmp, GCAndroidPrint.GC_ALIGN_Center, GCAndroidPrint.GC_BMP_COLOR_BITMAP, true)){
                            Toast.makeText(TestGcPrinter.this, "Busy ,please wait.", Toast.LENGTH_SHORT).show();
                        }
                        bmp.recycle();
                    }
                    else {
                        Toast.makeText(TestGcPrinter.this, "Fail to read picture file", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bmp=null;
                    path=null;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                        bmp=readBmpApiQ(this,uri);
                    }
                    else {
                        if ("file".equalsIgnoreCase(uri.getScheme())) {
                            path = uri.getPath();
                        }
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            path = getPath(this, uri);
                        } else {
                            path = getRealPathFromURI(uri);
                        }
                    }
                    if(path!=null) {
                        bmp = getLoacalBitmap(path);
                    }
                    if(bmp!=null) {
                        if (!mGcAndroidPrint.GcPrintBitmap(bmp)) {
                            Toast.makeText(TestGcPrinter.this, "Busy ,please wait.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(TestGcPrinter.this, "Fail to read picture file", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }

    }
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){
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
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

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
     * Android 10
     * @param context context
     * @param uri uri
     * @return bitmap
     */
    private static Bitmap readBmpApiQ(Context context, Uri uri) {

        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            try {
                FileInputStream fis = new FileInputStream(uri.getPath());
                return BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver contentResolver = context.getContentResolver();
            @SuppressLint("Recycle") Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                Bitmap bitmap=null;
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(is);
                } catch ( IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }
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
            if (cursor != null) {
                cursor.close();
            }
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