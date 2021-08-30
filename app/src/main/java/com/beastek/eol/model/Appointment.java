package com.beastek.eol.model;

import com.beastek.eol.utility.ConfigConstant;

public class Appointment {
    private int Appointment_ID;
    private String Description;
    private String appointmentDate;
    private String Appointment_Status;
    private int PAT_ID;
    private int DOC_ID;

    public Appointment(int Appointment_IDIn,String DescriptionIn, String Reminder_DateIn, String Appointment_StatusIn,int PAT_IDIn, int DOC_IDIn){
        Appointment_ID = Appointment_IDIn;
        Description = DescriptionIn;
        appointmentDate = Reminder_DateIn;
        Appointment_Status = Appointment_StatusIn;
        PAT_ID = PAT_IDIn;
        DOC_ID = DOC_IDIn;
    }
    // construtor with INVALID_ID when this value is not provided
    public Appointment(String DescriptionIn, String Reminder_DateIn, String Appointment_StatusIn, int PAT_IDIn, int DOC_IDIn){
        Appointment_ID = Integer.valueOf(ConfigConstant.INVALID_ID);
        Description = DescriptionIn;
        appointmentDate = Reminder_DateIn;
        Appointment_Status = Appointment_StatusIn;
        PAT_ID = PAT_IDIn;
        DOC_ID = DOC_IDIn;
    }

    public Appointment clone(Appointment other) {
        Appointment newAppointment = new Appointment(other.Appointment_ID, other.Description, other.appointmentDate, other.Appointment_Status, other.PAT_ID, other.DOC_ID);
        return newAppointment;
    }

    public int getId() { return Appointment_ID; }
    public String getDescription() { return Description; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentStatus() { return Appointment_Status; }
    public int getPatientId() { return PAT_ID; }
    public int getDoctorId() { return DOC_ID; }

}
