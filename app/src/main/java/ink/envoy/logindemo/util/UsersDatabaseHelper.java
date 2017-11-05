package ink.envoy.logindemo.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsersDatabaseHelper extends SQLiteOpenHelper {
    public UsersDatabaseHelper(Context context) {
        super(context, "users.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE users(_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, isAdmin INTEGER, createdAt INTEGER, updatedAt INTEGER)"
        );
        long time = System.currentTimeMillis();
        sqLiteDatabase.execSQL(
                "INSERT INTO users(username, password, isAdmin, createdAt, updatedAt) values ('admin', ?, 1, ?, ?)",
                new Object[] { MD5Hasher.hash("admin"), time, time });
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
