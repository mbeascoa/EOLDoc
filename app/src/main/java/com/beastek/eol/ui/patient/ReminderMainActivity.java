package com.beastek.eol.ui.patient;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.beastek.eol.R;
import com.beastek.eol.adapter.MainFragmentPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class ReminderMainActivity extends AppCompatActivity {

    private FragmentPagerAdapter adapterViewPager;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ViewPager mVpPager;
    private Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_main);


        mVpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MainFragmentPagerAdapter(fragmentManager);
        mVpPager.setAdapter(adapterViewPager);

        act = this;

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reminder List");
        getSupportActionBar().setIcon(R.mipmap.logo);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.pending)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.done)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.all)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ReminderMainActivity.this.adapterViewPager.notifyDataSetChanged();
                mVpPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mVpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
                ReminderMainActivity.this.adapterViewPager.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    ReminderMainActivity.this.adapterViewPager.notifyDataSetChanged();
                }
            }
        });

        //El floating action button crea una nueva tarea (task) utilizando SQLLite
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewTaskActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Let.handle(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
*/

    // 00 método  para infrar el menu superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //00 método para gestionar el item seleccionado

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent alarm = new Intent(this, AlarmActivity.class);
            this.startActivity(alarm);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}






/*

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        Intent accion;

        if (id== R.id.item_consultar)
        {
            Intent i = new Intent(this, Listado_registros.class);
            startActivity(i);

        }else if (id== R.id.item_alta_registro) {
            Intent i = new Intent(this, Alta_registro.class);
            startActivity(i);
        } else if( id== R.id.item_signup){
            Intent i = new Intent(this, SignUp_Registro.class);
            startActivity(i);
        }else if (id== R.id.item_navegar){

            //accion = new Intent("android.intent.action.VIEW", Uri.parse("http://developer.android.com"));
            accion = new Intent(this, Buscarporid.class);
            startActivity(accion);
        }
        return true;
    }

     */

