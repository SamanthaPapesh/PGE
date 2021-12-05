package com.example.navigationdrawer1;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.nio.charset.Charset;

public class VolumeUP {

    public static int VolumeUP_Implementation(int volume, BluetoothConnectionService mBluetoothConnection) {
        int VolumeClicks = volume;

        if(VolumeClicks<10) {
            VolumeClicks = VolumeClicks + 1; //Increment Value Upon Button Click
            //Debug to View Variable Value in "Logcat" & Validation
            Log.d("*Volume value = ", VolumeClicks +".");
            //Send Function to Inform Pi Below
            String LEDVolumeUP = "[Volume]: " + VolumeClicks + " ";
            byte[] bytesVolumeUP = LEDVolumeUP.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesVolumeUP);}

        if(VolumeClicks == 10) {
            //do nothing
        }
        return VolumeClicks;
    }
    public static void updateVolumeText(@NonNull TextView textviewVolumeUP, int Volumeclickcount) {
        //Method to Present Volume to User
        int VolumeTextViewclickcount = Volumeclickcount;
        Log.i("*Volume Text value = ", VolumeTextViewclickcount+".");

        if(VolumeTextViewclickcount < 10) {textviewVolumeUP.setText("V: " + VolumeTextViewclickcount + " ");}
        if(VolumeTextViewclickcount == 10) {textviewVolumeUP.setText("Max.");}
    }
}


