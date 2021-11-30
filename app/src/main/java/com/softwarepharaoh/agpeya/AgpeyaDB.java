package com.softwarepharaoh.agpeya;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.IOException;

public class AgpeyaDB {
    private static final String DATABASE_NAME = "agpeya";
    private static final int DATABASE_VERSION = 1;
    private final Context AgpeyaContext;
    private SQLiteDatabase AgpeyaDataBase;
    private DBHelper AgpeyaHelper;
    private Cursor cursor;
    public DBFile db;

    public class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, AgpeyaDB.DATABASE_NAME, null, 1);
            AgpeyaDB.this.db = new DBFile(context);
        }

        public void onCreate(SQLiteDatabase db) {
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public AgpeyaDB(Context c) {
        this.AgpeyaContext = c;
    }

    public AgpeyaDB open() throws SQLException {
        this.AgpeyaHelper = new DBHelper(this.AgpeyaContext);
        try {
            this.AgpeyaDataBase = this.db.loadDb(this.AgpeyaContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Cursor getPrayer(String selectIn) {
        String Query = "SELECT p.* FROM prayers p INNER JOIN prayers_order o ON p.prayer_key = o.prayer_key WHERE o.prayer = ? ORDER BY o.prayer_order";
        try {
            this.cursor = this.AgpeyaDataBase.rawQuery(Query, new String[]{String.valueOf(selectIn)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.cursor;
    }

    public Cursor getAllRemiders() {
        try {
            this.cursor = this.AgpeyaDataBase.query("prayers_reminder", new String[]{"prayer", "prayer_time", "is_active"}, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.cursor;
    }

    public void close() {
        this.AgpeyaDataBase.close();
        this.AgpeyaHelper.close();
    }
}

