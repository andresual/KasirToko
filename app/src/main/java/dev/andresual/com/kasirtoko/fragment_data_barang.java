package dev.andresual.com.kasirtoko;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import dev.andresual.com.kasirtoko.data.KasirContract.KasirEntry;
import dev.andresual.com.kasirtoko.data.KasirDbHelper;

/**
 * Created by andresual on 3/23/2017.
 */

public class fragment_data_barang extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mKategoriEditText;
    private EditText mNamaEditText;
    private EditText mHargaEditText;
    private KasirDbHelper mDbHelper;
    private static final int BARANG_LOADER = 0;
    private Uri mCurrentBarangUri;

    BarangCursorAdapter mCursorAdapter;

    public fragment_data_barang(){
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //((MainActivity)getActivity()).setActionBarTitle("Data Barang");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mDbHelper = new KasirDbHelper(getActivity());
        //insertBarang();
        //displayDatabaseInfo();
    }

    //tempat display databaseinfo

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.form_data_barang, container, false);
        setHasOptionsMenu(true);

        //mencari listView yang mempopulasikan data barang
        final ListView barangListview = (ListView) v.findViewById(R.id.list);
        View emptyView = v.findViewById(R.id.empty_view);

        //mencari dan set empty view pada listview yang tertampil jika list kosong
        barangListview.setEmptyView(emptyView);

        //setup adapter untuk membuat list item pada setiap row pada pet data dalam cursor
        mCursorAdapter = new BarangCursorAdapter(getActivity(), null);
        barangListview.setAdapter(mCursorAdapter);

        //setup item click listener
        barangListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               Intent intent = new Intent(fragment_data_barang.this.getActivity(), EditBarang.class);

                Uri currentBarangUri = ContentUris.withAppendedId(KasirEntry.CONTENT_URI_BARANG, id);

                intent.setData(currentBarangUri);
                startActivity(intent);
            }
        });

        //kick off the loader
        getLoaderManager().initLoader(BARANG_LOADER, null, this);

        return v;
        //return inflater.inflate(R.layout.form_data_barang, container, false);
        }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).setActionBarTitle("Data Barang");
    }

    //tempat display database info

    private void insertBarang(){

        //membuat database helper
        KasirDbHelper mDbHelper = new KasirDbHelper(getActivity());

        //database write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        mKategoriEditText = (EditText) getActivity().findViewById(R.id.input_kategori);
        mNamaEditText = (EditText) getActivity().findViewById(R.id.input_nama_barang);
        mHargaEditText = (EditText) getActivity().findViewById(R.id.input_harga_barang);

        String kategoriString = mKategoriEditText.getText().toString().trim();
        String namaString = mNamaEditText.getText().toString().trim();
        String hargaString = mHargaEditText.getText().toString().trim();
        //int harga = Integer.parseInt(hargaString);

        if (mCurrentBarangUri == null &&
                TextUtils.isEmpty(kategoriString) && TextUtils.isEmpty(namaString) &&
                TextUtils.isEmpty(hargaString)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(KasirEntry.COLUMN_KATEGORI, kategoriString);
        values.put(KasirEntry.COLUMN_NAMA, namaString);

        int harga = 0;
        if (!TextUtils.isEmpty(hargaString)) {
            harga = Integer.parseInt(hargaString);
        }

        values.put(KasirEntry.COLUMN_HARGA, harga);

        //memasukkan barang baru ke dalam provider. mengembalikan content URI untuk barang baru
        Uri newUri = getActivity().getContentResolver().insert(KasirEntry.CONTENT_URI_BARANG, values);

        //menampilkan toast jika ada kesalahan pada input
        if (newUri == null){
            Toast.makeText(getActivity(), getString(R.string.fragment_data_barang_input_gagal),
                    Toast.LENGTH_SHORT).show();
        }else {
            //menampilkan toast jika input berhasil
            Toast.makeText(getActivity(), getString(R.string.fragment_data_barang_input_berhasil),
                    Toast.LENGTH_SHORT).show();
        }

        //long newRowId = db.insert(KasirEntry.TABLE_NAME, null, values);

        //if (newRowId == -1){
        //    Toast.makeText(getActivity(), "Error saat menyimpan barang", Toast.LENGTH_SHORT).show();
        //}else {
        //    Toast.makeText(getActivity(), "Barang tersimpan dengan ro id : " + newRowId, Toast.LENGTH_SHORT).show();
        //}
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_form_data_barang, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_save:
                insertBarang();
                //displayDatabaseInfo();
                //getActivity().finish();
                return true;

            case R.id.action_reset:
                //sembarang
                return true;
        }
        //int id =item.getItemId();
        //if (id == R.id.action_save){
        //    insertBarang();
        //    return true;
        //}

        //if (id == R.id.action_reset){
        //    //sembarang
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onDetach(){
    super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //mendefinisikan projection yang menunjukkan kolom pada tabel
        String[] projection = {
                KasirEntry._ID,
                KasirEntry.COLUMN_KATEGORI,
                KasirEntry.COLUMN_NAMA,
                KasirEntry.COLUMN_HARGA
        };
        //loader akan mengeksekusi content provider query method pada background thread
        return new CursorLoader(getActivity(),
                KasirEntry.CONTENT_URI_BARANG,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
