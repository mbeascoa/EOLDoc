package com.beastek.eol.ui.patient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beastek.eol.Extras.Interface.RestCallbackHandler;
import com.beastek.eol.R;
import com.beastek.eol.model.EmergencyContact;
import com.beastek.eol.util.JSONUtil;
import com.beastek.eol.utility.ConfigConstant;

import org.json.JSONObject;

import java.net.URL;


public class ManageEmergencyContactActivity extends Activity implements RestCallbackHandler {
    String mPatientId;

    final String baseURL = ConfigConstant.BASE_URL;
    final String getECRestCallId = "GET EMERGENCY CONTACT";
    final String createECRestCallId = "POST EMERGENCY CONTACT";
    final String updateECRestCallId = "PUT EMERGENCY CONTACT";
    final String deleteECRestCallId = "DELETE EMERGENCY CONTACT";

    private EmergencyContact mEmergencyContact;
    private EmergencyContact mEmergencyContactUnsaved;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = this.getIntent();
        mPatientId = intent.getStringExtra("PatientId");

        String url = ConfigConstant.BASE_URL + "/search?sheet=emergencycontact&P_ID=" + mPatientId + "&single_object=true";
        //https://sheetdb.io/api/v1/ahhtehepl6e9f/search?sheet=emergencycontact&P_ID=1&single_object=true  selecciona el contacto
        //de emergencia del paciente 1
        System.out.println("Url para obtener el contacto de emergencia de este paciente " + url);
        EolRestClient client = new EolRestClient(getECRestCallId, url, EolRestClient.GET, "", this);
        System.out.println("Before Starting client connection");
        client.execute();
    }

    // ADDS AN INITIAL EMERGENCY CONTACT when pressing the right button it goes to the blue form created for adding an emergency contact
    public void addInitialEmergencyContact(View v) {
        //goes to blue form
        setContentView(R.layout.activity_addemergencycontact);
    }

    public void backFromECinitial(View v) {
        this.finish();
    }

    public void backFromEC(View v) {
        this.finish();
    }

    public void backFromAddEC(View V) {
        this.finish();
        //setContentView(R.layout.activity_sidemenu);
    }

    private void populateECLayoutEditTextValues(EmergencyContact ec) {
        EditText firstnameEditText = (EditText) findViewById(R.id.firstnameEditText);
        EditText lastnameEditText = (EditText) findViewById(R.id.lastnameEditText);
        EditText emailIdEditText = (EditText) findViewById(R.id.emailIdEditText);
        EditText contactEditText = (EditText) findViewById(R.id.contactEditText);
        EditText addressEditText = (EditText) findViewById(R.id.addressEditText);
        EditText relationEditText = (EditText) findViewById(R.id.relationEditText);

        firstnameEditText.setText(ec.getFirstname());
        lastnameEditText.setText(ec.getLastname());
        emailIdEditText.setText(ec.getEmailId());
        contactEditText.setText(ec.getContact());
        addressEditText.setText(ec.getAddress());
        relationEditText.setText(ec.getRelation());
    }

    // when SAVE button is pressed ....
    public void saveEmergencyContact(View v) {
        EditText firstnameEditText = (EditText) findViewById(R.id.firstnameEditText);
        EditText lastnameEditText = (EditText) findViewById(R.id.lastnameEditText);
        EditText emailIdEditText = (EditText) findViewById(R.id.emailIdEditText);
        EditText contactEditText = (EditText) findViewById(R.id.contactEditText);
        EditText addressEditText = (EditText) findViewById(R.id.addressEditText);
        EditText relationEditText = (EditText) findViewById(R.id.relationEditText);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setBackgroundColor(getResources().getColor(R.color.colorGreyDark));
        saveButton.setEnabled(false);

        String firstname = firstnameEditText.getText().toString();
        String lastname = lastnameEditText.getText().toString();
        String emailId = emailIdEditText.getText().toString();
        String contact = contactEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String relation = relationEditText.getText().toString();


        mEmergencyContactUnsaved = new EmergencyContact(mEmergencyContact.getId(), firstname, lastname, emailId, contact, address, relation);
        JSONObject body = JSONUtil.convertEmergencyContactToJSONUpdate(mEmergencyContactUnsaved, mPatientId);
        String url = ConfigConstant.BASE_URL + "/P_ID/" + mPatientId + "?sheet=emergencycontact";
        //https://sheetdb.io/api/v1/ahhtehepl6e9f/P_ID/1?sheet=emergencycontact
        // response 200 OK, updated 1
        EolRestClient client = new EolRestClient(updateECRestCallId, url, EolRestClient.PUT, body.toString(), this);
        client.execute();
    }

    // when the blue AddEmergencyContact form is created and SAVE button is pressed
    public void addEmergencyContact(View v) {
        EditText firstnameEditText = (EditText) findViewById(R.id.firstnameET);
        EditText lastnameEditText = (EditText) findViewById(R.id.lastnameET);
        EditText emailIdEditText = (EditText) findViewById(R.id.emailIdET);
        EditText contactEditText = (EditText) findViewById(R.id.contactET);
        EditText addressEditText = (EditText) findViewById(R.id.addressET);
        EditText relationEditText = (EditText) findViewById(R.id.relationET);

        String firstname = firstnameEditText.getText().toString();
        String lastname = lastnameEditText.getText().toString();
        String emailId = emailIdEditText.getText().toString();
        String contact = contactEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String relation = relationEditText.getText().toString();

        mEmergencyContactUnsaved = new EmergencyContact(firstname, lastname, emailId, contact, address, relation);
        JSONObject body = JSONUtil.convertEmergencyContactToJSON(mEmergencyContactUnsaved, mPatientId);
        System.out.println("Esta es la cadena que pasa el Objeto EC  a JSON" +body.toString());
        String url = ConfigConstant.BASE_URL + "?sheet=emergencycontact";
        EolRestClient client = new EolRestClient(createECRestCallId, url, EolRestClient.POST, body.toString(), this);
        client.execute();
    }

    public void deleteEmergencyContact(View v) {
        //String url = ConfigConstant.BASE_URL + "/P_ID/"+ mEmergencyContact.getId()+ "?sheet=emergencycontact" ;
        String url = ConfigConstant.BASE_URL + "/P_ID/" + mPatientId + "?sheet=emergencycontact";
        //https://sheetdb.io/api/v1/ahhtehepl6e9f/P_ID/1?sheet=emergencycontact
        String body = "";
        EolRestClient client = new EolRestClient(deleteECRestCallId, url, EolRestClient.DELETE, "", this);
        client.execute();
    }


    @Override
    public void handleResponse(EolRestClient client) {
        try {
            if (client.getId().equals(getECRestCallId)) {
                if (client.getResponseCode() == 200) {
                    String procesar = client.getResponseBody();
                    if (procesar.contains("[]")) {
                        System.out.println("no Emergency Contact EC returned");
                        setContentView(R.layout.activity_emergencycontactinitial);
                        ImageView logo = (ImageView) findViewById(R.id.imageView4);
                        logo.setImageResource(R.drawable.img2);
                    } else {
                        JSONObject response = new JSONObject(client.getResponseBody());
                        System.out.println("Emergency Contect EC  is returned" + response);
                        setContentView(R.layout.activity_emergencycontact);
                        mEmergencyContact = JSONUtil.parseEmergencyContactFromJSON(response);
                        populateECLayoutEditTextValues(mEmergencyContact);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again !", Toast.LENGTH_LONG).show();
                }
            } else if (client.getId().equals(createECRestCallId)) {
                if (client.getResponseCode() == 201) {
                    // if (client.getResponseCode() == 200) {
                    //JSONObject body = new JSONObject(client.getResponseBody());
                    //int dependentId = Integer.valueOf(body.getString("Dependent ID"));
                    int intdependentId = Integer.valueOf(mPatientId) * 2;
                    String dependentId = intdependentId + "";

                    mEmergencyContact = new EmergencyContact(dependentId, mEmergencyContactUnsaved.getFirstname(),
                            mEmergencyContactUnsaved.getLastname(), mEmergencyContactUnsaved.getContact(),
                            mEmergencyContactUnsaved.getEmailId(), mEmergencyContactUnsaved.getAddress(),
                            mEmergencyContactUnsaved.getRelation());
                    mEmergencyContactUnsaved = null;
                    // make toast about successful creation
                    Toast.makeText(getApplicationContext(), "Emergency Contact created successfully!", Toast.LENGTH_LONG).show();

                } else {
                    // make toast about server error !
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again!", Toast.LENGTH_LONG).show();
                }
            } else if (client.getId().equals(updateECRestCallId)) {
                if (client.getResponseCode() == 200) {
                    JSONObject body = new JSONObject(client.getResponseBody());
                    //this will be just like post except that we already have the dependent id;
                    mEmergencyContact = new EmergencyContact(mEmergencyContact.getId(), mEmergencyContactUnsaved.getFirstname(),
                            mEmergencyContactUnsaved.getLastname(), mEmergencyContactUnsaved.getContact(),
                            mEmergencyContactUnsaved.getEmailId(), mEmergencyContactUnsaved.getAddress(),
                            mEmergencyContactUnsaved.getRelation());
                    Toast.makeText(getApplicationContext(), "Emergency Contact updated successfully!", Toast.LENGTH_LONG).show();
                    mEmergencyContact = null;
                    Thread.sleep(1000);
                    backFromEC(null);
                } else {
                    // make toast about server error !
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again!", Toast.LENGTH_LONG).show();
                }
            } else if (client.getId().equals(deleteECRestCallId)) {
                if (client.getResponseCode() == 200) {
                    // make toast about successful deletion
                    Toast.makeText(getApplicationContext(), "Emergency Contact deleted successfully!", Toast.LENGTH_LONG).show();
                    mEmergencyContact = null;
                    Thread.sleep(1000);
                    backFromAddEC(null);

                } else {
                    // make toast about server error !
                    Toast.makeText(getApplicationContext(), "Server Error, Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}
