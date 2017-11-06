package dev.andresual.com.kasirtoko;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.Button;


public class fragmentLaporan extends Fragment {

    public fragmentLaporan() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_laporan, container, false);

        View v = inflater.inflate(R.layout.fragment_laporan, container, false);

        Button btnTransaksi = (Button) v.findViewById(R.id.btn_laporan_transaksi);
        Button btnHarian = (Button) v.findViewById(R.id.btn_laporan_harian);
        Button btnBulanan = (Button) v.findViewById(R.id.btn_laporan_bulanan);

        btnTransaksi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view1) {
                Intent intent = new Intent(getActivity(),LaporanTransaksi.class);
                startActivity(intent);
            }
        });

        btnHarian.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view2) {
                Intent intenta = new Intent(getActivity(),LaporanHarian.class);
                startActivity(intenta);
            }
        });

        btnBulanan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view3) {
                Intent intentb = new Intent(getActivity(),LaporanBulanan.class);
                startActivity(intentb);
            }
        });

        return (v);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).setActionBarTitle("Laporan");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
