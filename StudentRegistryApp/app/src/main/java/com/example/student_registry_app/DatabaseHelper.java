package com.example.student_registry_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "profileAccess.db";
    private static final int DATABASE_VERSION = 2;

    // Profile table
    private static final String TABLE_PROFILE = "Profile";
    private static final String COLUMN_PROFILE_ID = "ProfileID";
    private static final String COLUMN_STUDENT_ID = "StudentID";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_SURNAME = "Surname";
    private static final String COLUMN_GPA = "ProfileGPA";
    private static final String COLUMN_CREATION_DATE = "ProfileCreationDate";

    // Access table
    private static final String TABLE_ACCESS = "Access";
    private static final String COLUMN_ACCESS_ID = "AccessID";
    private static final String COLUMN_PROFILE_ID_FK = "ProfileID";  // Foreign Key
    private static final String COLUMN_ACCESS_TYPE = "AccessType";
    private static final String COLUMN_TIMESTAMP = "Timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Profile table with StudentID column
        String CREATE_PROFILE_TABLE = "CREATE TABLE " + TABLE_PROFILE + " ("
                + COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_SURNAME + " TEXT, "
                + COLUMN_GPA + " REAL, "
                + COLUMN_CREATION_DATE + " TEXT, "
                + COLUMN_STUDENT_ID + " INTEGER)";
        db.execSQL(CREATE_PROFILE_TABLE);

        // Create Access table
        String CREATE_ACCESS_TABLE = "CREATE TABLE " + TABLE_ACCESS + " ("
                + COLUMN_ACCESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PROFILE_ID_FK + " INTEGER, "
                + COLUMN_ACCESS_TYPE + " TEXT, "
                + COLUMN_TIMESTAMP + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_PROFILE_ID_FK + ") REFERENCES " + TABLE_PROFILE + "(" + COLUMN_PROFILE_ID + "))";
        db.execSQL(CREATE_ACCESS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }

    // Add a new profile
    public void addProfile(String name, String surname, double gpa, String creationDate, int studentID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SURNAME, surname);
        values.put(COLUMN_GPA, gpa);
        values.put(COLUMN_CREATION_DATE, creationDate);
        values.put(COLUMN_STUDENT_ID, studentID);

        db.insert(TABLE_PROFILE, null, values);
        db.close();
    }


    // Add a new access record
    public void addAccess(int profileID, String accessType, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_ID_FK, profileID);
        values.put(COLUMN_ACCESS_TYPE, accessType);
        values.put(COLUMN_TIMESTAMP, timestamp);

        db.insert(TABLE_ACCESS, null, values);
        db.close();
    }

    // Delete a profile by ProfileID
    public void deleteProfile(int profileID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCESS, COLUMN_PROFILE_ID_FK + " = ?", new String[]{String.valueOf(profileID)});
        db.delete(TABLE_PROFILE, COLUMN_PROFILE_ID + " = ?", new String[]{String.valueOf(profileID)});
        db.close();
    }

    // Get all profiles as a list of Profile objects
    public List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PROFILE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Profile profile = new Profile(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GPA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID))
                );
                profiles.add(profile);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return profiles;
    }

    public int getProfileIDByStudentID(int studentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        int profileID = -1; // Default value if not found

        // SQL query to search for ProfileID by StudentID
        Cursor cursor = db.query(TABLE_PROFILE, new String[]{COLUMN_PROFILE_ID},
                COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentID)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Get the ProfileID from the query result
            profileID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_ID));
            cursor.close();
        }

        db.close();
        return profileID; // Returns -1 if not found
    }


    public boolean isStudentIDExists(int studentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;

        String query = "SELECT 1 FROM " + TABLE_PROFILE + " WHERE " + COLUMN_STUDENT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentID)});

        if (cursor.moveToFirst()) {
            exists = true; // Student ID exists in the database
        }

        cursor.close();
        db.close();

        return exists;
    }


    public List<Profile> getAllProfilesSortedBySurname() {
        List<Profile> profileList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // SQL query to get all profiles sorted by surname
            String query = "SELECT * FROM " + TABLE_PROFILE + " ORDER BY " + COLUMN_SURNAME + " ASC";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Create Profile object for each row
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String surname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME));
                    double gpa = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GPA));
                    String creationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE));
                    int studentID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID));

                    Profile profile = new Profile(id, name, surname, gpa, creationDate, studentID);
                    profileList.add(profile);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            // Handle exceptions (e.g., log the error)
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // Close cursor if it's not null
            }
            db.close(); // Always close database
        }

        return profileList;
    }


    public List<Profile> getAllProfilesSortedByID() {
        List<Profile> profileList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // SQL query to get all profiles sorted by ID
            String query = "SELECT * FROM " + TABLE_PROFILE + " ORDER BY " + COLUMN_STUDENT_ID + " ASC";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Create Profile object for each row
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String surname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME));
                    double gpa = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GPA));
                    String creationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE));
                    int studentID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID));

                    Profile profile = new Profile(id, name, surname, gpa, creationDate, studentID);
                    profileList.add(profile);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            // Handle exceptions (e.g., log the error)
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // Close cursor if it's not null
            }
            db.close(); // Always close database
        }

        return profileList;
    }


    // Get all access records for a specific profile as a list of Access objects
    public List<Access> getAccessByProfileID(int profileID) {
        List<Access> accesses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ACCESS + " WHERE " + COLUMN_PROFILE_ID_FK + " = " + profileID;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Access access = new Access(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCESS_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_ID_FK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCESS_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                );
                accesses.add(access);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return accesses;
    }

    public Profile getProfileByID(int profileID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Profile profile = null;

        Cursor cursor = db.query(TABLE_PROFILE, null, COLUMN_PROFILE_ID + "=?",
                new String[]{String.valueOf(profileID)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            profile = new Profile(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GPA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID))
            );
            cursor.close();
        }

        db.close();
        return profile;
    }

}
