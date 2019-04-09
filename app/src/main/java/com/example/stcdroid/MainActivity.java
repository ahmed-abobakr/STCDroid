package com.example.stcdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

public class MainActivity extends AppCompatActivity {


    private NgnEngine mEngine;
    private INgnSipService mSipService;
    private RegistrationBroadcastReceiver regBroadcastReceiver;
    private CallStateReceiver callStateReceiver;


    EditText editImpu, editImpi, editName, editPassword, editRealm, editHost;
    TextView txtStatus;
    Button btnRegister, btnCall;

    private BroadcastReceiver mSipBroadCastRecv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);



        txtStatus = findViewById(R.id.txt_status_value);
        editImpi = findViewById(R.id.edit_impi);
        editImpu = findViewById(R.id.edit_impu);
        editName = findViewById(R.id.edit_name);
        editPassword = findViewById(R.id.edit_password);
        editRealm = findViewById(R.id.edit_realm);
        editHost = findViewById(R.id.edit_host);
        btnRegister = findViewById(R.id.btn_register);
        btnCall = findViewById(R.id.btn_call);

        mEngine = NgnEngine.getInstance();
        mSipService = mEngine.getSipService();



        regBroadcastReceiver = new RegistrationBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        registerReceiver(regBroadcastReceiver, intentFilter);


        final IntentFilter intentRFilter = new IntentFilter();
        callStateReceiver = new CallStateReceiver();
        intentRFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
        registerReceiver(callStateReceiver, intentRFilter);


        NgnEngine mEngine = NgnEngine.getInstance();
        INgnConfigurationService mConfigurationService
                = mEngine.getConfigurationService();

        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM,
                "ims.stc.com.sa");
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST,
                "10.247.69.97");


        String display_name = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME,
                NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME),
                public_identity = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPU,
                        NgnConfigurationEntry.DEFAULT_IDENTITY_IMPU),
                private_identity = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI,
                        NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI),
                password = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_PASSWORD,
                        NgnConfigurationEntry.DEFAULT_IDENTITY_PASSWORD),
                realm = mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM,
                        NgnConfigurationEntry.DEFAULT_NETWORK_REALM),
                host = mConfigurationService.getString(NgnConfigurationEntry.NETWORK_PCSCF_HOST,
                        NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_HOST);

        editImpu.setText(public_identity);
        editImpi.setText(private_identity);
        editName.setText(display_name);
        editPassword.setText(password);
        editRealm.setText(realm);
        editHost.setText(host);




        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI,
                editImpi.getText().toString());
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU,
                editImpu.getText().toString());
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD,
                editPassword.getText().toString());
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST,
                editHost.getText().toString());
        /*mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,
                "sip_server_port");*/
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM,
                editRealm.getText().toString());
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME,
                editName.getText().toString());
// By default, using 3G for calls disabled

        mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G,
                true);
// You may want to leave the registration timeout to the default 1700 seconds

        mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT,
                3600);
        mConfigurationService.commit();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeManager();
            }
        });

        mSipBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                // Registration Event
                if(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
                    NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                    if(args == null){
                        Log.e("DEBUG", "Invalid event args");
                        return;
                    }
                    switch(args.getEventType()){
                        case REGISTRATION_NOK:
                            Log.d("DEBUG", "Failed to register :(");
                            txtStatus.setText("Failed to register :");
                            break;
                        case UNREGISTRATION_OK:
                            Log.d("DEBUG", "You are now unregistered :)");
                            txtStatus.setText("You are now unregistered :)");
                            break;
                        case REGISTRATION_OK:
                            Log.d("DEBUG", "You are now registered :)");
                            txtStatus.setText("You are now registered :)");
                            break;
                        case REGISTRATION_INPROGRESS:
                            Log.d("DEBUG", "Trying to register...");
                            txtStatus.setText("Trying to register...");
                            break;
                        case UNREGISTRATION_INPROGRESS:
                            Log.d("DEBUG", "Trying to unregister...");
                            txtStatus.setText("Trying to unregister...");
                            break;
                        case UNREGISTRATION_NOK:
                            Log.d("DEBUG", "Failed to unregister :(");
                            break;
                    }
                }
            }
        };
        final IntentFilter intentF = new IntentFilter();
        intentF.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        registerReceiver(mSipBroadCastRecv, intentF);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CallActivity.class));
            }
        });

        if(!mEngine.isStarted()){
            if(!mEngine.start()){
                Log.e("DEBUG", "Failed to start the engine :(");
                return;
            }
        }
    }


    public void initializeManager() {


        // Register

        if(!mSipService.isRegistered()){
            mSipService.register(this);
        }
    }
}
