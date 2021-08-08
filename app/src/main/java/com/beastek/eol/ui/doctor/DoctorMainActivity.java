package com.beastek.eol.ui.doctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.beastek.eol.Extras.Interface.DocDashboardFragmentToActivity;
import com.beastek.eol.R;
import com.beastek.eol.adapter.NavigationListAdapter;
import com.beastek.eol.data.NavDrawerItem;
import com.beastek.eol.ui.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

// viene de launcheractivity una vez que sabemos que es doctor y está registrado y se logeo bien
//aplica interfaz on fragmentiteration()
public class DoctorMainActivity extends AppCompatActivity implements DocDashboardFragmentToActivity {

    private String[] drawerTitleArray;
    private TypedArray drawerIconsArray;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationListAdapter menuListAdapter;
    private ListView drawerList;
    private DrawerLayout drawerMenuLayout;
    private Boolean isMenuItemClicked=false;

    String doctor_name;
    Toolbar toolbar;
    TextView textViewToolbarTitle;
    TextView textUserName;
    SessionManager sessionManager;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        setListeners();

        if(savedInstanceState==null){
            Fragment fragment=new DoctorDashboardFragment();
            FragmentManager fragmentManager=getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.doctor_content_frame,fragment).commit();
        }


    }

    private void setListeners(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewToolbarTitle=(TextView)findViewById(R.id.toolbar_title);

        sessionManager=new SessionManager(DoctorMainActivity.this);
        HashMap<String,String> user=sessionManager.getUserDetails();
        doctor_name = user.get(SessionManager.KEY_NAME);
        textViewToolbarTitle.setText(getResources().getString(R.string.home_activity_title));
        textUserName=(TextView)findViewById(R.id.username);
        textUserName.setText("Dr. " +doctor_name);

        drawerMenuLayout=(DrawerLayout)findViewById(R.id.drawer_menu_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerTitleArray=getResources().getStringArray(R.array.doc_nav_drawer_items);
        drawerIconsArray=getResources().obtainTypedArray(R.array.doc_nav_drawer_icons);


        navDrawerItems=new ArrayList<NavDrawerItem>();

        int icono1 = drawerIconsArray.getResourceId(0, -1);
        @SuppressLint("ResourceType") int icono2 = drawerIconsArray.getResourceId(1, -1);
        @SuppressLint("ResourceType")  int icono3 = drawerIconsArray.getResourceId(2, -1);
        @SuppressLint("ResourceType") int icono4 = drawerIconsArray.getResourceId(3, -1);


        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[0], icono1));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[1], icono2));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[2], icono3));
        navDrawerItems.add(new NavDrawerItem(drawerTitleArray[3], icono4));

        menuListAdapter=new NavigationListAdapter(getApplicationContext(),navDrawerItems);
        drawerList.setAdapter(menuListAdapter);

        drawerToggle=new ActionBarDrawerToggle(this,drawerMenuLayout,toolbar,R.string.app_name,R.string.app_name){


            public void onDrawerClosed(View view) {

                if(isMenuItemClicked) {
                    int position=drawerList.getCheckedItemPosition();
                    displayActivity(position);
                    isMenuItemClicked=false;
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

    public void displayActivity(int position){
        switch (position){
            case 1:
                Intent intent=new Intent(DoctorMainActivity.this,AppointmentActivity.class);
                startActivity(intent);
                break;
            case 2:
                Intent pat_intent=new Intent(DoctorMainActivity.this,PatientActivity.class);
                startActivity(pat_intent);
                break;
            case 3:
                sessionManager.logoutUser();
                break;
        }

    }

    private class MenuItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){

            isMenuItemClicked=true;
            //drawerList.setItemChecked(position,true);
            //drawerList.setSelection(position);
            drawerMenuLayout.closeDrawer(GravityCompat.START);
            //displayActivity(position);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }


    @Override
    public void onFragmentInteraction() {

    }
}
