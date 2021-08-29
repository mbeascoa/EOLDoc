package com.beastek.eol.ui.doctor;

//import android.app.Activity; we change Activiy por AppCompatActivity
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.beastek.eol.R;
import com.beastek.eol.data.DoctorInfo;
import com.beastek.eol.ui.AlertDialogManager;
import com.beastek.eol.ui.LauncherActivity;
import com.beastek.eol.ui.SessionManager;
import com.beastek.eol.utility.ConfigConstant;
import com.beastek.eol.utility.encryptPasscode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class DoctorRegistration extends AppCompatActivity {

    private int uniqueDoctorId = 1;


    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_registration);
        Spinner dropdown = (Spinner)findViewById(R.id.dropDown);
        //TODO put values in Database MYSQL and get those values
        String[] items = new String[]{"GeneralPhysician","Cardiology", "Neurology","Oncology","AllergyImmunology", "CriticalCare","Psychiatry", "Urology"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_custom, items);
        dropdown.setAdapter(adapter);
        TextView lname = (TextView) findViewById(R.id.lname_doc);
        lname.setText(bundle.getString("lname"));

    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void LoginDoctorRegistration(View V)
    {

        Bundle bundle = getIntent().getExtras();

        EditText li =(EditText)findViewById(R.id.license);
        EditText cli = (EditText)findViewById(R.id.clinic);
        EditText un =(EditText)findViewById(R.id.uname_doc);
        EditText pass = (EditText)findViewById(R.id.password_doctor);
        Spinner spinspeciality = (Spinner)findViewById(R.id.dropDown);


        String license= li.getText().toString();
        //we obtain the text from the selected item in the spinner,
        String speciality= spinspeciality.getSelectedItem().toString();
        String clinic_add= cli.getText().toString();
        String uname= un.getText().toString();
        String password= pass.getText().toString();


        System.out.println("user name = "+uname);

        DoctorInfo D = new DoctorInfo();
        D.setFname(bundle.getString("fname"));
        D.setLname(bundle.getString("lname"));
        D.setDOB(bundle.getString("DOB"));
        D.setEmail(bundle.getString("email"));
        D.setContact(bundle.getString("contactNo"));
        D.setGender(bundle.getString("gender"));
        D.setSpeciality(speciality);
        D.setLicense(license);
        D.setAddress(clinic_add);
        D.setUsername(uname);
        D.setPassword(password);

        new AsyncTaskDoc().execute(D);

    }

    public class AsyncTaskDoc extends AsyncTask<DoctorInfo, String, DoctorInfo> {

        HttpResponse response;
        SessionManager session;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected DoctorInfo doInBackground(DoctorInfo... params)
        {
            try {

                JSONObject requestBody = new JSONObject();
                requestBody.put("username", params[0].Username);
                String password = encryptPasscode.encryptPassword(params[0].Password);
                requestBody.put("password",password);
                int docId= getUniqueDoctor();
                String dId = docId+"";
                requestBody.put("Doctor ID", dId);
                String request = requestBody.toString();
                System.out.println(requestBody);
                System.out.println(request);
                StringEntity request_param = new StringEntity(request);

                String Url= ConfigConstant.BASE_URL+ConfigConstant.insertDoctorCredential;
                HttpPost post = new HttpPost(Url);
                post.setHeader("Content-Type","application/json");
                post.setEntity(request_param);
                HttpClient httpClient = new DefaultHttpClient();
                response = httpClient.execute(post);
                System.out.println("Reached after coming back from Backend API");
                if (response.getStatusLine().getStatusCode() != 201)
                {
                    if(response.getStatusLine().getStatusCode() == 500)
                    {
                        System.out.println("Doctor registration failed!");
                    }
                    else
                       throw new RuntimeException("Failed: HTTP error code :" + response.getStatusLine().getStatusCode());
                }
                else
                {
                    /*
                    getEntity() of org.apache.http.HttpResponse Obtains the message entity of this response, if any.
                    The entity is provided by calling #setEntity.
                     */
                    HttpEntity e = response.getEntity();
                    String i = EntityUtils.toString(e);
                    System.out.println(i);
                    //JSONObject j = new JSONObject(i);
                    //int userID = j.getInt("integer");
                   //int userID = Integer.parseInt(i);
                    int userID= docId;
                    params[0].setID(userID);
                }
            }
            catch(Exception x)
            {
                throw new RuntimeException("Error from insert credential api",x);
            }

                // inserting doctor information gathered from the registration
            try {

                JSONObject requestBody = new JSONObject();
                requestBody.put("firstname",params[0].fname);
                requestBody.put("lastname",params[0].lname);
                requestBody.put("DOB",params[0].DOB);
                requestBody.put("emailId", params[0].email);
                requestBody.put("gender",params[0].gender);
                requestBody.put("contactNo",params[0].contact);
                requestBody.put("licenseNumber",params[0].license);
                requestBody.put("speciality",params[0].speciality);
                requestBody.put("address",params[0].address);
                requestBody.put("D_ID", params[0].ID);
                String request = requestBody.toString();
                StringEntity request_param = new StringEntity(request);

                String Url= ConfigConstant.BASE_URL+ConfigConstant.insertDoctor;
                HttpPost post = new HttpPost(Url);
                post.setEntity(request_param);
                post.setHeader("Content-Type","application/json");
                HttpClient httpClient = new DefaultHttpClient();
                response = httpClient.execute(post);
                System.out.println("Reached after coming back from Backend API");
                if (response.getStatusLine().getStatusCode() != 201)
                {
                    if(response.getStatusLine().getStatusCode() == 500)
                    {
                        System.out.println("Doctor registration failed!");
                    }
                    else if(response.getStatusLine().getStatusCode() == 400)
                    {
                        System.out.println("Doctor registration failed!");
                    }
                    else
                        throw new RuntimeException("Failed: HTTP error code :" + response.getStatusLine().getStatusCode());
                }
                else
                {
                    System.out.println("Reached after coming back from Backend API obtaning response code 201");
                    HttpEntity e = response.getEntity();
                    //String str = EntityUtils.toString(e);
                    //JSONObject j = new JSONObject(str);
                    //msg = j.getString("string");
                    //System.out.println("msg from doc info api: " +msg);
                    msg = "Request Succeeded!";
                }
            }
            catch (Exception x)
            {
                throw new RuntimeException("Error from insert Doc details api", x);
            }

            return params[0];
        }

        @Override
        protected void onPostExecute(DoctorInfo D)
        {
            super.onPostExecute(D);
            String userID;
            userID = String.valueOf(D.ID);
            System.out.println("user ID from doc post exec: "+userID);
            if(msg.equals("Request Succeeded!"))
            {
                System.out.println("Inside request succeeded of doc");
                session = new SessionManager(getApplicationContext());
                session.createLoginSession(D.Username, userID, "Doctor");

                Intent i = new Intent(getApplicationContext(), LauncherActivity.class); //doc dashboard
                startActivity(i);
                finish();
            }
            else
            {
                //ToDo insert doc unsuccessfull dialog
                alert.showAlertDialog(DoctorRegistration.this, "Doctor resgistration failed..", "Re-Register", false);
            }
        }
    }

    // creates a Unique Doctor Identifier
    private int getUniqueDoctor() {
        return uniqueDoctorId++;
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void finishDoctorRegistration(View V)
    {
        DoctorRegistration.this.finish();;
    }
}
