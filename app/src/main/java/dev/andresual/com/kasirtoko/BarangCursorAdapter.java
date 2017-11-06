package dev.andresual.com.kasirtoko;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import dev.andresual.com.kasirtoko.data.KasirContract;

/**
 * Created by andresual on 5/3/2017.
 */

public class BarangCursorAdapter extends CursorAdapter {
    public BarangCursorAdapter (Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.nama);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndex(KasirContract.KasirEntry.COLUMN_NAMA);
        int hargaColumnIndex = cursor.getColumnIndex(KasirContract.KasirEntry.COLUMN_HARGA);

        String barangName = cursor.getString(nameColumnIndex);
        String barangHarga = cursor.getString(hargaColumnIndex);

        nameTextView.setText(barangName);
        summaryTextView.setText(barangHarga);
    }
}
