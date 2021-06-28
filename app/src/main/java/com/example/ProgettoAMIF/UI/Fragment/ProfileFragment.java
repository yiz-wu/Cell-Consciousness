package com.example.ProgettoAMIF.UI.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.eserciziobroadcastreceiver.R;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";
    View root;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private View lyConnectFB;
    private View lyLeaderboard;
    private ImageView ivAccountIcon;
    private TextView tvName;
    private Button changeName;
    private Context context;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        ivAccountIcon = root.findViewById(R.id.ivAccountIcon);
        tvName = root.findViewById(R.id.tvName);
        sharedPref = context.getSharedPreferences("name",Context.MODE_PRIVATE);
        lyConnectFB = root.findViewById(R.id.lyConnectFB);
        lyLeaderboard = root.findViewById(R.id.lyLeaderboard);

        View.OnClickListener coming_soon = new View.OnClickListener() {
            @Override
            public void onClick(View v) {   Toast.makeText(getContext(),"Coming soon", Toast.LENGTH_SHORT ).show();    }
        };
        lyConnectFB.setOnClickListener(coming_soon);
        lyLeaderboard.setOnClickListener(coming_soon);
        ivAccountIcon.setOnClickListener(coming_soon);

        changeName = root.findViewById(R.id.changeName);
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText editText = new EditText(context);
                editText.setText(tvName.getText());
                alert.setTitle("Change Name");
                alert.setView(editText);

                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newName = editText.getText().toString();
                        tvName.setText(newName);
                        editor = sharedPref.edit();
                        editor.putString("name", newName);
                        editor.apply();
                        Log.i(TAG, "onClick Confirm button. newName : "+newName);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) { }
                });
                alert.show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        tvName.setText(sharedPref.getString("name", "User"));

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}