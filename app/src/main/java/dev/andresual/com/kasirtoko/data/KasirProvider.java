package dev.andresual.com.kasirtoko.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import dev.andresual.com.kasirtoko.data.KasirContract.KasirEntry;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.cacheColorHint;
import static android.R.attr.data;
import static android.R.attr.fingerprintAuthDrawable;
import static android.R.attr.id;
import static android.R.attr.value;

/**
 * Created by andresual on 4/25/2017.
 */

public class KasirProvider extends ContentProvider {
    public static final String LOG_TAG = KasirProvider.class.getSimpleName();

    private KasirDbHelper mDbHelper;

    //inisialisasi is untuk URI matcher
    private static final int BARANG = 100;
    private static final int BARANG_ID = 101;
    private static final int TRANSAKSI = 200;
    private static final int TRANSAKSI_ID = 201;

    //deklarasi URI matcher pada setiap tabel
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(KasirContract.CONTENT_AUTHORITY, KasirContract.PATH_BARANG, BARANG);
        sUriMatcher.addURI(KasirContract.CONTENT_AUTHORITY, KasirContract.PATH_BARANG + "/#", BARANG_ID);
        sUriMatcher.addURI(KasirContract.CONTENT_AUTHORITY, KasirContract.PATH_TRANSAKSI, TRANSAKSI);
        sUriMatcher.addURI(KasirContract.CONTENT_AUTHORITY, KasirContract.PATH_TRANSAKSI + "/#", TRANSAKSI_ID);
    }

    @Override
    public boolean onCreate(){
        mDbHelper = new KasirDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder){

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        //query untuk tabel barang
        int match = sUriMatcher.match(uri);
        switch (match){
            case BARANG :
                cursor = database.query(KasirEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            //query untuk id barang
            case BARANG_ID :
                selection = KasirEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(KasirEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            //query untuk tabel transaksi
            case TRANSAKSI :
                cursor = database.query(KasirEntry.TABLE_NAME_TRANSAKSI, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            //query untuk id transaksi
            case TRANSAKSI_ID :
                selection = KasirEntry._ID_TRANSAKSI + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(KasirEntry.TABLE_NAME_TRANSAKSI, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //set notifikasi URI pada cursor
        //sehingga kita tahu untuk apa corsor content URI dibuat
        //jika ada perubahan data pada URI, kita harus mengubah cursor juga
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues){
        final int match = sUriMatcher.match(uri);
        switch (match){
            //insert untuk barang
            case BARANG :
                 return insertBarang(uri, contentValues);

            //insert untuk transaksi
            case TRANSAKSI :
                return insertTransaksi(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBarang(Uri uri, ContentValues values){

        String kategori = values.getAsString(KasirEntry.COLUMN_KATEGORI);
        if (kategori == null){
            throw new IllegalArgumentException("Isi kategori barang");
        }

        String nama = values.getAsString(KasirEntry.COLUMN_NAMA);
        if (nama == null){
            throw new IllegalArgumentException("Isi nama barang");
        }

        Integer harga = values.getAsInteger(KasirEntry.COLUMN_HARGA);
        if (harga != null && harga < 0 ){
            throw new IllegalArgumentException("Harga tidak valid");
        }
        //Get Writable Database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Insert barang baru dengan values
        long id = database.insert(KasirEntry.TABLE_NAME, null, values);

        if (id == -1){
            Log.e(LOG_TAG, "Gagal menyimpan barang untuk " + uri);
            return null;
        }

        //menotifikasi semua listener jika data telah berubah untuk content URI barang
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertTransaksi(Uri uri, ContentValues values){

        String tanggalTransaksi = values.getAsString(KasirEntry.COLUMN_WAKTU_TRANSAKSI);
        if (tanggalTransaksi == null){
            throw new IllegalArgumentException("Isi transaksi");
        }

        Integer totalTransaksi = values.getAsInteger(KasirEntry.COLUMN_TOTAL_TRANSAKSI);
        if (totalTransaksi == null){
            throw new IllegalArgumentException("Isi transaksi");
        }

        //Get Writable Database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Insert transaksi baru dengan values
        long id = database.insert(KasirEntry.TABLE_NAME_TRANSAKSI, null, values);

        if (id == -1){
            Log.e(LOG_TAG, "Gagal menyimpan transaksi untuk " + uri);
            return null;
        }

        //menotifikasi semua listener jika data telah berubah untuk content URI barang
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    //method untuk eksekusi update barang
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BARANG:
                return updateBarang(uri, contentValues, selection, selectionArgs);
            case BARANG_ID:
                //untuk kode BARANG_ID, ekstrak ID dari URI.
                //sehingga kita bisa tahu kolom nama yang diupdate. Selection akan menjadi "=?"
                //dan selectionArgs akan menjadi string array yang memuat ID aktual.
                selection = KasirEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateBarang(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update tidak tersedia untuk " + uri);
        }
    }
    /**
     * Update barang pada database dengan content value yang telah diberikan. Membuat perubahan pada
     * selection dan selectionArgs yang telah ditentukan (yang dimana bisa 0 / 1 atau lebih barang)
     * kembalikan jumlah kolom yang sudah diupdate*/

    private int updateBarang(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        //jika {@link KasirEntry#COLUMN_KATEGORI} key tersedia,
        //cek dan pastikan kategori value tidak null
        if (values.containsKey(KasirEntry.COLUMN_KATEGORI)){
            String kategori = values.getAsString(KasirEntry.COLUMN_KATEGORI);
            if (kategori == null){
                throw new IllegalArgumentException("Isi kategori barang");
            }
        }

        //jika {@link KasirEntry#COLUMN_NAMA} key tersedia,
        //cek dan pastikan nama value tidak null
        if (values.containsKey(KasirEntry.COLUMN_NAMA)){
            String nama = values.getAsString(KasirEntry.COLUMN_NAMA);
            if (nama == null){
                throw new IllegalArgumentException("Isi nama barang");
            }
        }

        //jika {@link KasirEntry#COLUMN_HARGA} key tersedia,
        //cek dan pastikan harga value tidak null
        if (values.containsKey(KasirEntry.COLUMN_HARGA)){
            Integer harga = values.getAsInteger(KasirEntry.COLUMN_HARGA);
            if (harga == null && harga < 0){
                throw new IllegalArgumentException("Harga barang tidak valid");
            }
        }

        //jika tidak ada values untuk dipudate, jangan lakukan update pada database
        if (values.size() == 0){
            return 0;
        }

        //menjadikan database writable
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //mengembalikan jumlah baris database yang telah diupdate
        //return database.update(KasirEntry.TABLE_NAME, values, selection, selectionArgs);
        //mengeksekusi update pada database dan mengubah row yang bersangkutan
        int rowsUpdated = database.update(KasirEntry.TABLE_NAME, values, selection, selectionArgs);
        //jika ada 1 atau lebih diupdate, notifikasikan pada semua listener bahwa data yang
        //diberikan pada URI telah dirubah
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //melacak id yang dihapus
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case BARANG:
                //return database.delete(KasirEntry.TABLE_NAME, selection, selectionArgs);
                //menghapus semua row yang cocok dengan selection dan selectionArgs
                rowsDeleted = database.delete(KasirEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case BARANG_ID:
                selection = KasirEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                //return database.delete(KasirEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted = database.delete(KasirEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete gagal untuk " + uri);
        }

        //jika 1 atau lebih row terhapus, notifikasi semua listener pada data yang diberikan pada
        //uri telah berubah
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    //mengembalikan MIME tipe data untuk content URI
    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BARANG:
                return KasirEntry.CONTENT_LIST_TYPE_BARANG;

            case BARANG_ID:
                return KasirEntry.CONTENT_ITEM_TYPE_BARANG;

            case TRANSAKSI:
                return KasirEntry.CONTENT_LIST_TYPE_TRANSAKSI;

            case TRANSAKSI_ID:
                return KasirEntry.CONTENT_ITEM_TYPE_TRANSAKSI;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
}
