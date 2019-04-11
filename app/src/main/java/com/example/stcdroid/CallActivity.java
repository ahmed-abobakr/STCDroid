package com.example.stcdroid;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnUriUtils;

public class CallActivity extends AppCompatActivity {

    EditText editCall;
    ImageView imgOne, imgTwo, imgThree, imgFour, imgFive, imgSix, imgSeven, imgEight, imgNine, imgZero, imgCall,
                imgHangUp, imgAcceptCall, imgCancelCall;
    TextView txtCalling, txtIncoming;
    ScrollView scrollMakeCall;
    RelativeLayout relativeCalling, relativeIncoming;


    NgnEngine mEngine;
    CallStateReceiver callStateReceiver;
    static NgnAVSession avSession;

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
        imgHangUp = findViewById(R.id.ic_hangup);
        imgAcceptCall = findViewById(R.id.img_accept);
        imgCancelCall = findViewById(R.id.img_cancel);
        txtCalling = findViewById(R.id.txtCalling);
        txtIncoming = findViewById(R.id.text_incoming);
        scrollMakeCall = findViewById(R.id.relative_make_call);
        relativeCalling = findViewById(R.id.relative_calling);
        relativeIncoming = findViewById(R.id.relative_incoming);


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

        if(getIntent().getBooleanExtra("incoming", false)){
            showIncomingCall(avSession.getRemotePartyDisplayName());
        }

        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mEngine = NgnEngine.getInstance();
                    final INgnSipService sipService = mEngine.getSipService();
                    final String validUri = NgnUriUtils.makeValidSipUri(editCall.getText().toString());
                    avSession = NgnAVSession.createOutgoingSession(sipService.getSipStack(), NgnMediaType.Audio);
                    avSession.setContext(CallActivity.this);
                    if (avSession.makeCall(validUri)) {
                        Log.d("TEST", "all is ok");
                        Toast.makeText(CallActivity.this, "all is ok : " + validUri, Toast.LENGTH_SHORT).show();
                        showCallingUI(editCall.getText().toString());
                    } else {
                        Log.e("TEST", "Failed to place the call");
                        Toast.makeText(CallActivity.this, "Failed to place the call", Toast.LENGTH_LONG).show();
                    }

            }
        });
    }


    private void showCallingUI(String call){
        scrollMakeCall.setVisibility(View.GONE);
        relativeCalling.setVisibility(View.VISIBLE);
        relativeIncoming.setVisibility(View.GONE);

        txtCalling.setText(call);
        imgHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(avSession != null){
                    avSession.hangUpCall();
                    avSession = null;
                    finish();
                }
            }
        });
    }


    private void showIncomingCall(final String call){
        scrollMakeCall.setVisibility(View.GONE);
        relativeCalling.setVisibility(View.GONE);
        relativeIncoming.setVisibility(View.VISIBLE);

        txtIncoming.setText(call);
        imgAcceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(avSession != null){
                    if(avSession.acceptCall()) {
                        showCallingUI(call);
                    }else {
                        Toast.makeText(CallActivity.this, "we couldn't accept call", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        imgCancelCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(avSession  != null){
                    if(avSession.hangUpCall()) {
                        avSession = null;
                        finish();
                    }else {
                        Toast.makeText(CallActivity.this, "we couldn't cancel call", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public static void setAvSession(NgnAVSession ngnAVSession){
        avSession = ngnAVSession;
    }
}
