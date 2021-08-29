package com.beastek.eol.ui;

import android.app.Activity;
import android.content.Intent;
//import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


import com.beastek.eol.R;
import com.beastek.eol.data.LoginInfo;
import com.beastek.eol.utility.ConfigConstant;
import com.beastek.eol.utility.encryptPasscode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

//página para hacer el LOGIN, viene del LaunchAtivity.class inicialmente

public class LoginActivity extends Activity {

    EditText txtUsername, txtPassword;

    Button btnLogin;
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private String Url="";
    private String baseUrl= "";

    //variables para los mensajes....
    //Resources res = getResources();
    //String loginfailed= res.getString(R.string.msg_login_failed);
    String loginfailed = "Login failed,,...";
    //String enteruserpassword = res.getString(R.string.msg_enter_user_password);
    String enteruserpassword = "Enter user name and password";
    //String errusernamepasswordincorrect = res.getString(R.string.err_username_password_incorrect);
    String errusernamepasswordincorrect = "Username/Password is incorrect";

    private static final String TAG = LoginActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        System.out.println("Inside login activity");
        //System.setProperty("http.keepAlive", "false");


        session = new SessionManager(getApplicationContext());

        txtUsername = (EditText) findViewById(R.id.uname_login);
        txtPassword = (EditText) findViewById(R.id.password_login);
        RadioButton rd1 = (RadioButton) findViewById(R.id.doc_rd);
        RadioButton rd2 = (RadioButton) findViewById(R.id.patient_rd);
        //String uls =getgetString(R.string.msg_user_login_state);
        Toast.makeText(getApplicationContext(), "User Login State"+ session.isLoggedIn(), Toast.LENGTH_LONG).show();
        System.out.println("User login State" + session.isLoggedIn());
        btnLogin = (Button) findViewById(R.id.login);

        //al presionar el botón de LOGIN , captura usuario, password, si es doctor o paciente,
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                Bundle bundle = getIntent().getExtras();
                RadioButton rd1 = (RadioButton) findViewById(R.id.doc_rd);
                RadioButton rd2 = (RadioButton) findViewById(R.id.patient_rd);


