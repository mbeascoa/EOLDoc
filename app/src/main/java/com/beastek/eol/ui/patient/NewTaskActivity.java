package com.beastek.eol.ui.patient;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;


import com.beastek.eol.R;
import com.beastek.eol.data.TaskDBHelper;
import com.beastek.eol.model.Task;
import com.beastek.eol.util.DateUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import java.util.Calendar;
import java.util.List;

// Using SQLLite for keeping tasks.

public class NewTaskActivity extends AppCompatActivity {
    // public class NewTaskActivity extends AppCompatActivity implements Validator.ValidationListener {
    private Calendar calendar;

    private TextInputLayout titleTaskInputLayout, descriptionTaskInputLayout, dateTaskInputLayout;
    private TextInputEditText titleTask, descriptionTask, dateView;

    private View view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleTaskInputLayout = (TextInputLayout) findViewById(R.id.title_label);
        titleTask = (TextInputEditText) findViewById(R.id.title);
        descriptionTaskInputLayout = (TextInputLayout) findViewById(R.id.description_label);
        descriptionTask = (TextInputEditText) findViewById(R.id.description);
        dateTaskInputLayout = (TextInputLayout) findViewById(R.id.date_label);
        dateView = (TextInputEditText) findViewById(R.id.date);

    }

    // menu _ new_ task .xml  options ; save and cancel
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    // menu _ new_ task .xml  options ; save and cancel
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cancel) {
            this.finish();
            return true;
        } else if (id == R.id.action_save) {
            //validator.validate();
            titleTaskInputLayout = (TextInputLayout) findViewById(R.id.title_label);
            titleTask = (TextInputEditText) findViewById(R.id.title);
            descriptionTaskInputLayout = (TextInputLayout) findViewById(R.id.description_label);
            descriptionTask = (TextInputEditText) findViewById(R.id.description);
            if (!validateTitle()) {
                return true;
            }
            if (!validateDescription()) {
                return true;
            }

            Task task = new Task(titleTask.getText().toString(), descriptionTask.getText().toString(), null, false);
            if (calendar != null) {
                task.setDate(calendar.getTime());
            }
            SaveTask saveTask = new SaveTask(this);
            saveTask.execute(task);
            this.finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // when button DatePicker is pressed
    public void showDatePickerDialog(View v) {
        Calendar calendarTemp;
        if (calendar == null) {
            calendarTemp = Calendar.getInstance();
        } else {
            calendarTemp = this.calendar;
        }
        DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datepic, int year, int month, int day) {
                if (calendar == null) {
                    calendar = Calendar.getInstance();
                }
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateTime();
                //we allow the remove date buttom to appear so you can clear the date when you are editing
                ImageButton button = (ImageButton) findViewById(R.id.button_remove_date);
                button.setVisibility(View.VISIBLE);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, calendarTemp.get(Calendar.YEAR), calendarTemp.get(Calendar.MONTH), calendarTemp.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // when imagebutton remove date pressed then remove date, is hidden if not date or time selected
    public void removeDate(View v) {
        ImageButton button = (ImageButton) findViewById(R.id.button_remove_date);
        button.setVisibility(View.GONE);
        calendar = null;
        updateTime();
    }

    //when SetAlarm Button is pressed.....goes to AlarmActivity.class
    public void setAlarm(View v) {
        Intent intent = new Intent(NewTaskActivity.this, AlarmActivity.class);
        startActivity(intent);
    }

    // when image button is pressed TimePickerDialog
    public void showTimePickerDialog(View v) {
        Calendar calendarTemp;
        if (calendar == null) {
            calendarTemp = Calendar.getInstance();
        } else {
            calendarTemp = this.calendar;
        }
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                if (calendar == null) {
                    calendar = Calendar.getInstance();
                }
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, m);
                updateTime();
                ImageButton button = (ImageButton) findViewById(R.id.button_remove_date);
                button.setVisibility(View.VISIBLE);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, myTimeListener, calendarTemp.get(Calendar.HOUR_OF_DAY), calendarTemp.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public void updateTime() {

        if (calendar != null) {
            dateView.setText(new DateUtil(this).parse(calendar.getTime()));
        } else {
            dateView.setText("");
        }
    }


    //saves task in sqlite using TaskDBHelper
    private class SaveTask extends AsyncTask<Task, Void, Boolean> {

        private Context context;

        public SaveTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(final Task... tasks) {
            int rowInserted = 0;
            for (Task task : tasks) {
                TaskDBHelper taskDBHelper = TaskDBHelper.getInstance(this.context);
                Long newRowId = taskDBHelper.insert(task);
                rowInserted += newRowId;
            }
            return rowInserted > 0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    //needed for TextWatcher
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // textwatcher activity for TextIpuntValidation
    private class ValidationTextWatcher implements TextWatcher {
        private View view;
        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.title:
                    validateTitle();
                    break;
                case R.id.description:
                    validateDescription();
                    break;
            }
        }
    }

    // Title Validation not empty
    private boolean validateTitle() {
        if (titleTask.getText().toString().trim().isEmpty()) {
            titleTaskInputLayout.setError("Title is required");
            requestFocus(titleTask);
            return false;
        }else if(titleTask.getText().toString().length() < 4){
            titleTaskInputLayout.setError("Title can't be less than 4 letters");
            requestFocus(titleTask);
            return false;
        }
        else {
            titleTaskInputLayout.setErrorEnabled(false);
        }
        return true;
    }


    // Description Validation not empty
    private boolean validateDescription() {
        if (descriptionTask.getText().toString().trim().isEmpty()) {
            descriptionTaskInputLayout.setError("Description is required");
            requestFocus(descriptionTask);
            return false;
        }else if(descriptionTask.getText().toString().length() < 3){
            descriptionTaskInputLayout.setError("Description can't be less than 6 letters");
            requestFocus(descriptionTask);
            return false;
        }
        else {
            descriptionTaskInputLayout.setErrorEnabled(false);
        }
        return true;
    }

}
