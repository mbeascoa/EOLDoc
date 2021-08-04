package com.beastek.eol.ui.patient;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.beastek.eol.Extras.Interface.PatientDashboardFragmentToActivity;
import com.beastek.eol.Extras.broadcast_receiver.FallDetectService;
import com.beastek.eol.R;
import com.beastek.eol.adapter.NavigationListAdapter;
import com.beastek.eol.data.NavDrawerItem;
import com.beastek.eol.ui.SessionManager;
import com.beastek.eol.utility.ConfigConstant;
import com.google.android.gms.common.api.GoogleApi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PatientMainActivity extends AppCompatActivity implements PatientDashboardFragmentToActivity {

    private String[] drawerTitleArray;
    private TypedArray drawerIconsArray;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationListAdapter menuListAdapter;
    private ListView drawerList;
    private DrawerLayout drawerMenuLayout;
    private Boolean isMenuItemClicked = false;
    SessionManager sessionManager;

    Toolbar toolbar;
    TextView textViewToolbarTitle;
    TextView textUserName;
    Intent intent;
    String patient_id, patient_name;
    public static String contactNo;


    private static final int PERMISSION_REQUEST_CODE = 1;
    long mStartTimestamp;
    boolean mShouldLog;
    int mCountAccelUpdates;
    LocationManager mLocationManager = null;

    String fitbitToken, fitbitUid, currentDate;
    // private GoogleApiClient client;  sobra al estar deprecated la GoogleApiClient se sustituye por la siguiente:
    private GoogleApi client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);

        setListeners();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sessionManager = new SessionManager(PatientMainActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        //System.out.println("Emer Contact from shared pref:"+emergencyContact.get(SessionManager.EMERGENCY_CONTACT));
        //contactNo=emergencyContact.get(SessionManager.EMERGENCY_CONTACT);
        patient_id = user.get(SessionManager.KEY_ID);
        patient_name = user.get(SessionManager.KEY_NAME);

        TextView name = (TextView) findViewById(R.id.username);
        name.setText(patient_name);

        fitbitToken = sessionManager.getFitbitToken();
        fitbitUid = sessionManager.getFitbitUid();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDateTime[] = sdf.format(calendar.getTime()).split(" ");
        currentDate = strDateTime[0];

        if (savedInstanceState == null) {
            Fragment fragment = new PatientDashboardFragment();
            Bundle bundle = new Bundle();
            bundle.putString("token", fitbitToken);
            bundle.putString("user_id", fitbitUid);
            bundle.putString("date", currentDate);
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.patient_content_frame, fragment).commit();
        }

        verificarPermisos();

        mShouldLog = false;
        mCountAccelUpdates = 0;
        mStartTimestamp = System.currentTimeMillis();

        new AsyncTaskCheckEmergency().execute(Integer.parseInt(patient_id));

        Intent intent = new Intent(this, FallDetectService.class);
        startService(intent);

        registerReceiver(accelDataReceiver, new IntentFilter(FallDetectService.ACCEL_DATA_NOTIFICATION));
        registerReceiver(fallDetectionReceiver, new IntentFilter(FallDetectService.FALL_NOTIFICATION));

        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //client = new GoogleApi. Builder(this).addApi(AppIndex.API).build();
        // implementation 'com.google.firebase:firebase-appindexing:20.0.0'   en build graddle
    }

    // verificamos los permisos para enviar SMS y localización.

    private void verificarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {
                    // No explanation needed, we can request the permission.

                    Log.d("permission", "permission denied to SEND_SMS - requesting it");
                    String[] permissions = {Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);

                    //ActivityCompat.requestPermissions(this,
                    //        new String[]{Manifest.permission.READ_CONTACTS},
                    //        1);    está definido con la variable PERMISSION_REQUEST_CODE

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.

                }
            }
        }
    }

