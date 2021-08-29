package com.beastek.eol.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.beastek.eol.ui.doctor.DoctorMainActivity;
import com.beastek.eol.ui.patient.PatientMainActivity;

import java.util.HashMap;

// esta es la primera actividad que se realiza, lee las preferencias, y mira si es la primera vez
//que se ejecuta , no tiene asociado un layout. Si nunca se ha ejecutado va a HomeActivity


public class LauncherActivity extends Activity
{


    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences("prefs",0);
        boolean firstRun = settings.getBoolean("firstRun",false);



        if(firstRun == false)//if running for second, third, ....etc,  time, si no es la primera vez lo ejecuta
        {
            SharedPreferences.Editor editor=settings.edit();
            editor.putBoolean("firstRun",true);
            editor.commit();
            Intent i = new Intent(LauncherActivity.this,HomeActivity.class);
            startActivity(i);
            finish();
        }
        else //if running for first time
        {
            //setContentView(R.layout.home);
            // Session class instance
            session = new SessionManager(getApplicationContext());
            //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
            session.checkLogin();

            HashMap<String, String> user = session.getUserDetails();
            String Username = user.get(SessionManager.KEY_NAME);

            System.out.println("Session ID: "+SessionManager.KEY_ID);
            String userID = user.get(SessionManager.KEY_ID);
            int ID = 0;
            ID = Integer.parseInt(userID);


            String type;
            type = user.get(SessionManager.KEY_TYPE);


            System.out.println("username:" +Username);
            System.out.println("userID string:" +userID);
            System.out.println("userID int:" +ID);

            if(type != null && type.equals("Doctor"))
            {
                // go to doctor dashboard  DoctorMainActivity.class
                System.out.println("Goto DoctorMainActivity");
                Intent doc_intent=new Intent(LauncherActivity.this,DoctorMainActivity.class);
                startActivity(doc_intent);
            }
            else if (type != null && type.equals("Patient"))
            {
                //go to patient dasboard PatientMainActivity.class
                System.out.println("Goto PatientMainActivity");
                Intent pat_intent=new Intent(LauncherActivity.this,PatientMainActivity.class);
                startActivity(pat_intent);
            }
            else
            {
                Intent no_intent=new Intent(LauncherActivity.this,LoginActivity.class);
                startActivity(no_intent);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClicklogout(View view)
    {
        // Clear the session data
        // This will clear all session data and
        // redirect user to LoginActivity
        session.logoutUser();
    }


}
