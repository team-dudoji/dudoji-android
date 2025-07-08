package com.dudoji.android.database.dao

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.dudoji.android.database.DataBaseHelper
import com.dudoji.android.map.domain.mapsection.MapSection
import com.dudoji.android.map.utils.mapsection.BitmapUtil

class MapSectionDao(val context: Context) {
    val GET_ALL_MAP_SECTIONS =
        "select m.x, m.y, mb.bitmap " +
            "from MapSection as m " +
            "LEFT JOIN MapSectionStateBitmap as mb " +
            "on (m.x = mb.x and m.y = mb.y);"

    val DELETE_ALL_MAP_SECTIONS =
        "delete from MapSection;"
    val DELETE_ALL_MAP_SECTION_STATE_BITMAP =
        "delete from MapSectionStateBitmap;"

    val GET_MAP_SECTIONS =
        "select m.x, m.y, mb.bitmap " +
                "from MapSection as m " +
                "LEFT JOIN MapSectionStateBitmap as mb " +
                "on (m.x = mb.x and m.y = mb.y) " +
                "where (? <= m.x and m.x <= ? and ? <= m.y and m.y <= ?);"
    val GET_MAP_SECTION = """
        select m.x, m.y, mb.bitmap 
        from MapSection as m 
        LEFT JOIN MapSectionStateBitmap as mb 
        on (m.x = mb.x and m.y = mb.y) 
        where (m.x = ? and m.y = ?);
    """
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

    fun deleteAllMapSections() {
        val db = dataBaseHelper.writableDatabase
        db.execSQL(DELETE_ALL_MAP_SECTIONS)
        db.execSQL(DELETE_ALL_MAP_SECTION_STATE_BITMAP)
    }

    fun getMapSection(x: Int, y: Int): MapSection? {
        val db = dataBaseHelper.readableDatabase
        val cursor = db.rawQuery(GET_MAP_SECTION, arrayOf(x.toString(), y.toString()))
        if (cursor.moveToFirst()) {
            val bitmapBlob = cursor.getBlob(2)
            val bitmap = BitmapFactory.decodeByteArray(bitmapBlob, 0, bitmapBlob.size)
            return MapSection.Builder()
                .setXY(cursor.getInt(0), cursor.getInt(1))
                .setBitmap(bitmap)
                .build()
        }
        return null
    }

    @SuppressLint("Recycle")
    fun getMapSections(): List<MapSection> {
        val db = dataBaseHelper.readableDatabase
        val cursor = db.rawQuery(GET_ALL_MAP_SECTIONS, null)
        val mapSections = mutableListOf<MapSection>()
        Log.d("MapSectionDao", "getMapSections: ${cursor.count}")
        if (cursor.moveToFirst()) {
            do {
                Log.d("MapSectionDao", "x: ${cursor.getInt(0)}, y: ${cursor.getInt(1)}")
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
            Log.d("MapSectionDao", "insertMapSection: ${mapSection.x}, ${mapSection.y}")
            insertMapSection(mapSection)
        } else {
            Log.d("MapSectionDao", "updateMapSection: ${mapSection.x}, ${mapSection.y}")
            updateMapSection(mapSection)
        }
    }

    fun updateMapSection(mapSection: MapSection){
        if (mapSection.bitmap != null) {
            val db = dataBaseHelper.writableDatabase
            val statement = db.compileStatement(UPATE_MAP_SECTION_STATE_BITMAP)
            statement.bindBlob(1,
                BitmapUtil.bitmapToByteArray(mapSection.bitmap!!)
            )
            statement.bindLong(2, mapSection.x.toLong())
            statement.bindLong(3, mapSection.y.toLong())
            statement.execute()
        } else {
            val db = dataBaseHelper.writableDatabase
            val statement1 = db.compileStatement(DELETE_MAP_SECTION_STATE_BITMAP)
            statement1.bindLong(1, mapSection.x.toLong())
            statement1.bindLong(2, mapSection.y.toLong())
            statement1.execute()

            val statement2 = db.compileStatement(UPATE_MAP_SECTION_EXPLOED)
            statement2.bindLong(1, 1L)
            statement2.bindLong(2, mapSection.x.toLong())
            statement2.bindLong(3, mapSection.y.toLong())
            statement2.execute()
        }
    }

    fun insertMapSection(mapSection: MapSection) {
        val db = dataBaseHelper.writableDatabase
        if (mapSection.bitmap != null) {
            val statement1 = db.compileStatement(INSERT_MAP_SECTION)
            statement1.bindLong(1, mapSection.x.toLong())
            statement1.bindLong(2, mapSection.y.toLong())
            statement1.bindLong(3, 0L)
            statement1.executeInsert()


            val statement2 = db.compileStatement(INSERT_MAP_SECTION_STATE_BITMAP)
            statement2.bindLong(1, mapSection.x.toLong())
            statement2.bindLong(2, mapSection.y.toLong())
            val bitmap = mapSection.bitmap!!
            val byteArray: ByteArray = BitmapUtil.bitmapToByteArray(bitmap)
            statement2.bindBlob(3, byteArray)
            try {
                statement2.executeInsert()
            }
            catch (e: Exception) {
                Log.e("MapSectionDao", "insertMapSection: Error inserting map section state bitmap", e)
            }

        } else {
            val statement = db.compileStatement(INSERT_MAP_SECTION)
            statement.bindLong(1, mapSection.x.toLong())
            statement.bindLong(2, mapSection.y.toLong())
            statement.bindLong(3, 1L)
            statement.executeInsert()
        }
    }
}