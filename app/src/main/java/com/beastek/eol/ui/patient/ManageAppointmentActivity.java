package com.beastek.eol.ui.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beastek.eol.Extras.Interface.RestCallbackHandler;
import com.beastek.eol.R;
import com.beastek.eol.model.Appointment;
import com.beastek.eol.util.JSONUtil;
import com.beastek.eol.utility.ConfigConstant;

import org.json.JSONArray;
import org.json.JSONObject;

public class ManageAppointmentActivity extends Activity implements RestCallbackHandler {
    String mPatientId;

    final String getAPTRestCallId = "GET APPOINTMENT LIST";
    final String createAPTRestCallId = "POST APPOINTMENT";
    final String updateAPTRestCallId = "PUT APPOINTMENT";
    final String deleteAPTRestCallId = "DELETE APPOINTMENT";

    private Appointment mAppointment;
    private Appointment mAppointmentUnsaved;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = this.getIntent();
        mPatientId = intent.getStringExtra("PatientId");
        String url = ConfigConstant.BASE_URL + "/patient/appointment/" + mPatientId;
        EolRestClient client = new EolRestClient(getAPTRestCallId, url, EolRestClient.GET, "", this);
        client.execute();
    }

    public void addInitialAppoitnment(View v){
        setContentView(R.layout.activity_addappointment);
    }
    public void backFromAppointmentinitial(View v){
        this.finish();
    }
    public void backfromAppointmentList (View v){
        this.finish();
    }
    public void backFromAddAppointment(View V){
        //TODO: go to main activity of patient
        //setContentView(R.layout.activity_sidemenu);
    }
    public void launchAddAppointmentView(View V) {setContentView(R.layout.activity_addappointment); }


    private void populateAppointmentLayoutEditTextValues(Appointment a){
        EditText appointmentEditText = (EditText) findViewById(R.id.apt1ET);
        appointmentEditText.setText(a.getDescription() +" with " +a.getDoctorId() + " at " + a.getAppointmentDate());
    }


    public void addAppointment(View v){
        EditText descriptionEditText = (EditText) findViewById(R.id.descriptionET);
        EditText dateEditText = (EditText) findViewById(R.id.aptdateET);
        EditText doctorEditText = (EditText) findViewById(R.id.aptdoctorET);
        Button AddAptButton = (Button) findViewById(R.id.addAptBtn);

        String description = descriptionEditText.getText().toString();
        String appointmentDate = dateEditText.getText().toString();
        String status = "Requested";
        String doctorId = doctorEditText.getText().toString();

        mAppointmentUnsaved = new Appointment(description, appointmentDate, status, Integer.valueOf(mPatientId), Integer.valueOf(doctorId));
        JSONObject body = JSONUtil.convertAppointmentToJSON(mAppointmentUnsaved, mPatientId);
        String url = ConfigConstant.BASE_URL + "/patient/appointment/add" ;
        EolRestClient client = new EolRestClient(createAPTRestCallId, url, EolRestClient.POST, body.toString(), this);
        client.execute();
        AddAptButton.setEnabled(false);
        AddAptButton.setBackgroundColor(getResources().getColor(R.color.colorGrey));

    }

    public void deleteAppointment(View v){
        String url = ConfigConstant.BASE_URL + "/patient/appointment/delete/" + mPatientId + "/" + mAppointment.getId();
        String body = "";
        EolRestClient client = new EolRestClient(deleteAPTRestCallId, url, EolRestClient.DELETE, "", this);
        client.execute();
    }


    @Override
    public void handleResponse(EolRestClient client) {
        try {
            if(client.getId().equals(getAPTRestCallId)) {
                if (client.getResponseCode() == 200) {
                    JSONArray response = new JSONArray(client.getResponseBody());


                    System.out.print(response.length());
                    if (response.length() == 0 ) {
                        setContentView(R.layout.activity_appointmentinitial);
                    } else {

                        JSONObject responseObject = response.getJSONObject(0);
                        setContentView(R.layout.activity_appointmentlist);
                        mAppointment = JSONUtil.parseAppointmentFromJSON(responseObject);
                        populateAppointmentLayoutEditTextValues(mAppointment);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again !", Toast.LENGTH_LONG).show();
                }
            } else if (client.getId().equals(createAPTRestCallId)){
                if (client.getResponseCode() == 200) {
                    JSONObject body = new JSONObject(client.getResponseBody());
                    int appointmentId = Integer.valueOf(body.getString("AppointmentId"));
                    mAppointment = new Appointment(appointmentId, mAppointmentUnsaved.getDescription(),
                            mAppointmentUnsaved.getAppointmentDate(), mAppointmentUnsaved.getAppointmentStatus(),
                            mAppointmentUnsaved.getPatientId(), mAppointmentUnsaved.getDoctorId());
                    mAppointmentUnsaved = null;
                    // make toast about successful creation
                    Toast.makeText(getApplicationContext(), "Appointment created successfully!", Toast.LENGTH_LONG).show();

                } else {
                    // make toast about server error !
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again!", Toast.LENGTH_LONG).show();
                }
            } else if (client.getId().equals(updateAPTRestCallId)) {
                if (client.getResponseCode() == 200) {
                    JSONObject body = new JSONObject(client.getResponseBody());
                    //this will be just like post except that we already have the appointment id;
                    mAppointment = new Appointment(mAppointment.getId(), mAppointmentUnsaved.getDescription(),
                            mAppointmentUnsaved.getAppointmentDate(), mAppointmentUnsaved.getAppointmentStatus(),
                            mAppointmentUnsaved.getPatientId(), mAppointmentUnsaved.getDoctorId());
                    Toast.makeText(getApplicationContext(), "Appointment updated successfully!", Toast.LENGTH_LONG).show();
                    mAppointment = null;
                    Thread.sleep(1000);
                    backfromAppointmentList(null);
                } else {
                    // make toast about server error !
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again!", Toast.LENGTH_LONG).show();
                }
            } else if (client.getId().equals(deleteAPTRestCallId)) {
                if (client.getResponseCode() == 200) {
                    // make toast about successful deleteion
                    Toast.makeText(getApplicationContext(), "Appointment deleted successfully!", Toast.LENGTH_LONG).show();
                    mAppointment = null;
                    Thread.sleep(1000);
                    backFromAddAppointment(null);

                } else {
                    // make toast about server error !
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
