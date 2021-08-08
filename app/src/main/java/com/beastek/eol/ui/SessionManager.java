package com.beastek.eol.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

// Las API de SharedPreferences son para leer y escribir pares clave-valor.
//getSharedPreferences(): utilizamos este método porque necesitamos varios archivos de
//preferencias compartidas identificados por nombre, que especificas con el primer parámetro.
// Se puede llamar a este método desde cualquier instancia de Context en la app.
//getPreferences(): utiliza este método desde una instancia de Activity si necesitas utilizar
// UN SOLO archivo de preferencias compartidas para la actividad. Como este método recupera un
// archivo de preferencias compartidas predeterminado que pertenece a la actividad,
//  no necesitas indicar un nombre.
//Por ejemplo, el siguiente código accede al archivo de preferencias compartidas identificado por
//la string de recursos  R.string.preference_file_key y lo abre con el modo privado para que solo
// tu app pueda acceder al archivo:
//Context context = getActivity();
//        SharedPreferences sharedPref = context.getSharedPreferences(
//        getString(R.string.preference_file_key), Context.MODE_PRIVATE);

//Para recuperar valores de un archivo de preferencias compartidas, llama a métodos como
//getInt() y getString(), proporciona la clave del valor que desees y,
//opcionalmente, un valor predeterminado para mostrar si no se encuentra la clave. Por ejemplo:
//    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
//            int defaultValue = getResources().getInteger(R.integer.saved_high_score_default_key);
//            int highScore = sharedPref.getInt(getString(R.string.saved_high_score_key), defaultValue);


//.--------------------------------------------------------

public class SessionManager
{

    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AndroidHivePref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "userId";
    public static final String KEY_TYPE = "type";

    public static final String HAS_ACCESS_TOKEN = "hasAuthToken";
    public static final String FITBIT_UID = "fitbitUserID";
    public static final String FITBIT_TOKEN = "fitbit_token";
    public static final String FITBIT_TOKEN_TYPE = "fitbitTokenType";
    public static final String FITBIT_FULL_AUTH = "fitbitFullAuth";

    public static final String EMERGENCY_CONTACT="emergencyContact";
    public static final String EMERGENCY_ID="emergencyID";

    public static final String FITBIT_SLEEP_DATA="fitbitSleep";
    public static final String FITBIT_CALORIES_DATA="fitbitCalories";
    public static final String FITBIT_HEART_RATE_DATA="fitbitHeartRate";
    public static final String FITBIT_STEPS_DATA="fitbitSteps";


