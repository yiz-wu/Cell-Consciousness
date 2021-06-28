package com.example.ProgettoAMIF.UI.timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ProgettoAMIF.fasciaoraria.data.FasciaOraria;
import com.example.ProgettoAMIF.fasciaoraria.data.FasciaOrariaHandler;
import com.example.ProgettoAMIF.fasciaoraria.model.FasciaOrariaListAdapter;
import com.example.eserciziobroadcastreceiver.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TimerFragment extends Fragment {

    private final String TAG = "TimerFragment";
    private SharedPreferences sharedPref;
    private TextView tvHello;
    View root;
    FloatingActionButton fab;
    ListView listView;
    private FasciaOrariaHandler fasciaOrariaHandler;
    private FasciaOrariaListAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_timer, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated");

        fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick FAB");
                addFasciaOrariaRow();
                adapter.notifyDataSetChanged();
            }
        });

        tvHello = root.findViewById(R.id.tvHello);
        sharedPref = getContext().getSharedPreferences("name", Context.MODE_PRIVATE);

        fasciaOrariaHandler = new FasciaOrariaHandler(getContext());
        listView = root.findViewById(R.id.listView);
        adapter = new FasciaOrariaListAdapter(getContext(), R.layout.another_try_linear_layout, fasciaOrariaHandler.getList(), fasciaOrariaHandler);
        listView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.i(TAG, "onStart");
    }

    private void addFasciaOrariaRow() {
        Log.i(TAG, "addFasciaOrariaRow");
        fasciaOrariaHandler.addFasciaOraria(new FasciaOraria());
        fasciaOrariaHandler.printList(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        tvHello.setText(String.format("Hello %s!", sharedPref.getString("name", "User")));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        fasciaOrariaHandler.saveFasceOrarie();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        fasciaOrariaHandler.saveFasceOrarie();
    }
}