package dev.andresual.com.kasirtoko.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dev.andresual.com.kasirtoko.data.KasirContract.KasirEntry;
/**
 * Created by andresual on 4/8/2017.
 */

public class KasirDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = KasirDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "kasirtoko.db";
    private static final int DATABASE_VERSION = 1;

    public KasirDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String SQL_CREATE_BARANG_TABLE = "CREATE TABLE " + KasirEntry.TABLE_NAME + " ("
                + KasirEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KasirEntry.COLUMN_KATEGORI + " TEXT NOT NULL, "
                + KasirEntry.COLUMN_NAMA + " TEXT NOT NULL, "
                + KasirEntry.COLUMN_HARGA + " INTEGER NOT NULL DEFAULT 0);";

        String SQL_CREATE_TRANSAKSI_TABLE = "CREATE TABLE " + KasirEntry.TABLE_NAME_TRANSAKSI + "("
                + KasirEntry._ID_TRANSAKSI + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KasirEntry.COLUMN_WAKTU_TRANSAKSI + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + KasirEntry.COLUMN_QTY_TRANSAKSI + " INTEGER NOT NULL, "
                + KasirEntry.COLUMN_TOTAL_TRANSAKSI + " INTEGER NOT NULL DEFAULT 0);";

        //eksekusi SQL statement
        db.execSQL(SQL_CREATE_BARANG_TABLE);
        db.execSQL(SQL_CREATE_TRANSAKSI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXIST" + KasirEntry.TABLE_NAME);
        onCreate(db);
    }
}
