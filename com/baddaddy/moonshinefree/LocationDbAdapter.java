package com.baddaddy.moonshinefree;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocationDbAdapter {
    public static final String ACTION = "action";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_INSERT = "insert";
    public static final String ACTION_UPDATE = "update";
    private static final String DATABASE_CREATE = "create table locations (_id integer primary key autoincrement, name text not null, latitude double not null, longitude double not null, timezone text not null);";
    private static final String DATABASE_NAME = "moonshine";
    private static final String DATABASE_TABLE = "locations";
    private static final int DATABASE_VERSION = 3;
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_NAME = "name";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TIMEZONE = "timezone";
    private static final String TAG = "LocationDbAdapter";
    private final Context mCtx;
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, LocationDbAdapter.DATABASE_NAME, null, LocationDbAdapter.DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(LocationDbAdapter.DATABASE_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(LocationDbAdapter.TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS locations");
            onCreate(db);
        }
    }

    public LocationDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public LocationDbAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.mDbHelper.close();
    }

    public long createLocation(String name, double latitude, double longitude, String timezone) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_LATITUDE, Double.valueOf(latitude));
        initialValues.put(KEY_LONGITUDE, Double.valueOf(longitude));
        initialValues.put(KEY_TIMEZONE, timezone);
        return this.mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteLocation(long rowId) {
        return this.mDb.delete(DATABASE_TABLE, new StringBuilder("_id=").append(rowId).toString(), null) > 0;
    }

    public Cursor fetchAllLocations() {
        return this.mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NAME, KEY_LATITUDE, KEY_LONGITUDE, KEY_TIMEZONE}, null, null, null, null, null);
    }

    public long[] fetchAllIds(boolean addEmpty) {
        int first;
        long[] results = null;
        if (addEmpty) {
            first = 1;
        } else {
            first = 0;
        }
        Cursor cursor = this.mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            results = new long[(cursor.getCount() + first)];
            if (addEmpty) {
                results[0] = 0;
            }
            cursor.moveToFirst();
            do {
                results[cursor.getPosition() + first] = cursor.getLong(0);
            } while (cursor.moveToNext());
            return results;
        } else if (!addEmpty) {
            return results;
        } else {
            return new long[]{0};
        }
    }

    public Cursor fetchLocation(long rowId) throws SQLException {
        Cursor mCursor = this.mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NAME, KEY_LATITUDE, KEY_LONGITUDE, KEY_TIMEZONE}, "_id=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateLocation(long rowId, String name, double latitude, double longitude, String timezone) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_LATITUDE, Double.valueOf(latitude));
        args.put(KEY_LONGITUDE, Double.valueOf(longitude));
        args.put(KEY_TIMEZONE, timezone);
        return this.mDb.update(DATABASE_TABLE, args, new StringBuilder("_id=").append(rowId).toString(), null) > 0;
    }
}
