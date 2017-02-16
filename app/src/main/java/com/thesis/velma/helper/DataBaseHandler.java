package com.thesis.velma.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thesis.velma.helper.DBInfo.DataInfo;

import static com.thesis.velma.LandingActivity.db;

/**
 * Created by andrewlaurienrsocia on 03/01/2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    public static final int database_version = 1;

    public String CREATE_EVENTS = "CREATE TABLE " + DataInfo.TBl_Events + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "" + DataInfo.UserID + " TEXT, " +
            "" + DataInfo.EventID + " TEXT, " +
            DataInfo.EventName + " TEXT," + DataInfo.EventDescription + " TEXT," + DataInfo.EventLocation + " TEXT," +
            DataInfo.EventStartDate + " TEXT," + DataInfo.EventStartTime + " TEXT," + DataInfo.EventEndDate + " TEXT," +
            DataInfo.EventEndTime + " TEXT," + DataInfo.Notify + " TEXT," + DataInfo.Extra1 + " TEXT," +
            DataInfo.Extra2 + " TEXT," + DataInfo.Extra3 + " TEXT," + DataInfo.Extra4 + " TEXT)";
    public String CREATE_CONTACTS = "CREATE TABLE " + DataInfo.TBlContacts + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataInfo.Name + " TEXT," + DataInfo.EmailAdd + " TEXT," + DataInfo.Extra1 + " TEXT," +
            DataInfo.Extra2 + " TEXT," + DataInfo.Extra3 + " TEXT," + DataInfo.Extra4 + " TEXT)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS);
        db.execSQL(CREATE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    //region CONSTRUCTOR
    //***********************
    //CONSTRUCTOR
    //***********************
    public DataBaseHandler(Context context) {
        super(context, DataInfo.DATABASE_NAME, null, database_version);

    }

    //endregion

    //region METHODS

    public void saveEvent(String userid, Long eventid, String eventname, String eventDescription, String eventLocation,
                          String eventStartDate, String eventStartTime, String eventEndDate, String eventEndTime, String notify, String invitedfirends) {

        SQLiteDatabase sql = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DataInfo.UserID, userid);
        cv.put(DataInfo.EventID, eventid);
        cv.put(DataInfo.EventName, eventname);
        cv.put(DataInfo.EventDescription, eventDescription);
        cv.put(DataInfo.EventLocation, eventLocation);
        cv.put(DataInfo.EventStartDate, eventStartDate);
        cv.put(DataInfo.EventStartTime, eventStartTime);
        cv.put(DataInfo.EventEndDate, eventEndDate);
        cv.put(DataInfo.EventEndTime, eventEndTime);
        cv.put(DataInfo.Notify, notify);
        cv.put(DataInfo.Extra1, invitedfirends);

        sql.insert(DataInfo.TBl_Events, null, cv);

    }

    public void saveContact(String name, String email) {

        SQLiteDatabase sql = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(DataInfo.Name, name);
        cv.put(DataInfo.EmailAdd, email);

        sql.insert(DataInfo.TBlContacts, null, cv);

    }

    public Cursor getEvents() {

        SQLiteDatabase sql = db.getReadableDatabase();

        Cursor c = sql.rawQuery("SELECT * FROM " + DataInfo.TBl_Events, null);

        return c;

    }

    public Cursor getContacts() {

        SQLiteDatabase sql = db.getReadableDatabase();

        Cursor c = sql.rawQuery("SELECT * FROM " + DataInfo.TBlContacts, null);

        return c;

    }

    public Cursor getEventDetails(long id) {

        SQLiteDatabase sql = db.getReadableDatabase();

        Cursor c = sql.rawQuery("SELECT * FROM " + DataInfo.TBl_Events + " Where _id=" + id, null);

        return c;

    }

    public void deleteEvent(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(DataInfo.TBl_Events, "_id =" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

    }

    //endregion


}
