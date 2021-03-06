package com.beastek.eol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.beastek.eol.Extras.Interface.AppointmentAdapterToAppointmentActivity;
import com.beastek.eol.R;
import com.beastek.eol.data.AppointmentStructure;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.AppointmentViewHolder> {

    private Context context;
    private List<AppointmentStructure> appointmentList;
    AppointmentAdapterToAppointmentActivity itemClickListener;

    public AppointmentListAdapter(Context mContext, List<AppointmentStructure> appointment_list) {
        this.context = mContext;
        this.appointmentList = appointment_list;
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_list_item, parent, false);

        return new AppointmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, int position) {

        AppointmentStructure appObj = appointmentList.get(position);
        holder.patientName.setText(appObj.getPatient_name());

        //depending the appointment status we add a color to it.
        if(appObj.getAppointment_status().equals(context.getResources().getString(R.string.appt_status_requested))) {
            holder.status.setText("Request Pending");
            holder.status.setTextColor(ContextCompat.getColor(context,R.color.yellowColor));
            // equals "RequestPending" se visualizan en amarillo
        }else if(appObj.getAppointment_status().equals(context.getResources().getString(R.string.appt_status_accepted))){
            holder.status.setText(appObj.getAppointment_status());
            holder.status.setTextColor(ContextCompat.getColor(context,R.color.greenColor));
            // equals "Accepted" se visualizan en verde
        }else if(appObj.getAppointment_status().equals(context.getResources().getString(R.string.appt_status_declined))){
            holder.status.setText(appObj.getAppointment_status());
            holder.status.setTextColor(ContextCompat.getColor(context,R.color.redColor));
            //equals. "Declined" se pone en rojo
        }
        //String date=convertDate(appObj.getAppointment_date_time());
        String date = appObj.getAppointment_date_time();
        holder.appDate.setText(date);
        holder.appDesc.setText(appObj.getAppointment_desc());

    }


    // we keep the data in string format, not in Date format in Google Sheet, when working with Database we will use this
    private String convertDate(String input_date){
        SimpleDateFormat inputDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date p_date=null;
        try{
            p_date=inputDateFormat.parse(input_date);
        }catch (ParseException e){
            e.printStackTrace();
        }

        Format finalDateFormat=new SimpleDateFormat("MMM dd yyyy");
        String finalDate=finalDateFormat.format(p_date);
        return finalDate;
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView patientName, status, appDate,appDesc;

        public AppointmentViewHolder(View itemView) {
            super(itemView);

            patientName = (TextView) itemView.findViewById(R.id.card_patient_name);
            status = (TextView) itemView.findViewById(R.id.card_status);
            appDate = (TextView) itemView.findViewById(R.id.card_date);
            appDesc=(TextView)itemView.findViewById(R.id.card_app_desc);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if(itemClickListener!=null){
                itemClickListener.onAppointmentItemClick(getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(final AppointmentAdapterToAppointmentActivity appointmentItemClickListener){
        this.itemClickListener=appointmentItemClickListener;
    }

}
