package com.example.navigationdrawer1;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.nio.charset.Charset;

public class VolumeDOWN {

    public static int VolumeDOWN_Implementation(int volume, BluetoothConnectionService mBluetoothConnection) {
        if(volume == 0) {//do nothing
        }
        else {
            volume = volume-1;
            //Log Variable Value in "Logcat"
            Log.i("*Volume value = ", volume+".");}
            //Send Function to Inform Pi Below
            String LEDVolumeDOWN = "[Volume]: " + volume + " ";
            byte[] bytesVolumeDOWN = LEDVolumeDOWN.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesVolumeDOWN);
        return volume;
    }
    public static void updateVolumeText(@NonNull TextView textviewVolumeDOWN, int Volumeclickcount) {
        //Method to Present Volume to User
        int VolumeTextViewclickcount = Volumeclickcount;
        //VolumeTextViewclickcount++;
        Log.i("*Volume Text value = ", VolumeTextViewclickcount +" ");
        textviewVolumeDOWN.setText("V: " + Volumeclickcount + " ");
    }
}
