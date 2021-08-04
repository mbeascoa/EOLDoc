package com.beastek.eol.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.beastek.eol.Extras.Interface.DoctorFragmentToDoctorActivity;
import com.beastek.eol.R;


public class DoctorActivity extends AppCompatActivity implements DoctorFragmentToDoctorActivity,DoctorFragment.DocFragmentInteractionListener {

    public static final int ADD_DOCTOR_RESULT_CODE=10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.patient_doctors_title));
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState==null){
            Fragment fragment=new DoctorFragment();
            FragmentManager fragmentManager=getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_doclist,fragment).commit();
        }


    }
    @Override
    public void onDoctorItemClick(int position) {

        Intent intent=new Intent(DoctorActivity.this,DoctorDetailActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction() {
        Intent intent=new Intent(DoctorActivity.this,PatientAddDoctorActivity.class);
        startActivityForResult(intent,ADD_DOCTOR_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==ADD_DOCTOR_RESULT_CODE){
            if(resultCode==RESULT_OK){
                DoctorFragment docFragment=(DoctorFragment)getSupportFragmentManager().findFragmentById(R.id.frame_doclist);
                docFragment.updateDoctorList();
            }else if(resultCode==RESULT_CANCELED){

            }
        }
    }
}
