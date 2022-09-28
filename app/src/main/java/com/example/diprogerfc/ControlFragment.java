package com.example.diprogerfc;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControlFragment extends Fragment {

    Button btnConnect;
    SharedPreferences sPref;
    EditText editIpAdress;
    private SensorManager sm;
    private Sensor s;
    SensorEventListener sl;

    float[] orientation;
    private float[] savedValues;
    private TextView Xvalue;
    private TextView Yvalue;
    private TextView Zvalue;
    private boolean firstRun;

    private DatagramSocket socket;
    private InetAddress address;
    private byte[] data;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ControlFragment newInstance(String param1, String param2) {
        ControlFragment fragment = new ControlFragment();
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void onResume() {
        super.onResume();
        sm.registerListener((SensorEventListener) sl, s, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        super.onPause();
        if (s != null) sm.unregisterListener(sl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        firstRun = true;
        editIpAdress = view.findViewById(R.id.editTextIPAdress);
        sPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();

           String ip = sPref.getString("IP_Adress",null);
           if(ip == null){
               editIpAdress.setText("127.0.0.1");
           }else {
               editIpAdress.setText(String.valueOf(ip));
           }


        savedValues = new float[]{0, 0, 0};

        Xvalue = view.findViewById(R.id.Xvalue);
        Yvalue = view.findViewById(R.id.Yvalue);
        Zvalue = view.findViewById(R.id.Zvalue);

        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        try {
            address = InetAddress.getByName("192.168.43.167");
            socket = new DatagramSocket(8080);

        } catch (IOException e) {
            e.printStackTrace();
        }

        sl = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
                orientation = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientation);

                orientation[0] = (float) Math.toDegrees(-orientation[0]); // Azimuth
                orientation[1] = (float) Math.toDegrees(-orientation[1]); // Pitch
                orientation[2] = (float) Math.toDegrees(orientation[2]); // Roll
                if(firstRun){
                    System.arraycopy(orientation, 0, savedValues, 0, orientation.length);
                    firstRun = false;
                }

                Xvalue.setText(String.format("Yaw: %s", Math.floor(orientation[0] - savedValues[0])));
                Yvalue.setText(String.format("Pitch: %s",Math.floor(orientation[1] - savedValues[1])));
                Zvalue.setText(String.format("Row: %s", Math.floor(orientation[2] - savedValues[2])));
                data = ((orientation[0]-savedValues[0]) + "," + (orientation[1]-savedValues[1]) + "," + (orientation[2]-savedValues[2]) + "\n").getBytes();

                DatagramPacket packet = new DatagramPacket(data, data.length, address, 8080);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        if (s != null) sm.registerListener(sl, s, SensorManager.SENSOR_DELAY_NORMAL);
        btnConnect = view.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putString("IP_Adress",editIpAdress.getText().toString());
                editor.apply();


            }
        });

        return  view;

    }

    private float map(float x,float inMin,float inMax,float OutMin,float OutMax){
        return (x-inMin)*(OutMax-OutMin)/(inMax-inMin)+OutMin;
    }



}