    public SessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Editamos las variables del Shared Preferences IS_LOGIN a true,
    //KEY_NAME a Username, KEY_ID a ID y KEY_TYPE a type
    public void createLoginSession(String Username, String ID, String type)
    {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, Username);
        editor.putString(KEY_ID,ID);
        editor.putString(KEY_TYPE,type);
        editor.commit();
    }

    //Creamos la sesión Fitbit:  HAS_ACCESS_TOKEN, FITBIT_UID, FITBIT_TOKEN, FITBIT_TOKEN_TYPE,
    // FITBIT_FULL_AUTH
    public void createFitbitSession(Boolean f1, String f2, String f3, String f4, String f5)
    {
        editor.putBoolean(HAS_ACCESS_TOKEN,f1);
        editor.putString(FITBIT_UID, f2);
        editor.putString(FITBIT_TOKEN,f3);
        editor.putString(FITBIT_TOKEN_TYPE,f4);
        editor.putString(FITBIT_FULL_AUTH,f5);
        editor.commit();

    }

    //Establecemos los datos de Fitbit FITBIT_SLEEP_DATA, FITBIT_CALORIES_DATA, FITBIT_HEART_RATE_DATA,
    // FITBIT_STEPS_DATA
    public void setFitbitData(String sleep_data,String calories,String heart_rate,String steps){

        editor.putString(FITBIT_SLEEP_DATA,sleep_data);
        editor.putString(FITBIT_CALORIES_DATA,calories);
        editor.putString(FITBIT_HEART_RATE_DATA,heart_rate);
        editor.putString(FITBIT_STEPS_DATA,steps);
        editor.commit();
    }


    public HashMap<String, Boolean>getFitbit()
    {
        HashMap<String, Boolean> fitbit = new HashMap<String, Boolean>();
        fitbit.put(HAS_ACCESS_TOKEN, pref.getBoolean(HAS_ACCESS_TOKEN,false));

        return fitbit;
    }

    public String getFitbitSleepData(){
        return pref.getString(FITBIT_SLEEP_DATA,"");
    }

    public String getFitbitCaloriesData(){
        return pref.getString(FITBIT_CALORIES_DATA,"");
    }

    public String getFitbitHeartRateData(){
        return pref.getString(FITBIT_HEART_RATE_DATA,"");
    }

    public String getFitbitStepsData(){
        return pref.getString(FITBIT_STEPS_DATA,"");
    }

    public Boolean hasFitbitToken(){
        boolean istoken=pref.getBoolean(HAS_ACCESS_TOKEN,false);
        return istoken;
    }

    public String getFitbitToken(){

        return pref.getString(FITBIT_TOKEN,"");
    }

    public String getFitbitUid(){

        return pref.getString(FITBIT_UID,"");
    }


    public void checkLogin()
    {
        System.out.println("Dentro de  checklogin");
        //si no está logeado, lo manda a LoginActivity, cierra todas las actividades por encima de esta y no la crea de nuevo
        if(!this.isLoggedIn())
        {
            System.out.println("dentro del  checklogin if statement");
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            /*
            flag — FLAG_ACTIVITY_CLEAR_TOP: If the Activity being started is already running
             in the current task then instead of launching the new instance of that Activity, all
             the other activities on top of it is destroyed
            (with call to onDestroy method) and this intent is delivered to the resumed instance
            of the Activity
             */
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            /* If set, this activity will become the start of a new task on this history stack. A task
            (from the activity that started it to the next task activity) defines an atomic group of
            activities that the user can move to. Tasks can be moved to the foreground and background;
            all of the activities inside of a particular task always remain in the same order. See Tasks
            and Back Stack for more information about tasks.
            This flag is generally used by activities that want to present a "launcher" style behavior:
            they give the user a list of separate things that can be done, which otherwise run completely
             independently of the activity launching them.
             When using this flag, if a task is already running for the activity you are now starting,
             then a new activity will not be started; instead, the current task will simply be brought
             to the front of the screen with the state it was last in. See FLAG_ACTIVITY_MULTIPLE_TASK for
              a flag to disable this behavior
             This flag can not be used when the caller is requesting a result from the activity being
             launched.    */

            _context.startActivity(i);

        }

    }

    // Obtenemos los datos del usuario.
    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_ID, pref.getString(KEY_ID, "000"));
        user.put(KEY_TYPE, pref.getString(KEY_TYPE, null));

        return user;
    }

    // Cremos el contacto de emergencia, lo salvamos en preferencias
    public void createEmergencyContact(String contact, String ID)
    {
        editor.putString(EMERGENCY_ID, ID);
        editor.putString(EMERGENCY_CONTACT,contact);
    }

    //Obtenemos los datos del contacto de emergencias de preferencias
    public HashMap<String, String> getEmergencyContact()
    {
        HashMap<String, String> E = new HashMap<>();
        E.put(EMERGENCY_CONTACT,pref.getString(EMERGENCY_CONTACT,null));
        E.put(EMERGENCY_ID,pref.getString(EMERGENCY_ID,null));

        return E;
    }

    public void logoutUser()
    {
        // Borramos todos los datos de Shared Preferences, y nos lleva a HomeActivity.class
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }


    public boolean isLoggedIn()
    {
        //nos da el balor booleano guardado de la variable en preferencias IS_LOGIN, si es nulo da false
        return pref.getBoolean(IS_LOGIN, false);
    }

}