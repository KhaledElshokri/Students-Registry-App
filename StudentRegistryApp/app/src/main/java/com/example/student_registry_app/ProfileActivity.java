package com.example.student_registry_app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    protected TableLayout accessTable;
    protected TextView textViewName, textViewSurname, textViewID, textViewGPA, textViewCreation;
    protected Button deleteButton;
    protected DatabaseHelper databaseHelper;
    protected Profile profile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Retreive passed profile ID
        int profileID = getIntent().getIntExtra("profileID", -1);

        // Fetch the profile
        databaseHelper = new DatabaseHelper(this);
        profile = databaseHelper.getProfileByID(profileID);

        // Initialize UI elements
        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        // Enable the "Up" button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Shows the Up button in the toolbar
            getSupportActionBar().setTitle("Profile Activity");      // Sets a title for the toolbar
        }

        accessTable = findViewById(R.id.access_table);
        textViewName = findViewById(R.id.textView_name);
        textViewSurname = findViewById(R.id.textView_surname);
        textViewID = findViewById(R.id.textView_ID);
        textViewGPA = findViewById(R.id.textView_GPA);
        textViewCreation = findViewById(R.id.textView_creation);

        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteProfile(profile.getProfileID());
                finish();
            }
        });

        if (profile != null)
        {
            textViewName.setText("Name: " + profile.getName());
            textViewSurname.setText("Surname: " + profile.getSurname());
            textViewID.setText("ID: " + profile.getStudentID());
            textViewGPA.setText("GPA: " + profile.getGPA());
            textViewCreation.setText("Profile Created: " + profile.getCreationDate());

            List<Access> accesslist = databaseHelper.getAccessByProfileID(profile.getProfileID());

            for (Access event : accesslist) {
                // Create a new TableRow
                TableRow tableRow;
                tableRow = new TableRow(this);

                // Create a new TextView for the event string
                TextView textView = new TextView(this);
                textView.setText(event.getTimestamp() + " " + event.getAccessType());

                textView.setPadding(16, 16, 16, 16);  // Set padding for better readability

                // Add the TextView to the TableRow
                tableRow.addView(textView);

                // set a background for each row
                tableRow.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

                // Add the TableRow to the TableLayout
                accessTable.addView(tableRow);
            }
        }


    }

    // Handle "Up" button behavior
    @Override
    public boolean onSupportNavigateUp() {
        // Finish current activity and go back to the previous one
        databaseHelper.addAccess(profile.getProfileID(), "Closed",new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime()) );
        finish();
        return true;
    }

}
