package com.example.diprogerfc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Debug;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button btnSave, btnLoad;

    SharedPreferences sPref;

    final String SAVED_TEXT = "saved_text";

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        // Inflate the layout for this fragment
        EditText ServoMin1,ServoMax1,ServoMin2,ServoMax2,
                ServoMin3,ServoMax3,ServoMin4,ServoMax4;
        ServoMin1 = view.findViewById(R.id.editTextNumberMinServo1);
        ServoMax1 = view.findViewById(R.id.editTextNumberMaxServo1);

        ServoMin2 = view.findViewById(R.id.editTextNumberMinServo2);
        ServoMax2 = view.findViewById(R.id.editTextNumberMaxServo2);

        ServoMin3 =view.findViewById(R.id.editTextNumberMinServo3);
        ServoMax3 = view.findViewById(R.id.editTextNumberMaxServo3);

        ServoMin4 = view.findViewById(R.id.editTextNumberMinServo4);
        ServoMax4 = view.findViewById(R.id.editTextNumberMaxServo4);

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();

        btnLoad = view.findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadData();

            }

            private void LoadData() {
                String savedString = prefs.getString("ServoPrefs", "");
                if(Objects.equals(savedString, "")){
                    savedString = "0,180,0,180,0,180,0,180,";
                }
                StringTokenizer st = new StringTokenizer(savedString, ",");
                Log.i("t", savedString);
                int[] savedList = new int[8];
                for (int i = 0; i < 8; i++) {

                    savedList[i] = Integer.parseInt(st.nextToken());
                }
                ServoMin1.setText(String.valueOf(savedList[0]));
                ServoMax1.setText(String.valueOf(savedList[1]));

                ServoMin2.setText(String.valueOf(savedList[2]));
                ServoMax2.setText(String.valueOf(savedList[3]));

                ServoMin3.setText(String.valueOf(savedList[4]));
                ServoMax3.setText(String.valueOf(savedList[5]));

                ServoMin4.setText(String.valueOf(savedList[6]));
                ServoMax4.setText(String.valueOf(savedList[7]));

            }
        });

        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] list = {ServoMin1.getText().toString(),ServoMax1.getText().toString(),
                ServoMin2.getText().toString(),ServoMax2.getText().toString(),
                ServoMin3.getText().toString(),ServoMax3.getText().toString(),
                ServoMin4.getText().toString(),ServoMax4.getText().toString()};

                StringBuilder str = new StringBuilder();
                for (int i = 0; i < list.length; i++) {
                    str.append(list[i]).append(",");
                }
                Log.i("t", String.valueOf(str));
                edit.putString("ServoPrefs", str.toString());
                edit.apply();

            }


        });

        return view;
    }

}