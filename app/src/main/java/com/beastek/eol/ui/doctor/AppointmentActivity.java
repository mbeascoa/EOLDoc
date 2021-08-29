package com.beastek.eol.ui.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.beastek.eol.Extras.Interface.AppointmentFragmentToAppointmentActivity;
import com.beastek.eol.R;


public class AppointmentActivity extends AppCompatActivity implements AppointmentFragmentToAppointmentActivity{


    private static final int APPOINTMENT_RESULT_CODE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.appointment_title));
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
  /*
  When your activity is recreated, such as after a screen rotation or other configuration change,
  fragments are automatically reattached. By checking if savedInstanceState == null, you ensure
  that you are not re-adding a fragment that has already been added for you.
   */
        if(savedInstanceState==null){
            Fragment fragment=new AppointmentFragment();
            FragmentManager fragmentManager=getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_appointments,fragment).commit();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAppointmentItemClick(int position) {
        Intent intent=new Intent(AppointmentActivity.this,AppointmentDetailActivity.class);
        intent.putExtra("position",position);
        startActivityForResult(intent,APPOINTMENT_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==APPOINTMENT_RESULT_CODE){
            if(resultCode==RESULT_OK){
                AppointmentFragment appointmentFragment=(AppointmentFragment)getSupportFragmentManager().findFragmentById(R.id.frame_appointments);
                appointmentFragment.updateAppointmentList();
            }else if(resultCode==RESULT_CANCELED){

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
