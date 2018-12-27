package com.salim.salimzhulkhrni.student_marketplace

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class LocalEmailPwdDB(context: Context): SQLiteOpenHelper(context,DB_NAME,null,DB_VER){

    val TAG = "EmailPWDdb_fragment"

    companion object {

        private val DB_NAME = "emailpwd.db"
        private val DB_VER = 1
        private val COL_ID="id"
        private val COL_EMAIL = "email"
        private val COL_PASSWORD= "password"

        // create a email pwd table

        private val CREATE_TABLE_EMAILPWD = "CREATE TABLE IF NOT EXISTS emailpwd " +
                "( $COL_ID INTEGER PRIMARY KEY, $COL_EMAIL TEXT, $COL_PASSWORD TEXT )"

    }

    override fun onCreate(db: SQLiteDatabase?) {

        Log.d(TAG,"creating email pwd db")
        db!!.execSQL(CREATE_TABLE_EMAILPWD)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        Log.d(TAG,"upgrading email pwd db")
        db!!.execSQL(CREATE_TABLE_EMAILPWD)
    }

    fun closeDB(){

        // close DB connection
        val db=this.readableDatabase
        if(db != null && db.isOpen)
            db.close()

    }

    fun saveUserCredentialsIntoDB(email: String, password: String): Long{

        Log.d(TAG,"inside user credentials details in db func")
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_EMAIL,email)
        values.put(COL_PASSWORD,password)

        val row_id = db.insert("emailpwd",null,values)
        Log.d(TAG,"EmailPwd Table Insertion: "+ row_id.toString())
        return row_id

    }

    fun getFirstUserCredentials(): MutableList<String>{

        Log.d(TAG,"inside first user credentails func")
        val email_password_db_list = mutableListOf<String>()

        val query = "SELECT * FROM emailpwd WHERE $COL_ID = 1"

        val db=this.readableDatabase
        val cursor= db.rawQuery(query,null)

        if(cursor.moveToFirst()){

            Log.d(TAG,"db data available")
            val email_from_db = cursor.getString(cursor.getColumnIndex(COL_EMAIL))
            val password_from_db = cursor.getString(cursor.getColumnIndex(COL_PASSWORD))
            email_password_db_list.add(0,email_from_db)
            email_password_db_list.add(1,password_from_db)
            return email_password_db_list
        }
        else{

            Log.d(TAG,"no db data available")
            return email_password_db_list
        }

    }

    fun updateUserCredentials(email: String,password: String): Int {

        Log.d(TAG,"inside update user credentials func")

        val db= this.writableDatabase
        val values=ContentValues()

        values.put(COL_EMAIL,email)
        values.put(COL_PASSWORD,password)

        val row_id = db.update("emailpwd",values,"$COL_EMAIL = ?", arrayOf(email))
        Log.d(TAG,"pwd update for "+email+" success")
        return  row_id

    }


}