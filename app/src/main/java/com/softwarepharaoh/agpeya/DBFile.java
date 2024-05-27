package com.softwarepharaoh.agpeya;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DBFile {
    private static final String DB_NAME = "agpeya";
    private final Context myContext;

    public DBFile(Context context) {
        this.myContext = context;
    }

    public void copyDB() {
        try {
            InputStream myInput = this.myContext.getAssets().open(DB_NAME, 0);
            FileOutputStream myOutput = this.myContext.openFileOutput(DB_NAME, 0);
            byte[] buffer = new byte[1024];
            while (true) {
                int length = myInput.read(buffer);
                if (length <= 0) {
                    myOutput.flush();
                    myOutput.getFD().sync();
                    myOutput.close();
                    myInput.close();
                    return;
                }
                myOutput.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new Error("Error Copying Database");
        }
    }

    public SQLiteDatabase loadDb(Context context) throws SQLiteException, IOException {
        if (!checkDB()) {
            copyDB();
        }
        return SQLiteDatabase.openDatabase(context.getFileStreamPath(DB_NAME).getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
    }

    public boolean checkDB() throws IOException {
        File f = this.myContext.getFileStreamPath(DB_NAME);
        boolean exists = f.exists();
        boolean canRead = f.canRead();
        if (!exists || !canRead) {
            return false;
        } else return ((long) this.myContext.getAssets().open(DB_NAME, 0).available()) - f.length() < 500;
    }
}