                LoginInfo loginInfo = new LoginInfo();

                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    loginInfo.setUsername(username);
                    loginInfo.setPassword(password);
                    //si es doctor ejecuta login como doctor
                    if (rd1.isChecked())
                        new AsyncTaskLoginDoc().execute(loginInfo);
                        //si es paciente ejecuta login como paciente
                    else
                        new AsyncTaskLoginPatient().execute(loginInfo);
                } else {
                    alert.showAlertDialog(LoginActivity.this, loginfailed, enteruserpassword, false);
                }

            }

        });
    }

    /*Android SDK contiene una clase llamada JSONReader, que nos permite poder analizar y consumir los documentos JSON.

    Para crear una instancia nueva de la clase JSONReader, debemos pasar un objeto de la clase InputStreamReader con los datos JSON a su constructor.

    También existen objetos JSONObject y JSONArray que son los que utilizaremos  para extraer los datos de un servicio RestFul.

    Para poder realizar peticiones a los servicios de internet necesitamos utilizar una clase asíncrona que llamará al servicio y administrará la petición,
    ya que Android impide realizar llamadas de red en el hilo principal de la aplicación.

    Dicha clase heredará de AsyncTask para ejecutar acciones asíncronas.

    Debemos sobrescribir dos métodos:

    doInBackground: Realizará las acciones que necesitemos al leer el servicio.

    onPostExecute: Se ejecuta una vez que se ha finalizado de leer el servicio.

     */

    // -------- D  O  C  T  O  R  -------

    public class AsyncTaskLoginDoc extends AsyncTask<LoginInfo, String, LoginInfo> {

        HttpResponse response;
        SessionManager session;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LoginInfo doInBackground(LoginInfo... params) {

            String password = encryptPasscode.encryptPassword(params[0].Password);

            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("username", params[0].Username);
                requestBody.put("password", password); //ToDo hashcode of password
                String request = requestBody.toString();
                StringEntity request_param = new StringEntity(request);
                {

                    Url = ConfigConstant.BASE_URL + ConfigConstant.authenticateDoctor + params[0].Username + ConfigConstant.authenticateDoctor2 + password + ConfigConstant.authenticateDoctor3;
                    //authenticateDoctor = "/search?sheet=doctorcredentials&username
                    // authenticateDoctor2 ="&password=";
                    //authenticateDoctor3 ="&casesensitive=true&single_object=true";
                    HttpClient httpclient = new DefaultHttpClient();
                    String resultado = null;
                    HttpGet httpget = new HttpGet(Url);
                    HttpResponse response = null;
                    InputStream stream = null;
                    try {
                        response = httpclient.execute(httpget);
                        HttpEntity entity = response.getEntity();

                        if (entity != null) {
                            stream = entity.getContent();
                            resultado = convertirInputToString(stream);
                        }

                    } catch (Exception e) {
                        System.out.println(e.toString());
                    } finally {
                        try {
                            if (stream != null) {
                                stream.close();
                            }
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                    }


                    //HttpPost post = new HttpPost(Url);
                    //post.setHeader("Content-Type","application/json");
                    //post.setEntity(request_param);
                    //HttpClient httpClient = new DefaultHttpClient();
                    //response = httpClient.execute(post);
                    //System.out.println("Reached after coming back from Backend API");
                    if (response.getStatusLine().getStatusCode() != 200) {
                        int intuserID = 0;
                        params[0].setID(intuserID);

                        if (response.getStatusLine().getStatusCode() == 401) {
                            System.out.println("Verification failed");
                        } else if (response.getStatusLine().getStatusCode() == 400) {
                            System.out.println("Verification failed");
                        } else
                            throw new RuntimeException("Failed: HTTP error code :" + response.getStatusLine().getStatusCode());
                    } else {
                        if (resultado != null) {
                            //Toast.makeText(getBaseContext(), "Datos recibidos!", Toast.LENGTH_SHORT).show();
                            System.out.println("Datos recibidos");
                            try {
                                JSONObject jsonObject = new JSONObject(resultado);

                                int intuserID = jsonObject.optInt("Doctor ID");
                                String userID = String.valueOf(intuserID);
                                params[0].setID(intuserID);


                            } catch (JSONException e) {
                                System.out.println(e.toString());
                                System.out.println("ejecutándose en doInBackground");
                            }
                        } else {
                            //Toast.makeText(getBaseContext(), "Error recibiendo datos!", Toast.LENGTH_SHORT).show();
                            System.out.println("Error recibiendo datos de credenciales");
                        }


                        //HttpEntity e = response.getEntity();
                        //String i = EntityUtils.toString(e);
                        //JSONObject j = new JSONObject(i);
                        //if(!i.equals("Request Failed"))
                        //{
                        // estaba anulado//int userID = Integer.parseInt(i);
                        //  int userID = j.getInt("Doctor ID");
                        //  params[0].setID(userID);
                    }
                }
            } catch (Exception x) {
                String errauth = getString(R.string.err_authenticate_doc_api);
                throw new RuntimeException(errauth, x);
            }
            return params[0];
        }


        //04

        private String convertirInputToString(InputStream inputStream) throws IOException {
            BufferedReader buferredReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String resultado = "";
            while ((line = buferredReader.readLine()) != null)
                resultado += line;
            inputStream.close();
            return resultado;
        }


        @Override
        protected void onPostExecute(LoginInfo loginInfo) {
            super.onPostExecute(loginInfo);
            String userID;
            userID = String.valueOf(loginInfo.ID);
            if (loginInfo.ID != 0) {
                session = new SessionManager(getApplicationContext());
                session.createLoginSession(loginInfo.Username, userID, "Doctor");
                Intent i = new Intent(getApplicationContext(), LauncherActivity.class); //ToDo doctor dashboard
                startActivity(i);
                finish();
            } else {

                alert.showAlertDialog(LoginActivity.this, loginfailed, errusernamepasswordincorrect, false);
            }
        }

    }

    //  -------P  A  T  I  E  N  T --------------

    public class AsyncTaskLoginPatient extends AsyncTask<LoginInfo, String, LoginInfo> {


        HttpResponse response;
        SessionManager session;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LoginInfo doInBackground(LoginInfo... params) {

            String password = encryptPasscode.encryptPassword(params[0].Password);

            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("username", params[0].Username);
                requestBody.put("password", password); //ToDo hashcode of password
                String request = requestBody.toString();
                StringEntity request_param = new StringEntity(request);
                {

                    Url = ConfigConstant.BASE_URL + ConfigConstant.authenticatePatient + params[0].Username + ConfigConstant.authenticatePatient2 + password + ConfigConstant.authenticatePatient3;
                    //authenticatePatient = "/search?sheet=patientcredentials&username=";
                    //authenticatePatient2 = "&password=";
                    //authenticatePatient3 ="&casesensitive=true&single_object=true"
                    HttpClient httpclient = new DefaultHttpClient();
                    String resultado = null;
                    HttpGet httpget = new HttpGet(Url);
                    HttpResponse response = null;
                    InputStream stream = null;
                    try {
                        response = httpclient.execute(httpget);
                        HttpEntity entity = response.getEntity();

                        if (entity != null) {
                            stream = entity.getContent();
                            resultado = convertirInputToString(stream);
                        }

                    } catch (Exception e) {
                        System.out.println(e.toString());
                    } finally {
                        try {
                            if (stream != null) {
                                stream.close();
                            }
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                    }


                    //HttpPost post = new HttpPost(Url);
                    //post.setHeader("Content-Type","application/json");
                    //post.setEntity(request_param);
                    //HttpClient httpClient = new DefaultHttpClient();
                    //response = httpClient.execute(post);
                    //System.out.println("Reached after coming back from Backend API");
                    if (response.getStatusLine().getStatusCode() != 200) {
                        int intuserID = 0;
                        params[0].setID(intuserID);

                        if (response.getStatusLine().getStatusCode() == 401) {
                            System.out.println("Verification failed");
                        } else if (response.getStatusLine().getStatusCode() == 400) {
                            System.out.println("Verification failed");
                        } else
                            throw new RuntimeException("Failed: HTTP error code :" + response.getStatusLine().getStatusCode());
                    } else {
                        if (resultado != null) {
                            //Toast.makeText(getBaseContext(), "Datos recibidos!", Toast.LENGTH_SHORT).show();
                            System.out.println("Datos recibidos");
                            try {
                                JSONObject jsonObject = new JSONObject(resultado);

                                int intuserID = jsonObject.optInt("PatientId");
                                String userID = String.valueOf(intuserID);
                                params[0].setID(intuserID);


                            } catch (JSONException e) {
                                System.out.println(e.toString());
                                System.out.println("ejecutándose en doInBackground");
                            }
                        } else {
                            //Toast.makeText(getBaseContext(), "Error recibiendo datos!", Toast.LENGTH_SHORT).show();
                            System.out.println("Error recibiendo datos de credenciales");
                        }


                        //HttpEntity e = response.getEntity();
                        //String i = EntityUtils.toString(e);
                        //JSONObject j = new JSONObject(i);
                        //if(!i.equals("Request Failed"))
                        //{
                        // estaba anulado//int userID = Integer.parseInt(i);
                        //  int userID = j.getInt("Doctor ID");
                        //  params[0].setID(userID);
                    }
                }
            } catch (Exception x) {
                String errauth = getString(R.string.err_authenticate_doc_api);
                throw new RuntimeException(errauth, x);
            }
            return params[0];
        }


        //04

        private String convertirInputToString(InputStream inputStream) throws IOException {
            BufferedReader buferredReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String resultado = "";
            while ((line = buferredReader.readLine()) != null)
                resultado += line;
            inputStream.close();
            return resultado;
        }


        @Override
        protected void onPostExecute(LoginInfo loginInfo) {
            super.onPostExecute(loginInfo);
            String userID;
            userID = String.valueOf(loginInfo.ID);
            if (loginInfo.ID != 0) {
                session = new SessionManager(getApplicationContext());
                session.createLoginSession(loginInfo.Username, userID, "Patient");
                Intent i = new Intent(getApplicationContext(), LauncherActivity.class); //ToDo patient dashboard
                startActivity(i);
                finish();
            } else {

                alert.showAlertDialog(LoginActivity.this, loginfailed, errusernamepasswordincorrect, false);
            }
        }

    }

    /*
    public class AsyncTaskLoginPatient extends AsyncTask<LoginInfo, Void, LoginInfo> {

        HttpResponse response;
        SessionManager session;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LoginInfo doInBackground(LoginInfo... params)
        {

            LoginInfo loginInfo=params[0];

            String password = encryptPasscode.encryptPassword(loginInfo.Password);

            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String responseJson;
            try
            {
                String baseUrl= ConfigConstant.BASE_URL;
                 Url= ConfigConstant.BASE_URL+ConfigConstant.authenticatePatient+params[0].Username+ConfigConstant.authenticatePatient2+params[0].Password+ConfigConstant.authenticatePatient3;
                //authenticatePatient = "/search?sheet=patientcredentials&username=";
                //authenticatePatient2 = "&password=";
                //authenticatePatient3 ="&casesensitive=true&limit=1"



                //String baseUrl= ConfigConstant.BASE_URL;
                //final String PATH_PARAM = ConfigConstant.authenticatePatient;
                //Uri loginUri=Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).build();




                URL url=new URL(loginUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                try {
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("username", params[0].Username);
                    requestBody.put("password", password);

                    OutputStreamWriter os = new OutputStreamWriter(urlConnection.getOutputStream());
                    os.write(requestBody.toString());
                    os.close();


                    int HttpResult = urlConnection.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream=urlConnection.getInputStream();
                        StringBuffer buffer=new StringBuffer();
                        if(inputStream==null){
                            return null;
                        }

                        reader=new BufferedReader(new InputStreamReader(inputStream));

                        String line;

                        while((line=reader.readLine())!=null){
                            buffer.append(line+"\n");
                        }
                        if(buffer.length()==0){
                            return null;
                        }
                        responseJson=buffer.toString();

                        JSONObject jsonObject=new JSONObject(responseJson);
                        int uid=jsonObject.getInt("Patient ID");
                        loginInfo.setID(uid);
                    }
                }catch (JSONException e){
                    Log.e("Error",e.getMessage());
                }
                finally {
                    if(urlConnection!=null){
                        urlConnection.disconnect();
                    }
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch (final IOException e){
                            Log.e("Error","Error closing stream",e);
                        }
                    }

                }


            } catch (IOException e){

                Log.e("Error",e.getMessage(),e);
                return null;

            }
            return loginInfo;
        }

        @Override
        protected void onPostExecute(LoginInfo L)
        {
            super.onPostExecute(L);
            String userID;
            userID = String.valueOf(L.ID);
            if(L.ID != 0)
            {
                session = new SessionManager(getApplicationContext());
                session.createLoginSession(L.Username, userID,"Patient");
                Intent i = new Intent(getApplicationContext(), LauncherActivity.class); //ToDo Patient dashboard
                startActivity(i);
                finish();
            }
            else
            {
                alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
            }
        }

    }

    */


    //go to home page
    public void gotoHome(View V) {
        Intent intent = new Intent(LoginActivity.this, LauncherActivity.class);
        startActivity(intent);
    }

    // finish login activity
    public void finishLoginActivity(View V) {
        LoginActivity.this.finish();
        ;
    }

}


