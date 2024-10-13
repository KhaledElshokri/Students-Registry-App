package com.example.student_registry_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements MainFragment.OnProfileSavedListener {

    protected DatabaseHelper DBH;
    protected TableLayout studentsTable;
    protected List<Profile> studentsListBySur;
    protected List<Profile> studentsListByID;
    protected TextView totalDisplay;
    protected int toggleBit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Page Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting Up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Registry");      // Sets a title for the toolbar
        }

        // Set an OnClickListener on the button
        FloatingActionButton addProfile_b = findViewById(R.id.floatingActionButton);
        addProfile_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create and show the AddProfileDialogFragment
                MainFragment dialogFragment = new MainFragment();
                dialogFragment.show(getSupportFragmentManager(), "MainFragment");
            }
        });

        // Initialize DatabaseHelper
        studentsTable = findViewById(R.id.table_layout_main);
        DBH = new DatabaseHelper(this);

        // Init top display
        totalDisplay = findViewById(R.id.textView_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStudentList();  // Refresh the list when the activity is resumed
    }

    // Inflate the menu; this adds items to the action bar if present
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);  // Inflate the menu_main.xml
        return true;
    }

    // Handle toolbar menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();

        if (id == R.id.toggle_main) {
            // Handle toggle option click
            toggleMode();
        }
        return super.onOptionsItemSelected((android.view.MenuItem) item);
    }

    public void refreshStudentList()
    {
        // Clear the table first to avoid duplicating entries
        studentsTable.removeAllViews();

        // Re-fetch the student list from the database
        studentsListBySur = DBH.getAllProfilesSortedBySurname();
        studentsListByID = DBH.getAllProfilesSortedByID();

        totalDisplay.setText(studentsListBySur.size() + " Profiles, by Surname");

        if (!studentsListBySur.isEmpty())
        {
            int studentCount = 0;

            for(Profile itr: studentsListBySur)
            {
                studentCount++;

                // Create a new TableRow
                TableRow tableRow;
                tableRow = new TableRow(this);

                // Create a new TextView for the event string
                TextView textView = new TextView(this);
                textView.setText(studentCount + ". " + itr.getSurname() + ", " + itr.getName());

                textView.setPadding(16, 16, 16, 16);  // Set padding for better readability

                // Add the TextView to the TableRow
                tableRow.addView(textView);

                // set a background for each row
                tableRow.setBackgroundResource(android.R.drawable.dialog_holo_light_frame); // Optional styling

                // Add OnClickListener for each TableRow
                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Start ProfileActivity and pass the profile ID
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.putExtra("profileID", itr.getProfileID());  // Pass the profile ID
                        DBH.addAccess(itr.getProfileID(),"Opened", new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime()));
                        startActivity(intent);
                    }
                });

                // Add the TableRow to the TableLayout
                studentsTable.addView(tableRow);
            }
        }
    }

    protected void toggleMode()
    {
        if(toggleBit == 0)
        {
            toggleBit = 1; // Setting this mode to 1

            studentsTable.removeAllViews();

            totalDisplay.setText(studentsListByID.size() + " Profiles, by ID");

            int studentCount = 0;

            if (!studentsListByID.isEmpty())
            {
                for(Profile itr: studentsListByID)
                {
                    studentCount++;
                    // Create a new TableRow
                    TableRow tableRow;
                    tableRow = new TableRow(this);

                    // Create a new TextView for the event string
                    TextView textView = new TextView(this);
                    textView.setText(studentCount + ". " + itr.getStudentID());

                    // Set padding for better readability
                    textView.setPadding(16, 16, 16, 16);

                    // Add the TextView to the TableRow
                    tableRow.addView(textView);

                    // set a background for each row
                    tableRow.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

                    // Add OnClickListener for each TableRow
                    tableRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Start ProfileActivity and pass the profile ID
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            intent.putExtra("profileID", itr.getProfileID());  // Pass the profile ID
                            DBH.addAccess(itr.getProfileID(),"Opened", new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime()));
                            startActivity(intent);
                        }
                    });
                    // Add the TableRow to the TableLayout
                    studentsTable.addView(tableRow);
                }

            }


        }
        else
        {
            toggleBit = 0;

            studentsTable.removeAllViews();

            totalDisplay.setText(studentsListBySur.size() + " Profiles, by Surname");

            int studentCount = 0;

            if (!studentsListBySur.isEmpty())
            {
                for(Profile itr: studentsListBySur)
                {
                    studentCount++;
                    // Create a new TableRow
                    TableRow tableRow;
                    tableRow = new TableRow(this);

                    // Create a new TextView for the event string
                    TextView textView = new TextView(this);
                    textView.setText(studentCount + ". " + itr.getSurname() + ", " + itr.getName());

                    // Set padding for better readability
                    textView.setPadding(16, 16, 16, 16);

                    // Add the TextView to the TableRow
                    tableRow.addView(textView);

                    // Optionally, you can set a background or other styling for each row
                    tableRow.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

                    // Add OnClickListener for each TableRow
                    tableRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Start ProfileActivity and pass the profile ID
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            intent.putExtra("profileID", itr.getProfileID());  // Pass the profile ID
                            DBH.addAccess(itr.getProfileID(),"Opened", new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime()));
                            startActivity(intent);
                        }
                    });
                    // Add the TableRow to the TableLayout
                    studentsTable.addView(tableRow);
                }
            }
        }
    }

    @Override
    public void onProfileSaved() {
        // Trigger onResume by restarting the activity
        finish();  // Close the current activity
        startActivity(getIntent());  // Start the activity again
    }
}