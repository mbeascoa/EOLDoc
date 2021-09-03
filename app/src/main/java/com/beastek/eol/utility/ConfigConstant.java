package com.beastek.eol.utility;


public class ConfigConstant {

    public static final String BASE_URL="https://sheetdb.io/api/v1/38fj4irthplip"; //https://sheetdb.io/api/v1/ahhtehepl6e9f";
    public static final String FITBIT_BASE_URL="http://52.206.7.24:8060";
    public static final String FITBIT_SUMMARY_ENDPOINT="health/activity/summary";
    public static final String DOC_APPOINTMENT_LIST_ENDPOINT="search?sheet=addappointment&D_ID=";  //dentro del menu doctor, los appointments que tiene de sus pacientes
    public static final String PAT_APPOINTMENT_LIST_ENDPOINT="search?sheet=addappointment&P_ID=";  // patient appointments list recycler view
    public static final String DOC_PATIENT_LIST_ENDPOINT="search?sheet=insertpatient&P_ID=";  //dentro del menu doctor la lista de los pacientes asignados
    public static final String APPOINTMENT_STATUS_UPDATE="Appointment_Id";  //doctor/appointmentStatus/update actualizar el status del appointment

    public static final String PATIENT_DOC_LIST_ENDPOINT="search?sheet=insertdoctor&P_ID=";  //lista de doctores asignados a pacientes

    public static final String DOCTOR_ADD_HEALTH_REQUEST="doctor/healthDataRequest/add";
    public static final String PATIENT_HEALTH_REQUEST="patient/healthDataRequest";
    public static final String PATIENT_HEALTH_REQUEST_STATUS_UPDATE="patient/healthDataRequestStatus/update";
    public static final String GET_PATIENT_HEALTH_DATA="patient/healthData";
    public static final String POST_PATIENT_HEALTH_DATA="patient/healthData/add";
    public static final String GET_PATIENT_REQUEST_DATA="doctor/healthDataRequestDetails";
    public static final String DOCTOR_LIST_ENDPOINT="search?sheet=insertdoctor&P_ID=";  //el paciente selecciona la lista de sus doctores
    public static final String ADD_APPOINTMENT_ENDPOINT="https://sheetdb.io/api/v1/38fj4irthplip?sheet=addappointment=";  //el paciente añade appointments post se incluye la BASE URL

    public static final String FITBIT_AUTH_URL="https://www.fitbit.com/oauth2/authorize?response_type=token&client_id=23BF7P&redirect_uri=https%3A%2F%2Ffinish&scope=activity%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight&expires_in=604800";
    public static final String FITBIT_AUTH2_CLIENT="23BF7P";
    public static final String FITBIT_CLIENT_SECRET ="4d4ea6fd400198a2ce5784f49e6d66da";
    public static final String FITBIT_REDIRECT_URL ="https://finished";
    public static final String FITBIT_AUTHORIZATION_URI= "https://www.fitbit.com/oauth2/authorize";
    public static final String FITBIT_ACCESS_RENEW_TOKEN_REQUEST_URI= "https://api.fitbit.com/oauth2/token";

    public static final String PACKAGE_CUSTOM_TAB = "com.android.chrome";

    public static final String insertDoctor = "?sheet=insertdoctor";
    public static final String insertPatient = "?sheet=insertpatient";
    public static final String insertDoctorCredential = "?sheet=doctorcredentials";
    public static final String insertPatientCredential = "?sheet=patientcredentials";
    public static final String authenticateDoctor = "/search?sheet=doctorcredentials&username=";
    public static final String authenticateDoctor2 ="&password=";
    public static final String authenticateDoctor3 ="&casesensitive=true&single_object=true";
    public static final String authenticatePatient = "/search?sheet=patientcredentials&username=";
    public static final String authenticatePatient2 = "&password=";
    public static final String authenticatePatient3 ="&casesensitive=true&single_object=true";
    public static final String DOCTOR_SPECIALITY="?sheet=speciality";  //spinner specilities
    public static final String PATIENT_EMERGENCY="/patient/emergencyContact/";  //inicial en patient main activity
    public static final String PATIENT_ADD_DOCTOR="?sheet=patientdoctorassociation";  // patient adds a doctor
    // https://sheetdb.io/api/v1/38fj4irthplip?sheet=patientdoctorassociation   para obtener la asociacion entre paciente y doctor de la pestaña patientdoctorassociation

    public static final String MYSQL_SERVER_IP = "10.0.0.4";
    public static final String MYSQL_SERVER_PORT= "3306";
    public static final String MYSQL_SERVER_DATABASE = "sucamec";
    public static final String MYSQL_SERVER_USER = "root";
    public static final String MYSQL_SERVER_PASSWORD = "lnpTur01";


    public static final String INVALID_ID = "-1";


}
