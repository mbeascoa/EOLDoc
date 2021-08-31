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

import com.beastek.eol.Extras.Interface.AppointmentAdapterToAppointmentActivity;
import com.beastek.eol.Extras.Interface.AppointmentFragmentToAppointmentActivity;
import com.beastek.eol.R;
import com.beastek.eol.adapter.AppointmentListAdapter;
import com.beastek.eol.data.AppointmentData;
import com.beastek.eol.data.AppointmentStructure;
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


public class AppointmentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewAppointment;
    private String doc_id;
    AppointmentListAdapter appointmentListAdapter;
    SessionManager session;
    AppointmentFragmentToAppointmentActivity appointmentItemListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root_view=inflater.inflate(R.layout.fragment_appointment,container,false);

        session=new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        doc_id = user.get(SessionManager.KEY_ID);


        swipeRefreshLayout=(SwipeRefreshLayout)root_view.findViewById(R.id.appointment_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerViewAppointment=(RecyclerView) root_view.findViewById(R.id.appointment_list_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.LayoutManager layoutManager=linearLayoutManager;

        recyclerViewAppointment.setLayoutManager(layoutManager);
        recyclerViewAppointment.setItemAnimator(new DefaultItemAnimator());

        getAppointmentList();


        return root_view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentFragmentToAppointmentActivity) {
            appointmentItemListener = (AppointmentFragmentToAppointmentActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement volunteer listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        appointmentItemListener = null;
    }

    public void appointmentItemClick(int position) {
        if (appointmentItemListener != null)
            appointmentItemListener.onAppointmentItemClick(position);
    }

    public void getAppointmentList(){
        FetchAppointmentListTask fetchAppointmentListTask=new FetchAppointmentListTask();
        fetchAppointmentListTask.execute(doc_id);
    }

    public void updateAppointmentList(){
        getAppointmentList();
    }

    @Override
    public void onRefresh() {

        getAppointmentList();
        swipeRefreshLayout.setRefreshing(false);

    }


    public class FetchAppointmentListTask extends AsyncTask<String,Void,AppointmentData> {

        private final String LOG_TAG=FetchAppointmentListTask.class.getSimpleName();

        private AppointmentData getAppointmentListFromJson(String appJsonStr) throws JSONException {

            AppointmentData appointmentData=AppointmentData.getInstance();
            appointmentData.clear();
            AppointmentStructure appObj;
            JSONArray jsonArray=new JSONArray(appJsonStr);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject=jsonArray.getJSONObject(i);
                appObj=new AppointmentStructure(jsonObject);

                String patient_fullname=jsonObject.getString("Patient_FullName");
                String appdatetime=jsonObject.getString("Reminder_DateTime");
                String appstatus=jsonObject.getString("Appointment_Status");
                String appdescription=jsonObject.getString("Appointment_Description");
                String appid=jsonObject.getString("Appointment_Id");


                appObj.setPatient_name(patient_fullname);
                appObj.setAppointment_date_time(appdatetime);
                appObj.setAppointment_status(appstatus);
                appObj.setAppointment_desc(appdescription);
                appObj.setAppointment_id(appid);

                appointmentData.add(appObj);

            }

            return appointmentData;

        }


        @Override
        protected AppointmentData doInBackground(String... params) {


            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String appListJson=null;

            try{
                String baseUrl= ConfigConstant.BASE_URL;
                final String PATH_PARAM = ConfigConstant.DOC_APPOINTMENT_LIST_ENDPOINT;
                final String DOCTOR_ID=params[0];
                //En este caso nos da la relación de los appointments que tiene un deteminado doctor. D_ID=1
                //https://sheetdb.io/api/v1/ahhtehepl6e9f/search?sheet=addappointment&D_ID=1


                Uri appointmtUriParcial = Uri.parse(baseUrl).buildUpon().appendEncodedPath(PATH_PARAM).appendEncodedPath(DOCTOR_ID).build();

               String appointmtUripar= appointmtUriParcial.toString();
                String appointmtUri= appointmtUripar.replaceAll("=/","=");
                URL url=new URL(appointmtUri);

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


                Log.v(LOG_TAG,"AppointmentListStr: "+appListJson);

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
                return getAppointmentListFromJson(appListJson);
            }catch (JSONException e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(AppointmentData result){
            if(result!=null){
                if(appointmentListAdapter==null) {
                    appointmentListAdapter = new AppointmentListAdapter(getContext(), result.data);
                    recyclerViewAppointment.setAdapter(appointmentListAdapter);
                }else{
                    appointmentListAdapter.notifyDataSetChanged();
                }

                appointmentListAdapter.setOnItemClickListener(new AppointmentAdapterToAppointmentActivity() {
                    @Override
                    public void onAppointmentItemClick(int position) {
                        appointmentItemClick(position);
                    }
                });
            }
        }
    }

}
