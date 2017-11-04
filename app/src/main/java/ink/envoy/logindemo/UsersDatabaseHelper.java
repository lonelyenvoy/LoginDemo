package ink.envoy.logindemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class UsersDatabaseHelper extends SQLiteOpenHelper {
    UsersDatabaseHelper(Context context) {
        super(context, "users.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE users(_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, createdAt INTEGER, updatedAt INTEGER)"
        );
        long time = System.currentTimeMillis();
        sqLiteDatabase.execSQL(
                "INSERT INTO users(username, password, createdAt, updatedAt) values ('admin', 'admin', ?, ?)", new Object[] {time, time});
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
