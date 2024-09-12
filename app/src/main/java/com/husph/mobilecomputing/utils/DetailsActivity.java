package com.husph.mobilecomputing.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.husph.mobilecomputing.R;
import com.husph.mobilecomputing.RegisterActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    final static String TAG = "DetailsActivity";




    private final Map<String, String[]> countryToStates = Map.of(
            "Philippines", new String[]{"Ilocos Norte", "Cebu", "Davao", "Laguna", "Pampanga"},
            "USA", new String[]{"California", "Texas", "New York", "Florida", "Illinois"},
            "India", new String[]{"Maharashtra", "Karnataka", "Tamil Nadu", "West Bengal", "Gujarat"},
            "Australia", new String[]{"New South Wales", "Victoria", "Queensland", "South Australia", "Western Australia"},
            "Canada", new String[]{"Ontario", "Quebec", "British Columbia", "Alberta", "Manitoba"}
    );

    String[] countries;
    String[] states;

    private Spinner spn_state;
    private AutoCompleteTextView act_country;
    private TextView tv_birthDate;
    private TextView tv_birthTime;
    private Button btn_pickBirthDate;
    private Button btn_pickBirthTime;
    private Button btn_finish_register;
    private RadioButton rb_male;
    private RadioButton rb_female;
    private RadioGroup rg_gender;
    private TextInputEditText et_username;
    private TextInputEditText et_phone_number;
    private EditText et_interests;

    private ArrayAdapter stateArrAdapter, countryArrAdapter;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitializeComponents();

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                final String username = et_username.getText().toString().trim();
                final  String phoneNumber = et_phone_number.getText().toString().trim();
                final  String country = act_country.getText().toString().trim();
                final String state = spn_state.getSelectedItem().toString().toString();
                final String gender = String.valueOf(rg_gender.getCheckedRadioButtonId() == R.id.rb_female ? rb_female.getText() : rb_male.getText());
                final String birthDate = tv_birthDate.getText().toString().trim();
                final String birthTime = tv_birthTime.getText().toString().trim();
                final String interests = et_interests.getText().toString().trim();
                FormValidation.FormValidationResult registerFormResult = FormValidation.isDetailsFormValid(
                        username,
                        phoneNumber,
                        country,
                        gender,
                        birthDate,
                        birthTime,
                        interests,
                        countryToStates.keySet()

                );

                String message = "";

                switch (registerFormResult) {
                    case INVALID_USERNAME:
                        message = FormValidation.WarningMessage.INVALID_USERNAME.getMessage();
                        break;
                    case INVALID_PHONE_NUMBER:
                        message = FormValidation.WarningMessage.INVALID_PHONE_NUMBER.getMessage();
                        break;
                    case INVALID_COUNTRY:
                        message = FormValidation.WarningMessage.INVALID_COUNTRY.getMessage();
                        break;
                    case INVALID_STATE:
                        message = FormValidation.WarningMessage.INVALID_STATE.getMessage();
                        break;
                    case INVALID_GENDER:
                        message = FormValidation.WarningMessage.INVALID_GENDER.getMessage();
                        break;
                    case INVALID_BIRTH_DATE:
                        message = FormValidation.WarningMessage.INVALID_BIRTH_DATE.getMessage();
                        break;
                    case INVALID_BIRTH_TIME:
                        message = FormValidation.WarningMessage.INVALID_BIRTH_TIME.getMessage();
                        break;
                }

                if(!TextUtils.isEmpty(message)) {
                    Toast.makeText(DetailsActivity.this, message, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private void btn_pickBirthDate_onClickEvent() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog birthDatePicker = new DatePickerDialog(DetailsActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = " " + selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tv_birthDate.setText(selectedDate);
                }, year, month, day
        );

        birthDatePicker.show();
    }

    private void btn_pickBirthTime_onClickEvent() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(DetailsActivity.this,
                (view, selectedHour, selectedMinute) -> {

                    String amPm;
                    if (selectedHour >= 12) {
                        amPm = "PM";
                        if (selectedHour > 12) {
                            selectedHour -= 12; // Convert 24-hour to 12-hour format
                        }
                    } else {
                        amPm = "AM";
                        if (selectedHour == 0) {
                            selectedHour = 12; // Midnight case
                        }
                    }

                    String formattedSelectedMinute = selectedMinute < 10 ? ("0" + selectedMinute) : String.valueOf(selectedMinute);
                    String selectedTime = " " + selectedHour + ":" + formattedSelectedMinute + " " + amPm;
                    tv_birthTime.setText(selectedTime);
                }, hour, minute, false);

        timePickerDialog.show();

    }

    private void btn_finish_register_OnClickEvent() {
        final String username = et_username.getText().toString().trim();
        final String phoneNumber = et_phone_number.getText().toString().trim();
        final String country = act_country.getText().toString().trim();
        final String state = spn_state.getSelectedItem().toString().toString();
        final String gender = String.valueOf(rg_gender.getCheckedRadioButtonId() == R.id.rb_female ? rb_female.getText() : rb_male.getText());
        final String birthDate = tv_birthDate.getText().toString().trim();
        final String birthTime = tv_birthTime.getText().toString().trim();
        final String interests = et_interests.getText().toString().trim();

        FormValidation.FormValidationResult registerFormResult = FormValidation.isDetailsFormValid(
                username,
                phoneNumber,
                country,
                gender,
                birthDate,
                birthTime,
                interests,
                countryToStates.keySet()
        );

        String message = "";

        switch (registerFormResult) {
            case INVALID_USERNAME:
                message = FormValidation.WarningMessage.INVALID_USERNAME.getMessage();
                break;
            case INVALID_PHONE_NUMBER:
                message = FormValidation.WarningMessage.INVALID_PHONE_NUMBER.getMessage();
                break;
            case INVALID_COUNTRY:
                message = FormValidation.WarningMessage.INVALID_COUNTRY.getMessage();
                break;
            case INVALID_STATE:
                message = FormValidation.WarningMessage.INVALID_STATE.getMessage();
                break;
            case INVALID_GENDER:
                message = FormValidation.WarningMessage.INVALID_GENDER.getMessage();
                break;
            case INVALID_BIRTH_DATE:
                message = FormValidation.WarningMessage.INVALID_BIRTH_DATE.getMessage();
                break;
            case INVALID_BIRTH_TIME:
                message = FormValidation.WarningMessage.INVALID_BIRTH_TIME.getMessage();
                break;
            case INVALID_INTERESTS:
                message = FormValidation.WarningMessage.INVALID_INTERESTS.getMessage();
                break;
        }

        if(!TextUtils.isEmpty(message)) {
            Toast.makeText(DetailsActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Information");

        String toastMessage = "Username: " + username + "\n" +
                "Phone Number: " + phoneNumber + "\n" +
                "Country: " + country + "\n" +
                "State: " + state + "\n" +
                "Gender: " + gender + "\n" +
                "Birth Date: " + birthDate + "\n" +
                "Birth Time: " + birthTime + "\n" +
                "Interests: " + interests;

// Set the toastMessage to the dialog
        builder.setMessage(toastMessage);

// Add an OK button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                // Handle OK button click
                dialog.dismiss();
                finish();
            }
        });

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void InitializeComponents() {
        countries = countryToStates.keySet().toArray(new String[0]);
        states = countryToStates.get("Philippines");

        spn_state = findViewById(R.id.spn_state);
        stateArrAdapter = new ArrayAdapter(DetailsActivity.this, android.R.layout.simple_list_item_1, states);
        spn_state.setAdapter(stateArrAdapter);

        act_country = findViewById(R.id.act_country);
        countryArrAdapter = new ArrayAdapter(DetailsActivity.this, android.R.layout.simple_list_item_1, countries);
        act_country.setAdapter(countryArrAdapter);

        tv_birthDate = findViewById(R.id.tv_birthDate);
        tv_birthTime = findViewById(R.id.tv_birthTime);

        btn_pickBirthDate = findViewById(R.id.btn_pickBirthDate);
        btn_pickBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_pickBirthDate_onClickEvent();
            }
        });

        btn_pickBirthTime = findViewById(R.id.btn_pickBirthTime);
        btn_pickBirthTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_pickBirthTime_onClickEvent();
            }
        });

        btn_finish_register = findViewById(R.id.btn_finish_register);
        btn_finish_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_finish_register_OnClickEvent();
            }
        });

        et_username = findViewById(R.id.et_username);
        et_phone_number = findViewById(R.id.et_phone_number);
        et_interests = findViewById(R.id.et_interests);

        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);
        rg_gender = findViewById(R.id.rg_gender);

        act_country.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String selectedKey = charSequence.toString();
                if (countryToStates.containsKey(selectedKey)) {
                    // Update the states array based on the selected country
                    states = countryToStates.get(selectedKey);

                    // Create a new adapter with the updated states array
                    stateArrAdapter = new ArrayAdapter<>(DetailsActivity.this, android.R.layout.simple_spinner_item, states);
                    stateArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Set the new adapter to the Spinner
                    spn_state.setAdapter(stateArrAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });



    }




}