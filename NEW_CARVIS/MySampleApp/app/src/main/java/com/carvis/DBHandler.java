//package com.carvis;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.database.sqlite.SQLiteOpenHelper;
///**
// * Created by Seamus on 22/02/2017.
// */
//
//public class DBHandler extends SQLiteOpenHelper {
//    // Database Version
//    private static final int DATABASE_VERSION = 1;
//    // Database Name
//    private static final String DATABASE_NAME = "SpeedCamera";
//    // Contacts table name
//    private static final String TABLE_SPEEDCAMERA = "SpeedCamera";
//    // Shops Table Columns names
//    private static final String KEY_ID = "id";
//    private static final String KEY_START_LATITUDE = "startLatitude";
//    private static final String KEY_START_LONGITUDE = "startLongitude";
//    private static final String KEY_END_LATITUDE = "endLatitude";
//    private static final String KEY_END_LONGITUDE = "endLongitude";
//    public DBHandler(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPEEDCAMERA);
//
//        String CREATE_SPEEDCAMERA_TABLE = "CREATE TABLE " + TABLE_SPEEDCAMERA + "("
//        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_START_LATITUDE  + " REAL,"
//        + KEY_START_LONGITUDE + " REAL," +  KEY_END_LATITUDE + " REAL,"+ KEY_END_LONGITUDE+" REALadsasd" +");";
//
//        try {
//            db.execSQL(CREATE_SPEEDCAMERA_TABLE);
//            System.out.println("db created");
//        }
//
//        catch(SQLiteException e){
//            System.out.println(e.getMessage()+"  ////");
//        }
//    }
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//// Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPEEDCAMERA);
//// Creating tables again
//        onCreate(db);
//    }
//
//    public void addSpeedCamera(SpeedCamera camera) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_START_LATITUDE, camera.getStartLatitude());
//        values.put(KEY_START_LONGITUDE, camera.getStartLongitude());
//        values.put(KEY_END_LATITUDE, camera.getEndLatitude());
//        values.put(KEY_END_LONGITUDE, camera.getEndLongitude());
//
//// Inserting Row
//        db.insert(TABLE_SPEEDCAMERA, null,values);
//        db.close(); // Closing database connection
//    }
//
//    public SpeedCamera getSpeedCamera(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_SPEEDCAMERA, new String[] { KEY_ID,
//                        KEY_START_LATITUDE, KEY_START_LONGITUDE, KEY_END_LATITUDE, KEY_END_LONGITUDE }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//        SpeedCamera camera = new SpeedCamera(Double.parseDouble(cursor.getString(0)),
//                Double.parseDouble(cursor.getString(0)), Double.parseDouble(cursor.getString(0)),
//                Double.parseDouble(cursor.getString(0)));
//// return shop
//        return camera;
//    }
//}
//
///*
//testing sqllite local db
// */
////        db = new DBHandler(context);
////        db.addSpeedCamera(new SpeedCamera(52.2651,-9.7112,52.2791,-9.7024));
////        db.addSpeedCamera(new SpeedCamera(52.28,-9.7675,52.2746,-9.8096));
////        db.addSpeedCamera(new SpeedCamera(52.1407,-10.1748,52.1296,-10.2493));
////        db.addSpeedCamera(new SpeedCamera(52.0724,-9.5753,52.1005,-9.6231));
////        db.addSpeedCamera(new SpeedCamera(52.1021,-9.6276,52.1065,-9.6425));
////        db.addSpeedCamera(new SpeedCamera(52.0812,-9.247,52.0542,-9.385));
////        db.addSpeedCamera(new SpeedCamera(52.1134,-9.5169,52.1437,-9.5543));

//
//public void queryDB(View view) {
//final TextView dbquery = (TextView) findViewById(R.id.dbDisplay);
//        SpeedCamera speedCamera = db.getSpeedCamera(1);
//
//        dbquery.setText(String.valueOf(speedCamera.getStartLatitude())+String.valueOf(speedCamera.getStartLongitude()));
//
//        }