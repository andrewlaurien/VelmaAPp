package com.thesis.velma.helper;

import android.provider.BaseColumns;

/**
 * Created by andrewlaurienrsocia on 03/01/2017.
 */

public class DBInfo {

    public static class DataInfo implements BaseColumns {


        //DataBase Name
        public static final String DATABASE_NAME = "velma_app";

        //TableNames
        public static final String TBl_Events = "velma_events";
        public static final String TBlContacts = "Contacts";


        //FieldName
        public static final String UserID = "UserID";
        public static final String EventID = "EventID";
        public static final String EventName = "EventName";
        public static final String EventDescription = "EventDescription";
        public static final String EventLocation = "EventLocation";
        public static final String EventStartDate = "StartDate";
        public static final String EventStartTime = "StartTime";
        public static final String EventEndDate = "EndDate";
        public static final String EventEndTime = "EndTime";
        public static final String Notify = "Notify";
        public static final String Extra1 = "Extra1";
        public static final String Extra2 = "Extra2";
        public static final String Extra3 = "Extra3";
        public static final String Extra4 = "Extra4";

        public static final String Name = "Name";
        public static final String EmailAdd = "EmailAdd";

    }
}
