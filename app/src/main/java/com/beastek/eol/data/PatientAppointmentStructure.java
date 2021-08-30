package com.beastek.eol.data;

import org.json.JSONException;
import org.json.JSONObject;


public class PatientAppointmentStructure {

    public String appointment_date_time;
    public String appointment_status;
    public String appointment_desc;
    public String appointment_id;

    public PatientAppointmentStructure(JSONObject jsonObject){

        try {
            appointment_date_time = jsonObject.getString("Reminder_DateTime");
            appointment_status = jsonObject.getString("Appointment_Status");
            appointment_desc=jsonObject.getString("Appointment_Description");
            appointment_id=jsonObject.getString("Appointment_Id");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void setAppointment_date_time(String appointment_date_time) {
        this.appointment_date_time = appointment_date_time;
    }

    public void setAppointment_status(String appointment_status) {
        this.appointment_status = appointment_status;
    }

    public void setAppointment_desc(String appointment_desc) {
        this.appointment_desc = appointment_desc;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }

    public String getAppointment_date_time() {
        return appointment_date_time;
    }

    public String getAppointment_status() {
        return appointment_status;
    }

    public String getAppointment_desc() {
        return appointment_desc;
    }

    public String getAppointment_id() {
        return appointment_id;
    }
}
