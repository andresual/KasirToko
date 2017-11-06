package dev.andresual.com.kasirtoko.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by andresual on 4/8/2017.
 */

public final class KasirContract {

    private KasirContract(){

    }

    public static final String CONTENT_AUTHORITY = "dev.andresual.com.kasirtoko";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BARANG = "barang";
    public static final String PATH_TRANSAKSI = "transaksi"; //anyar

    public static final class KasirEntry implements BaseColumns{

        public static final Uri CONTENT_URI_BARANG = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BARANG);
        public static final Uri CONTENT_URI_TRANSAKSI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRANSAKSI);

        //MIME type pada #CONTENT_URI untuk daftar barang.
        public static final String CONTENT_LIST_TYPE_BARANG =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BARANG;

        //MIME type pada #CONTENT_URI untuk satu baris barang.
        public static final String CONTENT_ITEM_TYPE_BARANG =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BARANG;

        //MIME type pada #CONTENT_URI untuk tabel transaksi
        public static final String CONTENT_LIST_TYPE_TRANSAKSI =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSAKSI;

        //mime pada #CONTENT_URI untuk tabel transaksi
        public static final String CONTENT_ITEM_TYPE_TRANSAKSI =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSAKSI;

        public final static String TABLE_NAME = "barang";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_KATEGORI = "kategori";
        public final static String COLUMN_NAMA = "nama";
        public final static String COLUMN_HARGA = "harga";

        public final static String TABLE_NAME_TRANSAKSI = "transaksi";

        public final static String _ID_TRANSAKSI = BaseColumns._ID;
        public final static String COLUMN_WAKTU_TRANSAKSI = "waktu";
        public final static String COLUMN_QTY_TRANSAKSI = "Quantity";
        public final static String COLUMN_TOTAL_TRANSAKSI = "total";
    }
}
