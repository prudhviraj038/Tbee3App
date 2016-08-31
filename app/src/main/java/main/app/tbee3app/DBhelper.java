package main.app.tbee3app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBhelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cavarat_mall_db";
    private static final String TABLE_WISH = "wishList";
    private static final String TABLE_WISH_LIST = "wishList_list";
    private static final String TABLE_KART = "kart_list";
    private static final String KEY_ID = "id";
    private static final String WISH_ID = "wish_id";
    private static final String PRO_ID = "pro_id";
    private static final String PRO_PR = "pro_price";
    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WISH_TABLE = "CREATE TABLE  IF NOT EXISTS " + TABLE_WISH + "("
                + KEY_ID + " INTEGER,"+WISH_ID+" TEXT)";
        db.execSQL(CREATE_WISH_TABLE);

        String CREATE_WISH_TABLE_list = "CREATE TABLE  IF NOT EXISTS " + TABLE_WISH_LIST + "("
                + WISH_ID + " TEXT)";

        db.execSQL(CREATE_WISH_TABLE_list);

        String CREATE_KART_TABLE = "CREATE TABLE  IF NOT EXISTS " + TABLE_KART + "("
                + PRO_ID + " TEXT," + PRO_PR + " TEXT) ";
        db.execSQL(CREATE_KART_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISH_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KART);
        // Create tables again
        onCreate(db);
    }


    // Adding new contact
    public void add_wish_item(Integer add_id,String wish_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, add_id);
        values.put(WISH_ID, wish_id);
        db.insert(TABLE_WISH, null, values);
        db.close();
    }

    // Getting All Contacts
    // Getting contacts Count
    public int get_wish_item_count(Integer add_id) {
        String countQuery = "SELECT  * FROM " + TABLE_WISH +" WHERE "+ KEY_ID +" = " + String.valueOf(add_id) ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // return count
        return cursor.getCount();

    }


    // Deleting single contact
    public boolean delete_wish_item(Integer add_id,String wish_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_WISH, KEY_ID + " = " + String.valueOf(add_id),null) > 0;

    }

    public void add_wish_list(String wish_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WISH_ID,wish_id);
        db.insert(TABLE_WISH_LIST, null, values);
        db.close();
    }

    // Getting All Contacts
    // Getting contacts Count
    public ArrayList<String> get_wish_list_count() {
        ArrayList mWishnames= new ArrayList();
        String countQuery = "SELECT  * FROM " + TABLE_WISH_LIST ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Log.e("cursor--->",cursor.getString(0));
                mWishnames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        // return count
        return mWishnames;

    }

    public ArrayList<String> get_wish_items_by_wish_list(String wishname) {
        ArrayList mWishnames= new ArrayList();
        String countQuery = "SELECT  * FROM " + TABLE_WISH ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Log.e("cursor--->",cursor.getString(0));
                mWishnames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        // return count
        return mWishnames;

    }

    // Deleting single contact
    public boolean delete_wish_list(String wish_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("db", WISH_ID + " = '"+wish_id+"'");
        return  db.delete(TABLE_WISH_LIST, WISH_ID + " = '"+wish_id+"'",null) > 0;

    }

    public boolean add_kart_list_old(String wish_id, String qty) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PRO_ID,wish_id);
        values.put(PRO_PR,qty);
        db.insert(TABLE_KART, null, values);
        db.close();
        return false;
    }

    public boolean add_kart_list(String wish_id,String qty) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_KART + " WHERE TRIM("+PRO_ID+") = '" + wish_id.trim() + "'", null);
        if(c.getCount()==0) {
            ContentValues values = new ContentValues();
            values.put(PRO_ID, wish_id);
            values.put(PRO_PR, qty);
            db.insert(TABLE_KART, null, values);
            db.close();
            return true;
        }
        else{
            int id_qty=0;
            if (c.moveToFirst()) {
                do {
                    Log.e("cursor--->",c.getString(0));
                    id_qty = Integer.parseInt(c.getString(1));
                } while (c.moveToNext());
            }

            id_qty=id_qty+Integer.parseInt(qty);
            ContentValues values = new ContentValues();
            values.put(PRO_PR,String.valueOf(id_qty));
            db.update(TABLE_KART,
                    values,
                    PRO_ID + " = ?",
                    new String[]{wish_id});
            db.close();
            return false;
        }
    }

    // Getting All Contacts
    // Getting contacts Count
    public ArrayList<String> get_kart_list_count() {
        ArrayList mWishnames= new ArrayList();
        String countQuery = "SELECT  * FROM " + TABLE_KART ; ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Log.e("cursor--->",cursor.getString(0));
                mWishnames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        // return count
        return mWishnames;

    }
    // Deleting single contact
    public boolean delete_kart_list(String wish_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("db", PRO_ID + " = '" + wish_id + "'");
        return  db.delete(TABLE_KART, PRO_ID + " = '"+wish_id+"'",null) > 0;

    }


    public ArrayList<String> get_kart_list_qty() {
        ArrayList mWishnames= new ArrayList();
        String countQuery = "SELECT  * FROM " + TABLE_KART ; ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Log.e("cursor--->",cursor.getString(0));
                mWishnames.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        // return count
        return mWishnames;
    }
}
