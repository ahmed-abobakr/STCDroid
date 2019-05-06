package com.example.stcdroid;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
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
import org.doubango.ngn.events.NgnInviteEventTypes;
import org.doubango.ngn.events.NgnMediaPluginEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnTimer;
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
    BroadcastReceiver broadcastReceiver;

    private static int mLastRotation;
    private final NgnTimer mTimerInCall = new NgnTimer();
    private final NgnTimer mTimerSuicide = new NgnTimer();
    private final NgnTimer mTimerBlankPacket = new NgnTimer();
    private final NgnTimer mTimerQoS = new NgnTimer();
    private PowerManager.WakeLock mWakeLock;

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


        mLastRotation = -1;

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
            if(avSession != null) {
                switch (avSession.getState()) {
                    case INCOMING:
                        showIncomingCall(avSession.getRemotePartyDisplayName());
                        break;
                    default:
                        showCallingUI(avSession.getRemotePartyDisplayName());
                }
            }

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

        broadcastReceiver = new CallReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
        intentFilter.addAction(NgnMediaPluginEventArgs.ACTION_MEDIA_PLUGIN_EVENT);
        registerReceiver(broadcastReceiver, intentFilter);
    }


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onStart() {
        super.onStart();

        final PowerManager powerManager = getPowerManager();
        if(powerManager != null && mWakeLock == null){
            mWakeLock = powerManager.newWakeLock(PowerManager.ON_AFTER_RELEASE | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "");
            if(mWakeLock != null){
                mWakeLock.acquire();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mWakeLock != null && mWakeLock.isHeld()){
            mWakeLock.release();
        }
    }

    private  PowerManager getPowerManager(){

            PowerManager sPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        return sPowerManager;
    }

    private void showCallingUI(String call){
        scrollMakeCall.setVisibility(View.GONE);
        relativeCalling.setVisibility(View.VISIBLE);
        relativeIncoming.setVisibility(View.GONE);

        txtCalling.setText("Calling: " + call);
        imgHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(avSession != null){
                    avSession.hangUpCall();
                    //avSession = null;
                    //finish();
                }
            }
        });
    }



    private void showInCall(String call){
        scrollMakeCall.setVisibility(View.GONE);
        relativeCalling.setVisibility(View.VISIBLE);
        relativeIncoming.setVisibility(View.GONE);

        txtCalling.setText("in call: " + call);
        imgHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(avSession != null){
                    avSession.hangUpCall();
                   // avSession = null;
                   // finish();
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
                    avSession.acceptCall();
                        //showCallingUI(call);
                   /* }else {
                        Toast.makeText(CallActivity.this, "we couldn't accept call", Toast.LENGTH_LONG).show();
                    }*/
                }
            }
        });

        imgCancelCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(avSession  != null){
                    avSession.hangUpCall();
                       // avSession = null;
                       // finish();
                    /*}else {
                        Toast.makeText(CallActivity.this, "we couldn't cancel call", Toast.LENGTH_LONG).show();
                    }*/
                }
            }
        });
    }

    public static void setAvSession(NgnAVSession ngnAVSession){
        avSession = ngnAVSession;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        avSession = null;
    }

    class CallReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(intent.getAction())){
                if (NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)) {
                    NgnInviteEventArgs args = intent.getParcelableExtra(NgnInviteEventArgs.EXTRA_EMBEDDED);
                    if (args == null) {
                        Log.e("TEST", "Invalid event args");
                        return;
                    }
                    if (args.getEventType() == NgnInviteEventTypes.TERMINATED) {
                        Toast.makeText(CallActivity.this, "Terminated1", Toast.LENGTH_LONG).show();
                        CallActivity.this.finish();
                        return;
                    }

                    switch (avSession.getState()){
                        case INCALL:
                            showInCall(avSession.getRemotePartyDisplayName());
                            //if(state == InviteState.INCALL){
                            // stop using the speaker (also done in ServiceManager())
                            //avSession.setSpeakerphoneOn(true);
                            if(avSession != null){
                                applyCamRotation(avSession.compensCamRotation(true));
                               // mTimerBlankPacket.schedule(mTimerTaskBlankPacket, 0, 250);
                                /*if(mIsVideoCall) {
                                    mTimerQoS.schedule(mTimerTaskQoS, 0, 3000);
                                }
                                else {*/
                                 //   mTimerInCall.schedule(mTimerTaskInCall, 0, 1000);
                              //  }
                                // release power lock if not video call
                                if(mWakeLock != null && mWakeLock.isHeld()){
                                    mWakeLock.release();
                                }
                            }
                            break;
                        case TERMINATED:
                            Toast.makeText(CallActivity.this, "Terminated2", Toast.LENGTH_LONG).show();
                            CallActivity.this.finish();
                            break;
                        case TERMINATING:
                            Toast.makeText(CallActivity.this, "Terminating", Toast.LENGTH_LONG).show();
                            CallActivity.this.finish();
                            break;
                    }
                }
            }
        }
    }

    private void applyCamRotation(int rotation){
        if(avSession != null){
            mLastRotation = rotation;
            // libYUV
            avSession.setRotation(rotation);

            // FFmpeg
			/*switch (rotation) {
				case 0:
				case 90:
					mAVSession.setRotation(rotation);
					mAVSession.setProducerFlipped(false);
					break;
				case 180:
					mAVSession.setRotation(0);
					mAVSession.setProducerFlipped(true);
					break;
				case 270:
					mAVSession.setRotation(90);
					mAVSession.setProducerFlipped(true);
					break;
				}*/
        }
    }
}
