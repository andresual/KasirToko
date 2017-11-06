package dev.andresual.com.kasirtoko;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import dev.andresual.com.kasirtoko.data.KasirContract;
import dev.andresual.com.kasirtoko.data.KasirDbHelper;
import dev.andresual.com.kasirtoko.data.KasirContract.KasirEntry;

import static android.content.ContentValues.TAG;


public class FragmentTransaksi extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    CursorAdapter mCursorAdapter;
    Calendar calendar;
    SimpleDateFormat simpledateformat;
    String date;
    TextView displayDateTime;

    private static final int BARANG_LOADER = 0;

    public FragmentTransaksi() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_transaksi, container, false);
        setHasOptionsMenu(true);

        //return inflater.inflate(R.layout.form_data_barang, container, false);
        //View v = inflater.inflate(R.layout.fragment_transaksi, container, false);

        //tampil kalender
        displayDateTime = (TextView) v.findViewById(R.id.waktu_transaksi);
        calendar = Calendar.getInstance(Locale.getDefault());
        simpledateformat = new SimpleDateFormat("MM-dd-yy hh:mm:ss a", Locale.getDefault());
        date = simpledateformat.format(calendar.getTime());
        displayDateTime.setText(date);

        setHasOptionsMenu(true);

        return v;
    }

    //cursor adapter untuk menampilkan listview
    private class TransaksiAdapter extends CursorAdapter {

        public TransaksiAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_item_transaksi, parent, false);
        }

        @Override
        public void bindView(final View view, Context context, final Cursor cursor) {

                cursor.getPosition();

                //deklarasi semua item pada listView
                TextView nameTextView = (TextView) view.findViewById(R.id.nama);
                TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
                Button buttonKurang = (Button) view.findViewById(R.id.btn_kurang);
                final Button buttonTambah = (Button) view.findViewById(R.id.btn_tambah);

                //mengambil data dari database
                int nameColumnIndex = cursor.getColumnIndex(KasirContract.KasirEntry.COLUMN_NAMA);
                final int hargaColumnIndex = cursor.getColumnIndex(KasirContract.KasirEntry.COLUMN_HARGA);

                //menjadikan object menjadi string
                String barangName = cursor.getString(nameColumnIndex);
                String barangHarga = cursor.getString(hargaColumnIndex);

                //menampilkan pada string pada UI
                nameTextView.setText(barangName);
                summaryTextView.setText(barangHarga);

                final int[] hargaSatuan = {cursor.getInt(hargaColumnIndex)};

                //deklarasi value picker
                final int[] valuePicker = {0};

                //deklarasi harga satuan
                final int[] totalHargaPerList = {0};

                //deklarasi total harga
                final int[] grandTotal = {0};

                buttonTambah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cursor.getPosition();

                        valuePicker[0] = valuePicker[0] + 1;
                        totalHargaPerList[0] = hargaSatuan[0] * valuePicker[0];

                        totalHarga(totalHargaPerList[0], view);
                        displayValuePicker(valuePicker[0], view);
                        //grandTotal(grandTotal[0], view);
                        System.out.println(Arrays.toString(totalHargaPerList));
                    }
                });

                buttonKurang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        valuePicker[0] = valuePicker[0] - 1;
                        totalHargaPerList[0] = hargaSatuan[0] * valuePicker[0];

                        totalHarga(totalHargaPerList[0], view);
                        displayValuePicker(valuePicker[0], view);
                    }
                });
            }

        private void displayValuePicker(int number, View valuePickerView) {
            TextView valuePicker = (TextView) valuePickerView.findViewById(R.id.value_picker);
            valuePicker.setText(String.valueOf(number));
        }

        private void totalHarga(int total, View hargaView) {
            TextView totalHargaTextView = (TextView) hargaView.findViewById(R.id.total);
            totalHargaTextView.setText(String.valueOf(total));
        }
    }

    //menampilkan grandTotal textView pada fragment
    public void grandTotal(int grandTotal, View grandTotalView) {
        TextView grandTotalTextView  = (TextView) getActivity().findViewById(R.id.text_nominal);
        grandTotalTextView.setText(String.valueOf(grandTotal));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).setActionBarTitle("Transaksi");

        //eksekusi button submit untuk trigger hitung jumlah total pada listview
        Button buttonSubmit = (Button) getActivity().findViewById(R.id.btn_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "kencrotan", Toast.LENGTH_SHORT).show();
            }
        });

        final ListView transaksiListview = (ListView) getActivity().findViewById(R.id.list_transaksi);

        //setup adapter untuk membuat list item pada setiap row pada pet data dalam cursor
        mCursorAdapter = new TransaksiAdapter(getActivity(), null);
        transaksiListview.setAdapter(mCursorAdapter);

        transaksiListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                transaksiListview.getItemAtPosition(position);
                System.out.println(position);

            }
        });

        getLoaderManager().initLoader(BARANG_LOADER, null, this);
    }

    //tempat display databaseinfo

    private void insertTransaksi(){
        //membuat database helper
        KasirDbHelper mDbHelper = new KasirDbHelper(getActivity());

        //database write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        TextView mWaktuTransaksiTextView = (TextView) getActivity().findViewById(R.id.waktu_transaksi);
        TextView mTotalransaksiTextView = (TextView) getActivity().findViewById(R.id.text_nominal);
        String waktuTransaksiString = mWaktuTransaksiTextView.getText().toString();
        String totalTransaksiString = mTotalransaksiTextView.getText().toString().trim();
        int total = Integer.parseInt(totalTransaksiString);

        ContentValues values = new ContentValues();
        values.put(KasirEntry.COLUMN_WAKTU_TRANSAKSI, waktuTransaksiString);
        values.put(KasirEntry.COLUMN_TOTAL_TRANSAKSI, total);

        //memasukkan data transaksi baru ke dalam provider. mengebalikan content URI untuk transaksi baru
        Uri newUri = getActivity().getContentResolver().insert(KasirEntry.CONTENT_URI_TRANSAKSI, values);

        //menampilkan toast jika ada kesalahan pada input
        if (newUri == null){
            Toast.makeText(getActivity(), getString(R.string.fragment_transaksi_input_gagal),
                    Toast.LENGTH_SHORT).show();
        }else {
            //menampilkan toast jika input berhasil
            Toast.makeText(getActivity(), getString(R.string.fragment_transaksi_input_berhasil),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

        //menampilkan opsi pada layout
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_form_transaksi, menu);
    }

        //memberi aksi pada opsi jika ditekan
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_transaksi:
                insertTransaksi();
                return true;

            case R.id.action_print_transaksi:
                //sembarang
                return true;
            //int id =item.getItemId();
            //if (id == R.id.action_save){
            //sembarang
            //    insertTransaksi();
            //    return true;
            //}

            //if (id == R.id.action_print){
            //    //sembarang
            //    return true;
            //}
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
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
