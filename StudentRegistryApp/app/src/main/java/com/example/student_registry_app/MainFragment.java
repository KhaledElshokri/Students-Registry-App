package com.example.student_registry_app;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainFragment extends DialogFragment {

    public interface OnProfileSavedListener {
        void onProfileSaved();
    }


    protected EditText surNameEditText, NameEditText, IdEditText, GpaEditText;
    protected Button saveButton, cancelButton;
    private DatabaseHelper databaseHelper;
    private OnProfileSavedListener listener;

    public MainFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        surNameEditText = view.findViewById(R.id.editText_surname);
        NameEditText = view.findViewById(R.id.editText_name);
        IdEditText = view.findViewById(R.id.editText_ID);
        GpaEditText = view.findViewById(R.id.editText_GPA);
        saveButton = view.findViewById(R.id.save_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        // Setting Hints
        surNameEditText.setHint("Write surname here");
        NameEditText.setHint("Write name here");
        IdEditText.setHint("Write student ID here");
        GpaEditText.setHint("Write student GPA here");

        // Optionally set background color and dim the view
        ConstraintLayout layout = view.findViewById(R.id.fragment);
        layout.setBackgroundColor(Color.TRANSPARENT); // Set transparent to allow blur

        // Init DataBase
        databaseHelper = new DatabaseHelper(getContext());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Get the dialog window
        if (getDialog() != null) {
            // Retrieve the current window's attributes
            Window window = getDialog().getWindow();
            if (window != null) {
                // Get screen dimensions
                DisplayMetrics displayMetrics = new DisplayMetrics();
                window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;

                // Set the size to a percentage of the screen size
                int dialogWidth = (int) (screenWidth * 0.8); // 80% of screen width
                int dialogHeight = (int) (screenHeight * 0.65); // 60% of screen height

                // Apply the calculated dimensions
                window.setLayout(dialogWidth, dialogHeight);
            }
        }
    }

    private void saveProfile() {
        // Get input values
        String name = NameEditText.getText().toString().trim();
        String surname = surNameEditText.getText().toString().trim();
        String studentID = IdEditText.getText().toString().trim();
        String gpaString = GpaEditText.getText().toString().trim();

        // Validate input fields
        if (name.isEmpty() || surname.isEmpty() || studentID.isEmpty() || gpaString.isEmpty()) {
            showToast("Please fill in all fields");
            return;
        }

        // Parse GPA input
        double gpa;
        try {
            gpa = Double.parseDouble(gpaString);
            if (gpa < 0.0 || gpa > 4.3) {
                showToast("GPA must be between 0.0 and 4.0");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid GPA value");
            return;
        }

        // Parse Student ID input
        int sID;
        try {
            sID = Integer.parseInt(studentID);
            if (databaseHelper.isStudentIDExists(sID))
            {
                showToast("Student ID Already Exists!!!");
                return;
            }
            else if(sID < 10000000 || sID > 99999999)
            {
                showToast("Invalid Student ID!!!");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid Student ID value");
            return;
        }


        // Get the current date as the creation date
        String creationDate = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());

        // Insert the profile into the database
        databaseHelper.addProfile(name, surname, gpa, creationDate, sID);

        int profIDfromSID = databaseHelper.getProfileIDByStudentID(sID);
        databaseHelper.addAccess(profIDfromSID, "Created", new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime()));

        if (listener != null) {
            listener.onProfileSaved();
        }

        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnProfileSavedListener) context;  // Attach the listener
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnProfileSavedListener");
        }
    }

    // Helper method to show toasts safely
    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

}

