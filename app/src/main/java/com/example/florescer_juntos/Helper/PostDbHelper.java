package com.example.florescer_juntos.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class PostDbHelper extends SQLiteOpenHelper {
    public PostDbHelper(@Nullable Context context) {
        super(context, "Florescer_Juntos", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE post (id TEXT PRIMARY KEY, descricao TEXT, userId TEXT, image TEXT, type TEXT, typeUser TEXT, datetime TEXT, mailUser TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}