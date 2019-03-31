package com.example.stcdroid;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnUriUtils;

public class CallActivity extends AppCompatActivity {

    EditText editCall;
    ImageView imgOne, imgTwo, imgThree, imgFour, imgFive, imgSix, imgSeven, imgEight, imgNine, imgZero, imgCall;


    NgnEngine mEngine;
    CallStateReceiver callStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        editCall = findViewById(R.id.activity_call_editText_dtmf);
        imgOne = findViewById(R.id.activity_call_imageview_num1);
        imgTwo = findViewById(R.id.activity_call_imageview_num2);
        imgThree = findViewById(R.id.activity_call_imageview_num3);
        imgFour = findViewById(R.id.activity_call_imageview_num4);
        imgFive = findViewById(R.id.activity_call_imageview_num5);
        imgSix  = findViewById(R.id.activity_call_imageview_num6);
        imgSeven = findViewById(R.id.activity_call_imageview_num7);
        imgEight = findViewById(R.id.activity_call_imageview_num8);
        imgNine = findViewById(R.id.activity_call_imageview_num9);
        imgZero = findViewById(R.id.activity_call_imageview_num0);
        imgCall = findViewById(R.id.activity_call_imageview_hangkeypad);


        imgOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCall.setText(editCall.getText().toString() + "1");
            }
        });

        imgTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCall.setText(editCall.getText().toString() + "2");
            }
        });
        imgThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCall.setText(editCall.getText().toString() + "3");
            }
        });
        imgFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCall.setText(editCall.getText().toString() + "4");
            }
        });
        imgFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCall.setText(editCall.getText().toString() + "5");
            }
        });
        imgSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCall.setText(editCall.getText().toString() + "6");
            }
        });
        imgSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCall.setText(editCall.getText().toString() + "7");
            }
        });
        imgEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCall.setText(editCall.getText().toString() + "8");
            }
        });
        imgNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCall.setText(editCall.getText().toString() + "9");
            }
        });
        imgZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCall.setText(editCall.getText().toString() + "0");
            }
        });


        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getIntent().getBooleanExtra("incoming", false)) {

                }else {
                    mEngine = NgnEngine.getInstance();
                    final INgnSipService sipService = mEngine.getSipService();
                    final String validUri = NgnUriUtils.makeValidSipUri(editCall.getText().toString());
                    NgnAVSession avSession = NgnAVSession.createOutgoingSession(sipService.getSipStack(), NgnMediaType.Audio);
                    if (avSession.makeCall(validUri)) {
                        Log.d("TEST", "all is ok");
                        Toast.makeText(CallActivity.this, "all is ok", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("TEST", "Failed to place the call");
                        Toast.makeText(CallActivity.this, "Failed to place the call", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
