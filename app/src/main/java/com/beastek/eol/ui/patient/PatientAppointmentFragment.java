package com.beastek.eol.ui.patient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beastek.eol.R;
import com.beastek.eol.adapter.PatientAppointmentListAdapter;
import com.beastek.eol.data.PatientAppointmentStructure;
import com.beastek.eol.ui.SessionManager;
import com.beastek.eol.utility.ConfigConstant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PatientAppointmentFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private FloatingActionButton fabAdd;
    private RecyclerView recyclerViewPatAppointment;

    PatientAppointmentListAdapter appmtListAdapter;

    SessionManager sessionManager;
    String patient_id;

    public PatientAppointmentFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View root_view= inflater.inflate(R.layout.fragment_patient_appointment,container,false);

        sessionManager=new SessionManager(getActivity());
        HashMap<String,String> user=sessionManager.getUserDetails();
        patient_id=user.get(SessionManager.KEY_ID);

        fabAdd=(FloatingActionButton)root_view.findViewById(R.id.pat_add_appointment);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed();
            }
        });

        recyclerViewPatAppointment=(RecyclerView) root_view.findViewById(R.id.list_view_pat_appointments);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.LayoutManager layoutManager=linearLayoutManager;

        recyclerViewPatAppointment.setLayoutManager(layoutManager);
        recyclerViewPatAppointment.setItemAnimator(new DefaultItemAnimator());

        getPatAppointmentList();

        return root_view;
    }

    public void updatePatientAppList(){
        getPatAppointmentList();
    }

    private void getPatAppointmentList(){
        FetchAppmtListTask appmtListTask=new FetchAppmtListTask();
        appmtListTask.execute(patient_id);
    }


    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class FetchAppmtListTask extends AsyncTask<String,Void,List<PatientAppointmentStructure>> {

        private final String LOG_TAG=FetchAppmtListTask.class.getSimpleName();

        private List<PatientAppointmentStructure> getAppmtListFromJson(String appJsonStr) throws JSONException {


            List<PatientAppointmentStructure> appmtList=new ArrayList<>();
            JSONArray jsonArray=new JSONArray(appJsonStr);


            for(int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject=jsonArray.getJSONObject(i);

                PatientAppointmentStructure donationObj=new PatientAppointmentStructure(jsonObject);
                String appointment_date_time = jsonObject.getString("Reminder_DateTime");
                String appointment_status = jsonObject.getString("Appointment_Status");
                String appointment_desc=jsonObject.getString("Appointment_Description");
                String appointment_id=jsonObject.getString("Appointment_Id");

                donationObj.setAppointment_date_time(appointment_date_time);
                donationObj.setAppointment_status(appointment_status);
                donationObj.setAppointment_desc(appointment_desc);
                donationObj.setAppointment_id(appointment_id);

                appmtList.add(donationObj);
            }

            return appmtList;

        }


        @Override
        protected List<PatientAppointmentStructure> doInBackground(String... params) {


            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String appListJson=null;

            try{
                String baseUrl= ConfigConstant.BASE_URL;
                final String PATH_PARAM = ConfigConstant.PAT_APPOINTMENT_LIST_ENDPOINT;
                final String pat_id=params[0];
                //En este caso nos da la relación de los appointments que tiene un deteminado paciente. P_ID=1
                //https://sheetdb.io/api/v1/ahhtehepl6e9f/search?sheet=addappointment&P_ID=1

                Uri appUriParcial=Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).appendEncodedPath(pat_id).build();
                String appUripar= appUriParcial.toString();
                String appUri= appUripar.replaceAll("=/","=");
                //en la construccion de la Uri se mete un =/1 que debe ser un =1 por eso se quita
                URL url=new URL(appUri.toString());

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

                appListJson=buffer.toString();


                Log.v(LOG_TAG,"AppListStr: "+appListJson);
                System.out.println("Patient Appointment List gathered from Google Sheets :"+ appListJson);

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
                return getAppmtListFromJson(appListJson);
            }catch (JSONException e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(List<PatientAppointmentStructure> result){
            if(result!=null){

                if(appmtListAdapter==null) {
                    appmtListAdapter = new PatientAppointmentListAdapter(getContext(), result);
                    recyclerViewPatAppointment.setAdapter(appmtListAdapter);
                }else{
                    appmtListAdapter.refreshList(result);
                }
            }
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

}
