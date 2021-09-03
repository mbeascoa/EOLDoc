package com.beastek.eol.Extras.fitbit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.beastek.eol.R;
import com.beastek.eol.ui.SessionManager;
import com.beastek.eol.ui.patient.FitbitActivity;
import com.beastek.eol.utility.FitbitReferences;

//lee los datos recibidos de oauth2 de fitbit, https://dev.fitbit.com/apps/oauthinteractivetutorial?
// clientEncodedId=23BF7P&clientSecret=4d4ea6fd400198a2ce5784f49e6d66da&redirectUri=
// https://finish&applicationType=CLIENT
public class FitbitTokenResponse extends AppCompatActivity {


    String respStrUrl;
    String LOG_TAG=FitbitTokenResponse.class.getSimpleName();

    @Override
    protected void onNewIntent(Intent intent) {
        respStrUrl = intent.getDataString();
        // this line was suggested by the editor
        super.onNewIntent(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onNewIntent(getIntent());

        Log.d(LOG_TAG,respStrUrl);
        String token=respStrUrl.substring(respStrUrl.indexOf(getResources().getString(R.string.fitbit_token))+14,respStrUrl.indexOf(getResources().getString(R.string.fitbit_uid)));
        Log.d(LOG_TAG,token);
        String user_id=respStrUrl.substring(respStrUrl.indexOf(getResources().getString(R.string.fitbit_uid))+9,respStrUrl.indexOf(getResources().getString(R.string.fibit_scope)));
        Log.d(LOG_TAG,user_id);
        String token_type=respStrUrl.substring(respStrUrl.indexOf(getResources().getString(R.string.fitbit_token_type))+12,respStrUrl.indexOf(getResources().getString(R.string.fitbit_token_expiry)));
        Log.d(LOG_TAG,token_type);

        SessionManager sessionManager=new SessionManager(FitbitTokenResponse.this);
        sessionManager.createFitbitSession(true,user_id,token,token_type,token_type+" "+token);

        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(FitbitReferences.HAS_ACCESS_TOKEN,true).commit();
        sp.edit().putString(FitbitReferences.FITBIT_UID,user_id).commit();
        sp.edit().putString(FitbitReferences.FITBIT_TOKEN,token).commit();
        sp.edit().putString(FitbitReferences.FITBIT_TOKEN_TYPE,token_type).commit();
        sp.edit().putString(FitbitReferences.FITBIT_FULL_AUTH,token_type+" "+token).commit();

        /*
        editor.putBoolean(HAS_ACCESS_TOKEN,f1);

        editor.putString(FITBIT_UID, f2);
        editor.putString(FITBIT_TOKEN,f3);
        editor.putString(FITBIT_TOKEN_TYPE,f4);
        editor.putString(FITBIT_FULL_AUTH,f5);  */

        Intent intent=new Intent(FitbitTokenResponse.this,FitbitActivity.class);
        startActivity(intent);


    }
}
