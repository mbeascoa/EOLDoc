package com.beastek.eol.data;

import org.json.JSONException;
import org.json.JSONObject;


public class PatientStructure {

    public String patient_id;
    public String patient_fname;
    public String patient_lname;
    public String dob;
    public String gender;
    public String weight;
    public String age;
    public String email;
    public String contact_num;
    public String address;

    public void setPatientid(String patient_id) {
        this.patient_id = patient_id;
    }

    public void setPatientfname(String patient_fname) {
        this.patient_fname = patient_fname;
    }

    public void setPatientlname(String patient_lname) {
        this.patient_lname = patient_lname;
    }

    public void setPatientDob(String dob) {
        this.dob = dob;
    }

    public void setPatientGender(String gender) {
        this.gender = gender;
    }

    public void setPatientWeight(String weight) {
        this.weight = weight;
    }

    public void setPatientAge(String age) {
        this.age = age;
    }

    public void setPatientEmail(String email) {
        this.email = email;
    }

    public void setPatientContactNum(String contact_num) {
        this.contact_num = contact_num;
    }

    public void setPatientAddress(String address) {
        this.address = address;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public String getPatient_fname() {
        return patient_fname;
    }

    public String getPatient_lname() {
        return patient_lname;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getWeight() {
        return weight;
    }

    public String getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getContact_num() {
        return contact_num;
    }

    public String getAddress() {
        return address;
    }

    public PatientStructure(JSONObject jsonObject){

        try {
            patient_id=jsonObject.getString("P_Id");
            patient_fname=jsonObject.getString("firstname");
            patient_lname=jsonObject.getString("lastname");
            dob=jsonObject.getString("DOB");
            gender=jsonObject.getString("gender");
            weight=jsonObject.getString("weight");
            age=jsonObject.getString("age");
            email=jsonObject.getString("emailId");
            contact_num=jsonObject.getString("contactNo");
            address=jsonObject.getString("address");


        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
