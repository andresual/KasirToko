package dev.andresual.com.kasirtoko;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import dev.andresual.com.kasirtoko.data.KasirContract.KasirEntry;
import dev.andresual.com.kasirtoko.data.KasirDbHelper;

public class EditBarang extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mKategoriEditText;
    private EditText mNamaEditText;
    private EditText mHargaEditText;
    private KasirDbHelper mDbHelper;
    private Uri mCurrentBarangUri;
    private static final int EXISTING_BARANG_LOADER = 0;

    //boolean yang menandai dan mempertahankan alur ketika barang telah diedit
    private boolean mBarangHasChanged = false;

    /**
     * OnTouchListener yang merecord semua sentuhan user pada view.
     * mengingatkan bahwa mereka sedang mengedit barang.
     * dan saya mengubah mBarangHasChanged boolean menjadi true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBarangHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_barang);

        Intent intent = getIntent();
        mCurrentBarangUri = intent.getData();

        if (mCurrentBarangUri == null){
            setTitle("Tambah Barang");
        }else {
            setTitle("Edit Barang");
        }

        mKategoriEditText = (EditText) findViewById(R.id.input_kategori);
        mNamaEditText = (EditText) findViewById(R.id.input_nama_barang);
        mHargaEditText = (EditText) findViewById(R.id.input_harga_barang);

        //setup OntouchListener pada semua input fields. sehingga kita dapat menentukan jika user
        //telah menyentuh atau memodifikasinya. ini akan memberitahu kita jika ada unsaved changes
        //atau tidak. jika user mencoba untuk meninggalkan editor tanpa menyimpan

        mKategoriEditText.setOnTouchListener(mTouchListener);
        mNamaEditText.setOnTouchListener(mTouchListener);
        mHargaEditText.setOnTouchListener(mTouchListener);

        getLoaderManager().initLoader(EXISTING_BARANG_LOADER, null, this);
    }

    private void saveBarang(){

        KasirDbHelper mDbHelper = new KasirDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Uri mCurrentBarangUri = ContentUris.withAppendedId(KasirEntry.CONTENT_URI_BARANG, ); //mamang

        //membaca input field
        //gunakan trim untuk leading whitespace
        String kategoriString = mKategoriEditText.getText().toString().trim();
        String namaString = mNamaEditText.getText().toString().trim();
        String hargaString = mHargaEditText.getText().toString().trim();
        //int harga = Integer.parseInt(hargaString);

        if (mCurrentBarangUri == null &&
                TextUtils.isEmpty(kategoriString) && TextUtils.isEmpty(namaString) &&
                TextUtils.isEmpty(hargaString)){
            return;
        }

        //mebuat contentValues dimana nama kolom adalah key,
        //dan atribut barang dari editor adalah values
        ContentValues values = new ContentValues();
        values.put(KasirEntry.COLUMN_KATEGORI, kategoriString);
        values.put(KasirEntry.COLUMN_NAMA, namaString);

        int harga = 0;
        if (!TextUtils.isEmpty(hargaString)) {
            harga = Integer.parseInt(hargaString);
        }

        values.put(KasirEntry.COLUMN_HARGA, harga);

        if (mCurrentBarangUri == null){
            Uri newUri = getContentResolver().insert(KasirEntry.CONTENT_URI_BARANG, values);

            if (newUri == null){
                Toast.makeText(this, getString(R.string.fragment_data_barang_input_gagal),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.fragment_data_barang_input_berhasil),
                        Toast.LENGTH_SHORT).show();
            }

        }else {
            int rowsAffected = getContentResolver().update(mCurrentBarangUri, values, null, null);

            if (rowsAffected == 0){
                Toast.makeText(this, getString(R.string.fragment_data_barang_input_gagal),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.fragment_data_barang_input_berhasil),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_barang, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save_edit_barang:
                // Save pet to database
                saveBarang();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_reset_edit_barang:
                // Do nothing for now
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed () {
        //jika barang tidak jadi diubah. lanjutkan dengan handling back button pressed
        if (!mBarangHasChanged) {
            super.onBackPressed();
            return;
        }

        //jika tidak ada perubahan data, setup sebuah dialog untuk mengingatkan user.
        //membuat click listener untuk handle konfirmasi user bahwa perubahan data dibatalkan
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //user mengeklik discard button, tutup activity.
                        finish();
                    }
                };

                //tampilkan dialog
                showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //metode kencrot biar add barang tidak crash
        if (mCurrentBarangUri == null) {
            return null;
        }

        String[] projection = {
                KasirEntry._ID,
                KasirEntry.COLUMN_KATEGORI,
                KasirEntry.COLUMN_NAMA,
                KasirEntry.COLUMN_HARGA
        };

        return new CursorLoader(this,
                mCurrentBarangUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //pastikan awal kursor adalah null atau <1 baris pada cursor
        if (cursor == null || cursor.getCount() < 1){
            return;
        }

        //memproses dengan berpindah ke first row pada kursor dan membaca data
        //(PASTIKAN HARUS ADA 1 baris pada kursor)
        if (cursor.moveToFirst()) {
            //mencari kolom pada atribut barang yang kita maksud
            int kategoriColumnIndex = cursor.getColumnIndex(KasirEntry.COLUMN_KATEGORI);
            int namaColumnIndex = cursor.getColumnIndex(KasirEntry.COLUMN_NAMA);
            int hargaColumnIndex = cursor.getColumnIndex(KasirEntry.COLUMN_HARGA);

            //mengekstrak value dalam cursor untuk diberikan kepada column index
            String kategori = cursor.getString(kategoriColumnIndex);
            String nama = cursor.getString(namaColumnIndex);
            int harga = cursor.getInt(hargaColumnIndex);

            //update view pada screen dengan values dalam database
            mKategoriEditText.setText(kategori);
            mNamaEditText.setText(nama);
            mHargaEditText.setText(Integer.toString(harga));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //jika loader tidak mempunyai hak akses yang valid, hapus semua data dari input fields
        mKategoriEditText.setText("");
        mNamaEditText.setText("");
        mHargaEditText.setText("");
    }

    /**
     * tampilkan dialog yang mengingatkan user bahwa data akan hilang
     * jika user meninggalkan editor
     */

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        //membuat Alert Dialog.Builder dan membuat pesan. dan click listener
        //untuk button positif dan negatif pada dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //user klik "Keep Editing" button. jadi abaikan dialog
                //dan lanjutkan edit barang
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //mebuat dan menampilkan AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                deleteBarang();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                //user klik cancel button jadi dismiss dialog
                //dan lanjutkan edit barang
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //membuat dan tampilkan dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBarang() {
        if (mCurrentBarangUri != null) {
            //panggil content resolver untuk menghapus barang pada URI yang diberikan
            int rowsDeleted = getContentResolver().delete(mCurrentBarangUri, null, null);

            //tampilkan toast apakah delete berhasil
            if (rowsDeleted == 0) {
                //jika tidak ada kolom yang dihapus, tampilkan pesan error
                Toast.makeText(this, getString(R.string.fragment_data_barang_delete_gagal),
                        Toast.LENGTH_SHORT).show();
            }else {
                //jika delete berhasil
                Toast.makeText(this, getString(R.string.fragment_data_barang_delete_berhasil),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

}

