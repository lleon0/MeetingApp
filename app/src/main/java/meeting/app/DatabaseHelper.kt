package meeting.app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val DATABASE_NAME = "MeetingsData"
val TABLE_NAME = "Meetings"
val COL_ID = "id"
val COL_DATE = "date"
val COL_SUMMARY = "summary"

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_DATE + " TEXT,"
                + COL_SUMMARY + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME)
        onCreate(db)
    }

    fun insertData(date: String, summary: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_DATE, date)
        contentValues.put(COL_SUMMARY, summary)
        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result
    }

    fun getAllData(): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("select * from $TABLE_NAME", null)
    }

    fun deleteData(id: String): Int? {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "ID = ?", arrayOf(id))
        db.close()
    }
}