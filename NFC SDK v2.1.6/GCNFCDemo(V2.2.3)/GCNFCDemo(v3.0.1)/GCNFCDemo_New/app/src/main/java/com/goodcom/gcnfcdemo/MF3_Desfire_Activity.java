package com.goodcom.gcnfcdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.goodcom.gcnfcsdk.GCAndroidNFC;
import com.goodcom.gcnfcsdk.GcDESFireCard;

public class MF3_Desfire_Activity extends Activity {
    private final String TAG="[Des]";
    private Button btn_read;
    private Button btn_write;
    private Button btn_format;
    private Button btn_clear;
    private EditText text1;
    private EditText text2;
    private TextView tipView;

    private GCAndroidNFC mGcAndroidNFC;
    private byte mOperateType;
    private final byte OPERATE_WRITE = 0;
    private final byte OPERATE_READ  = 1;
    private final byte OPERATE_FORMAT = 2;

    private  boolean bCreatApp=false;
    private  int wFileNo=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mf3_desfire);
        Log.d(TAG,"onCreate");

        btn_read = (Button) findViewById(R.id.read_btn);
        btn_write = (Button) findViewById(R.id.write_btn);
        btn_format = (Button) findViewById(R.id.format_btn);
        btn_clear = (Button) findViewById(R.id.clear_btn);

        text1 = (EditText) findViewById(R.id.editText1);
        text2 = (EditText) findViewById(R.id.editText2);
        tipView = (TextView) findViewById(R.id.tip_view);

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReadNfc(OPERATE_WRITE);
            }
        });

        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReadNfc(OPERATE_READ);
            }
        });

        btn_format.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReadNfc(OPERATE_FORMAT);
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text1.setText(null);
                text2.setText(null);
                stopReadNfc();
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopReadNfc();
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    void startReadNfc(byte operateType){
        if (mGcAndroidNFC == null) {
            bCreatApp=false;
            wFileNo=0;
            mOperateType=operateType;
            mGcAndroidNFC = new GCAndroidNFC();
            mGcAndroidNFC.RegisterNFCHandler(mHandler);
            tipView.setText("NFC is opening...\r\nPlease put the NFC card close to the machine");
            tipView.setVisibility(View.VISIBLE);

            btn_write.setEnabled(false);
            btn_read.setEnabled(false);
            btn_format.setEnabled(false);
        }
    }

    void stopReadNfc(){
        tipView.setVisibility(View.GONE);
        if (mGcAndroidNFC != null) {
            mGcAndroidNFC.free();
            mGcAndroidNFC=null;
        }
        btn_write.setEnabled(true);
        btn_read.setEnabled(true);
        btn_format.setEnabled(true);
    }

    void tipNfcResult(String tip){
        tipView.setText(tip);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_F10) {
            if (mGcAndroidNFC != null) {
                if (mGcAndroidNFC.GetICCReady()) {
                    Log.v("idv", "Press down");
                    mGcAndroidNFC.GC_ReadICC();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case GCAndroidNFC.GC_MSG_ERROR:
                {
                    if (msg.obj != null) {
                        String e = (String)msg.obj;
                        tipNfcResult(e);
                        Log.e(TAG,"Error:"+e);
                    }
                }
                break;
                case  GCAndroidNFC.GC_MSG_NFC_WAIT:
                {
                }
                break;
                case  GCAndroidNFC.GC_MSG_NFC_READY:
                {
                    Log.v(TAG, "GC_MSG_NFC_READY");
//                    char [] serial_no = (char[]) msg.obj;
//                    String displayString = "Serial Number:";
//                    displayString += String.valueOf(serial_no);
                    mGcAndroidNFC.SendRATS();
                }
                break;

                case GCAndroidNFC.GC_MSG_RATS_RETURN: {
                    boolean ret=(boolean)msg.obj;
                    Log.d(TAG,"Rats ret:"+ret);
                    if (ret) {
                        char[] aid=new char[3];
                        aid[2]=0x00;
                        if (mOperateType == OPERATE_READ) {
                            aid[2]=0x01; //When reading the card, directly read the application (AID is 010000)
                        }
                        char[] defaultPwd =new char[16];
                        mGcAndroidNFC.GcDesFireFun().MF3_AuthPsw(aid, 0, defaultPwd);
                    }
                    else{
                        tipNfcResult("Card Type Error!!");
                    }
                }
                break;

                case GCAndroidNFC.GC_MSG_MF3_DESFIRE_RETURN:{
                    if (msg.arg2 == 1){
                        switch (msg.arg1){
                            case GcDESFireCard.MF3_STEP_RET_AUTH: {
                                Log.d(TAG,"Auth is OK:"+mOperateType);
                                if (mOperateType == OPERATE_READ){
                                    wFileNo=1;
                                    mGcAndroidNFC.GcDesFireFun().MF3_ReadFile(wFileNo,0,15);
                                }
                                else if (mOperateType == OPERATE_FORMAT){
                                    mGcAndroidNFC.GcDesFireFun().MF3_Format();
                                }
                                else {
                                    if (bCreatApp == false) { //For the initial card, first create an application
                                        char[] aid = new char[3];  //This AID is 010000
                                        aid[2] = 0x01;
                                        mGcAndroidNFC.GcDesFireFun().MF3_Create_Application(aid, 0x0F, 1);
                                    } else {
                                        char[] content = {0x00, 0x0F, 0x20, 0x00, 0x38, 0x00, 0x34, 0x04, 0x06, 0xE1, 0x04, 0x10};
                                        wFileNo = 1;  //The first to write a file 1
                                        mGcAndroidNFC.GcDesFireFun().MF3_Create_AndWriteStdFile(wFileNo, content, GcDESFireCard.DES_COMMIT_TYPE_NO, 0x0000, 15);
                                    }
                                }
                            }
                            break;
                            case GcDESFireCard.MF3_STEP_RET_CREATE_APP:{
                                Log.d(TAG,"create app is OK:");
                                char[] aid=new char[3];
                                aid[2]=0x01;
                                char[] defaultPwd =new char[16];
                                bCreatApp=true;
                                mGcAndroidNFC.GcDesFireFun().MF3_AuthPsw(aid, 0, defaultPwd);
                            }
                            break;
                            case GcDESFireCard.MF3_STEP_RET_WRITE_FILE:{
                                Log.d(TAG,"Write is OK");
                                if (wFileNo == 1) {
                                    wFileNo = 2;  //Write file2 after completion
                                    String content = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\rabcdefghijklmnopqrstuvwxyz\r0123456789\rABCDEFGHIJKLMNOPQRSTUVWXYZ\n" +
                                            "abcdefghijklmnopqrstuvwxyz\n" +
                                            "0123456789";
                                    mGcAndroidNFC.GcDesFireFun().MF3_Create_AndWriteStdFile(wFileNo, content.toCharArray(), GcDESFireCard.DES_COMMIT_TYPE_NO, 0x0000, 256);
                                }
                                else{
                                    //success
                                    tipNfcResult("Write is success!");
                                }
                            }
                            break;
                            case GcDESFireCard.MF3_STEP_RET_READ_FILE:{
                                char[] readData=(char[])msg.obj;
                                if (wFileNo == 1){
                                    wFileNo = 2;
                                    mGcAndroidNFC.GcDesFireFun().MF3_ReadFile(wFileNo,0,128);
                                    if (readData != null) {
                                        text1.setText(readData,0,readData.length);
                                    }
                                }
                                else {
                                    if (readData != null) {
                                        Log.d(TAG, "Read is OK:" + String.valueOf(readData));
                                        tipNfcResult("Read is success!");
                                        text2.setText(readData,0,readData.length);
                                    }
                                }
                            }
                            break;
                            case GcDESFireCard.MF3_STEP_RET_FORMAT:
                                Log.d(TAG,"Format is success");
                                tipNfcResult("Format is success!!!");
                                break;
                        }
                    }
                    else{  //error
                        Log.d(TAG,"Error");
                        tipNfcResult("ERROR!!!!!!");
                    }
                }
                break;
                default:
                    break;
            }
        }
    };
}
