package com.dudoji.android.database.dao

import android.R.attr.x
import android.R.attr.y
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import com.dudoji.android.database.DataBaseHelper
import com.dudoji.android.map.domain.mapsection.DetailedMapSection
import com.dudoji.android.map.domain.mapsection.MapSection
import com.dudoji.android.map.utils.mapsection.BitmapUtil

class MapSectionDao(val context: Context) {
    val GET_ALL_MAP_SECTIONS =
        "select m.x, m.y, mb.bitmap " +
            "from MapSection as m " +
            "LEFT JOIN MapSectionStateBitmap as mb " +
            "on (m.x = mb.x and m.y = mb.y);"
    val GET_MAP_SECTIONS =
        "select m.x, m.y, mb.bitmap " +
                "from MapSection as m " +
                "LEFT JOIN MapSectionStateBitmap as mb " +
                "on (m.x = mb.x and m.y = mb.y) " +
                "where (? <= m.x and m.x <= ? and ? <= m.y and m.y <= ?);"
    val INSERT_MAP_SECTION =
        "insert into MapSection (x, y, explored) values (?, ?, ?);"
    val INSERT_MAP_SECTION_STATE_BITMAP =
        "insert into MapSectionStateBitmap (x, y, bitmap) values (?, ?, ?);"
    val DELETE_MAP_SECTION_STATE_BITMAP =
        "delete from MapSectionStateBitmap where x = ? and y = ?;"
    val UPATE_MAP_SECTION_EXPLOED =
        "update MapSection set explored = ? where x = ? and y = ?;"

    val UPATE_MAP_SECTION_STATE_BITMAP =
        "update MapSectionStateBitmap set bitmap = ? where x = ? and y = ?;"

    val dataBaseHelper = DataBaseHelper(context)

    @SuppressLint("Recycle")
    fun getMapSections(): List<MapSection> {
        val db = dataBaseHelper.readableDatabase
        val cursor = db.rawQuery(GET_ALL_MAP_SECTIONS, null)
        val mapSections = mutableListOf<MapSection>()
        if (cursor.moveToFirst()) {
            do {
                val x = cursor.getInt(0)
                val y = cursor.getInt(1)
                val bitmapBlob = cursor.getBlob(2)
                val bitmap = BitmapFactory.decodeByteArray(bitmapBlob, 0, bitmapBlob.size)
                val mapSection = MapSection.Builder()
                    .setXY(x, y)
                    .setBitmap(bitmap)
                    .build()
                mapSections.add(mapSection)
            } while (cursor.moveToNext())
        }
        return mapSections
    }

    fun checkMapSection(x: Int, y: Int): Boolean {
        val db = dataBaseHelper.readableDatabase
        val cursor = db.rawQuery(
            "select * from MapSection where x = ? and y = ?",
            arrayOf(x.toString(), y.toString())
        )
        return cursor.count > 0
    }

    fun setMapSection(mapSection: MapSection){
        if (!checkMapSection(mapSection.x, mapSection.y)) {
            insertMapSection(mapSection)
        } else {
            updateMapSection(mapSection)
        }
    }

    fun updateMapSection(mapSection: MapSection){
        if (mapSection is DetailedMapSection) {
            val db = dataBaseHelper.writableDatabase
            val statement = db.compileStatement(UPATE_MAP_SECTION_STATE_BITMAP)
            statement.bindBlob(1,
                BitmapUtil.bitmapToByteArray(mapSection.getBitmap())
            )
            statement.executeUpdateDelete()
        } else {
            val db = dataBaseHelper.writableDatabase
            val statement1 = db.compileStatement(DELETE_MAP_SECTION_STATE_BITMAP)
            statement1.bindLong(1, mapSection.x.toLong())
            statement1.bindLong(2, mapSection.y.toLong())
            statement1.executeUpdateDelete()

            val statement2 = db.compileStatement(UPATE_MAP_SECTION_EXPLOED)
            statement2.bindLong(1, 1L)
            statement2.bindLong(2, mapSection.x.toLong())
            statement2.bindLong(3, mapSection.y.toLong())
            statement2.executeUpdateDelete()
        }

    }

    fun insertMapSection(mapSection: MapSection) {
        val db = dataBaseHelper.writableDatabase
        if (mapSection is DetailedMapSection) {
            val statement1 = db.compileStatement(INSERT_MAP_SECTION)
            statement1.bindLong(1, mapSection.x.toLong())
            statement1.bindLong(2, mapSection.y.toLong())
            statement1.bindLong(2, 0L)
            statement1.executeInsert()

            val statement2 = db.compileStatement(INSERT_MAP_SECTION_STATE_BITMAP)
            statement2.bindLong(1, mapSection.x.toLong())
            statement2.bindLong(2, mapSection.y.toLong())
            val bitmap = mapSection.getBitmap()
            val byteArray: ByteArray = BitmapUtil.bitmapToByteArray(bitmap)
            statement2.bindBlob(3, byteArray)
            statement2.executeInsert()

        } else {
            val statement = db.compileStatement(INSERT_MAP_SECTION)
            statement.bindLong(1, mapSection.x.toLong())
            statement.bindLong(2, mapSection.y.toLong())
            statement.bindLong(2, 1L)
            statement.executeInsert()
        }
        val statement = db.compileStatement(INSERT_MAP_SECTION)
        statement.bindLong(1, x.toLong())
        statement.bindLong(2, y.toLong())
        statement.executeInsert()
    }
}