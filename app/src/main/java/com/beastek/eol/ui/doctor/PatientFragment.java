package com.beastek.eol.ui.doctor;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.beastek.eol.Extras.Interface.DoctorFragmentToDoctorActivity;
import com.beastek.eol.Extras.Interface.PatientAdapterToPatientFragment;
import com.beastek.eol.Extras.Interface.PatientFragmentToPatientActivity;
import com.beastek.eol.R;
import com.beastek.eol.adapter.PatientListAdapter;
import com.beastek.eol.data.PatientData;
import com.beastek.eol.data.PatientStructure;
import com.beastek.eol.ui.SessionManager;
import com.beastek.eol.utility.ConfigConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class PatientFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewPatient;
    private PatientListAdapter patientListAdapter;
    private SessionManager sessionManager;
    private String doc_id;
    private PatientFragmentToPatientActivity patientItemListener;
   private Uri docUri;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root_view=inflater.inflate(R.layout.fragment_doctor_patients,container,false);
        swipeRefreshLayout=(SwipeRefreshLayout)root_view.findViewById(R.id.patients_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        sessionManager=new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetails();
        doc_id = user.get(SessionManager.KEY_ID);


        recyclerViewPatient=(RecyclerView) root_view.findViewById(R.id.patient_list_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView.LayoutManager layoutManager=linearLayoutManager;

        recyclerViewPatient.setLayoutManager(layoutManager);
        recyclerViewPatient.setItemAnimator(new DefaultItemAnimator());

        getPatientList();

        return root_view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PatientFragmentToPatientActivity) {
            patientItemListener = (PatientFragmentToPatientActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement volunteer listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        patientItemListener = null;
    }

    public void getPatientList(){

        FetchPatientListTask fetchPatientListTask=new FetchPatientListTask();
        fetchPatientListTask.execute(doc_id);
    }

    public void patientItemClick(int position){
        if(patientItemListener!=null){
            patientItemListener.onPatientItemClick(position);
        }
    }

    @Override
    public void onRefresh() {

        getPatientList();
        swipeRefreshLayout.setRefreshing(false);
    }

    public class FetchPatientListTask extends AsyncTask<String,Void,PatientData> {

        private final String LOG_TAG=FetchPatientListTask.class.getSimpleName();

        private PatientData getPatientListFromJson(String appJsonStr) throws JSONException {

            PatientData patientData=PatientData.getInstance();
            patientData.clear();
            PatientStructure patObj;
            JSONArray jsonArray=new JSONArray(appJsonStr);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                patObj=new PatientStructure(jsonObject);

                String patient_id=jsonObject.getString("P_ID");
                String patient_fname=jsonObject.getString("firstname");
                String patient_lname=jsonObject.getString("lastname");
                String patient_dob=jsonObject.getString("DOB");
                String patient_gender=jsonObject.getString("gender");
                String patient_weight=jsonObject.getString("weight");
                String patient_age=jsonObject.getString("age");
                String patient_email=jsonObject.getString("emailId");
                String patient_contact_num=jsonObject.getString("contactNo");
                String patient_address=jsonObject.getString("address");

                patObj.setPatientid(patient_id);
                patObj.setPatientfname(patient_fname);
                patObj.setPatientlname(patient_lname);
                patObj.setPatientDob(patient_dob);
                patObj.setPatientGender(patient_gender);
                patObj.setPatientWeight(patient_weight);
                patObj.setPatientAge(patient_age);
                patObj.setPatientEmail(patient_email);
                patObj.setPatientContactNum(patient_contact_num);
                patObj.setPatientAddress((patient_address));

                patientData.add(patObj);

            }

            return patientData;

        }


        @Override
        protected PatientData doInBackground(String... params) {


            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String patListJson=null;

            try{
                String baseUrl= ConfigConstant.BASE_URL;


                final String DOC_ID=params[0];
                //String DOC_PATIENT_LIST_ENDPOINT="search?sheet=insertpatient&P_ID="
                final String PATH_PARAM = ConfigConstant.DOC_PATIENT_LIST_ENDPOINT + DOC_ID;
                // la cadena correcta es https://sheetdb.io/api/v1/ahhtehepl6e9f/search?sheet=insertpatient&D_ID=1 para consultar
                //por el D_ID= 1 ;  que es el DOC_ID


                //Uri docUri= Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).appendEncodedPath(PAT_ID).build();
                docUri= Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).build();

                URL url=new URL(docUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


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

                patListJson=buffer.toString();


                Log.v(LOG_TAG,"PatientListStr: "+patListJson);

            }catch (IOException e){

                Log.e(LOG_TAG,e.getMessage(),e);
                return null;

            }
            finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }catch (final IOException e){
                        Log.e(LOG_TAG,"Error closing stream",e);
                    }
                }

            }

            try{
                return getPatientListFromJson(patListJson);
            }catch (JSONException e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(PatientData result){
            if(result!=null){

                if(patientListAdapter==null) {
                    patientListAdapter = new PatientListAdapter(getContext(), result.data);
                    recyclerViewPatient.setAdapter(patientListAdapter);
                }else{
                    patientListAdapter.notifyDataSetChanged();
                }
                patientListAdapter.setOnItemClickListener(new PatientAdapterToPatientFragment() {
                    @Override
                    public void onPatientItemClick(int position) {
                        patientItemClick(position);
                    }
                });
            }
        }
    }



}
