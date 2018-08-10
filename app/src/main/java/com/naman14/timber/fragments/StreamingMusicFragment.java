package com.naman14.timber.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.naman14.timber.R;
import com.naman14.timber.service.RetrofitInstance;
import com.naman14.timber.service.Router;
import com.naman14.timber.sesion.SessionManager;
import com.naman14.timber.streaming.MyDividerItemDecoration;
import com.naman14.timber.streaming.SitusAdapter;
import com.naman14.timber.streaming.SitusArray;
import com.naman14.timber.streaming.SitusList;
import com.naman14.timber.streaming.online.Online;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


public class StreamingMusicFragment extends Fragment implements  SitusAdapter.SitusAdapterListener{

    private RecyclerView recyclerView;
    private SitusAdapter mAdapter;
    private RetrofitInstance retrofit;

    public StreamingMusicFragment() {
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
        View view = inflater.inflate(R.layout.fragment_streaming_music, container, false);
        Getlist();

        return view;
    }

        private void Getstreaming(ArrayList<SitusList> Situs) {
            try {
                recyclerView = getView().findViewById(R.id.recycler_view_streaming);
                mAdapter = new SitusAdapter(getActivity(), Situs, this);
                // white background notification bar
                //  whiteNotificationBar(recyclerView);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 36));
                recyclerView.setAdapter(mAdapter);
            }catch (Exception e){

            }
        }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);

        }
    }


    @Override
    public void onSitusSelected(SitusList list) {
        Intent i = new Intent(getActivity(), Online.class);
        Bundle ambildata = new Bundle();
        ambildata.putString("parse_situs", list.Listsitus());
        i.putExtras(ambildata);
        startActivity(i);
    }



    public void Getlist(){
        retrofit = new RetrofitInstance();
        Router service = retrofit.getRetrofitInstanceall().create(Router.class);
        Call<SitusArray> call = service.Situslist(1);
        call.enqueue(new Callback<SitusArray>() {
            @Override
            public void onResponse(Call<SitusArray> call, retrofit2.Response<SitusArray> response) {
                Getstreaming(response.body().Getlistsitus());
            }
            @Override
            public void onFailure(Call<SitusArray> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Mengambil Data Dari Server Pastikan Anda Terhubung Dengan Internet " , Toast.LENGTH_SHORT).show();
            }
        });
    }
}
