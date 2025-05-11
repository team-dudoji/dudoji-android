package com.dudoji.android.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "dudoji.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        val createTableMapSection =
            "CREATE TABLE MapSection (" +
                "x INT NOT NULL," +
                "y INT NOT NULL," +
                "explored BOOLEAN NOT NULL DEFAULT FALSE," +
                "UNIQUE (x, y)," +
                "PRIMARY KEY (x, y)" +
            ");"

        val createTableMapSectionStateBitmap =
            "CREATE TABLE MapSectionStateBitmap (" +
                "x INT NOT NULL," +
                "y INT NOT NULL," +
                "bitmap BLOB NOT NULL," +
                "primary key (x, y)," +
                "foreign key (x, y) references MapSection(x, y)" +
            ");"

        p0?.execSQL(createTableMapSection)
        p0?.execSQL(createTableMapSectionStateBitmap)
    }

    override fun onUpgrade(
        p0: SQLiteDatabase?,
        p1: Int,
        p2: Int
    ) {

    }
}