//  ===========   Establecemos los listeners ===================================
    private void setListeners() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        textViewToolbarTitle.setText(getResources().getString(R.string.home_activity_title));
        textUserName = (TextView) findViewById(R.id.username);

        drawerMenuLayout = (DrawerLayout) findViewById(R.id.drawer_menu_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerTitleArray = getResources().getStringArray(R.array.pat_nav_drawer_items);
        drawerIconsArray = getResources().obtainTypedArray(R.array.pat_nav_drawer_icons);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[0], drawerIconsArray.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[1], drawerIconsArray.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[2], drawerIconsArray.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[3], drawerIconsArray.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[4], drawerIconsArray.getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[5], drawerIconsArray.getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[6], drawerIconsArray.getResourceId(6, -1)));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[7], drawerIconsArray.getResourceId(7, -1)));
        drawerIconsArray.recycle();
        menuListAdapter = new NavigationListAdapter(getApplicationContext(), navDrawerItems);
        drawerList.setAdapter(menuListAdapter);

        drawerToggle = new ActionBarDrawerToggle(this, drawerMenuLayout, toolbar, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {

                if (isMenuItemClicked) {
                    int position = drawerList.getCheckedItemPosition();
                    displayActivity(position);
                    isMenuItemClicked = false;
                }
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        drawerMenuLayout.addDrawerListener(drawerToggle);
        drawerList.setOnItemClickListener(new MenuItemClickListener());
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    private class MenuItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            isMenuItemClicked = true;
            //drawerList.setItemChecked(position,true);
            //drawerList.setSelection(position);
            drawerMenuLayout.closeDrawer(GravityCompat.START);
            //displayActivity(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //PatientDashboardFragment patientFragment=(PatientDashboardFragment) getSupportFragmentManager().findFragmentById(R.id.patient_content_frame);
        //patientFragment.updatePatientDashboard(fitbitToken,fitbitUid,currentDate);


        /*Fragment frag=getSupportFragmentManager().findFragmentById(R.id.patient_content_frame);
        if(frag instanceof PatientDashboardFragment){
            PatientDashboardFragment patientFragment=(PatientDashboardFragment)frag;
            patientFragment.updatePatientDashboard(fitbitToken,fitbitUid,currentDate);
        }*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(accelDataReceiver);
        //unregisterReceiver(fallDetectionReceiver);
    }


    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, FallDetectService.class);
        stopService(intent);
        super.onDestroy();
        unregisterReceiver(accelDataReceiver);
        unregisterReceiver(fallDetectionReceiver);
    }

    public void displayActivity(int position) {
        switch (position) {
            case 1:
                intent = new Intent(this, PatientAppointmentActivity.class);
                intent.putExtra("PatientId", patient_id);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(PatientMainActivity.this, DoctorActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(PatientMainActivity.this, ReminderMainActivity.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(PatientMainActivity.this, ActivityHealthDataRequests.class);
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(this, ManageEmergencyContactActivity.class);
                intent.putExtra("PatientId", patient_id);
                startActivity(intent);
                break;
            case 6:
                Intent intent = new Intent(PatientMainActivity.this, PatientSourceActivity.class);
                startActivity(intent);
                break;
            case 7:
                sessionManager.logoutUser();
                break;
        }

    }


    private BroadcastReceiver fallDetectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            long currentTime = System.currentTimeMillis();

            sendSMS();

        }
    };

    private BroadcastReceiver accelDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Double accelData = intent.getDoubleExtra(FallDetectService.ACCEL_DATA_KEY, 0);

            if (mShouldLog) {

                long currentTime = System.currentTimeMillis();
                if (mCountAccelUpdates < 100) {
                    mCountAccelUpdates++;
                } else {
                    mCountAccelUpdates = 1;

                }

            }
        }
    };


    public void sendSMS() {
        ArrayList<String> location = getLocation();
        try {
            System.out.println("Em contact: " + contactNo);
            SmsManager.getDefault().sendTextMessage(contactNo, null, "Alert:Patient has fallen, attention needed!" + "\n Location:http://maps.google.com/?q=" + location.get(0) + "," + location.get(1), null, null);
            Toast.makeText(getApplicationContext(), "Alert message sent to emergency contact", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.setMessage(e.getMessage());
            dialog.show();
        }

    }

    public ArrayList<String> getLocation() {

    Location currentlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Double lat = currentlocation.getLatitude();
        Double lng = currentlocation.getLongitude();
        String lat_str = lat.toString();
        String lng_str = lng.toString();

        ArrayList<String> location = new ArrayList();
        location.add(0, lat_str);
        location.add(1, lng_str);

        return location;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onFragmentInteraction() {

    }

    public class AsyncTaskCheckEmergency extends AsyncTask<Integer, String, ArrayList> {

        HttpResponse response;
        SessionManager session;
        ArrayList<String> emergency = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Integer... params) {
            try {


                String Url = ConfigConstant.BASE_URL + ConfigConstant.PATIENT_EMERGENCY + params[0];
                HttpGet get = new HttpGet(Url);
                HttpClient httpClient = new DefaultHttpClient();
                response = httpClient.execute(get);
                System.out.println("Reached after coming back from Backend API");
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed: HTTP error code :" + response.getStatusLine().getStatusCode());
                } else {
                    HttpEntity e = response.getEntity();
                    String i = EntityUtils.toString(e);
                    System.out.println(i);
                    JSONObject j = new JSONObject(i);
                    if (j.length() == 0) {
                        System.out.println("No emergency contact");
                    } else {
                        emergency.add(j.getString("Contact"));
                        emergency.add(j.getString("Dependent_Id"));
                    }
                }
            } catch (Exception x) {
                throw new RuntimeException("Error from get emergency contact api", x);
            }

            return emergency;
        }

        @Override
        protected void onPostExecute(ArrayList E) {
            super.onPostExecute(E);
            if (!E.isEmpty()) {
                session = new SessionManager(getApplicationContext());
                System.out.println(E.get(0).toString() + E.get(1).toString());
                session.createEmergencyContact(E.get(0).toString(), E.get(1).toString());
                HashMap<String, String> emergencyContact = sessionManager.getEmergencyContact();
                //contactNo = emergencyContact.get(SessionManager.EMERGENCY_CONTACT);
                contactNo = E.get(0).toString();
            } else {
                //AlertDialogManager alert = new AlertDialogManager();
                //alert.showAlertDialog(PatientMainActivity.this, "Kindly Add Emergency Contact!", "Navigate to Emergency Contact on the Side Menu", false);
//new android.support.v7.app.AlertDialog.Builder(PatientMainActivity.this).setTitle("Add Emergency Contact")
                new AlertDialog.Builder(PatientMainActivity.this).setTitle("Add Emergency Contact")
                        .setMessage("Navigate to Emergency Contact on the Side Menu")
                        .setPositiveButton(R.string.apmt_dialog_btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                Intent intent = new Intent(PatientMainActivity.this, ManageEmergencyContactActivity.class);
                                intent.putExtra("PatientId", patient_id);
                                startActivity(intent);
                            }
                        })
                        .show();
                //TODO goto ManageEmergencyContactActivity
            }
        }
    }

}
