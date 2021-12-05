package com.example.navigationdrawer1;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.nio.charset.Charset;

public class LED {

    public static int LED_Implementation(int LEDclickcount, BluetoothConnectionService mBluetoothConnection) {
        //Method: section of code to execute over & over again
        //LED Switch Program executed when LED_button is clicked
        int LEDButtonclicks = LEDclickcount;
        LEDButtonclicks++;

        //LED Cases:
        if (LEDButtonclicks == 0) { //(0) OFF
            //*This Case Should Never Appear
        }
        if (LEDButtonclicks == 1) { //(1) Red
            Log.i("*LED value = (RED) ", LEDButtonclicks + ".");
            //Send Function to Inform Pi Below
            String LEDSend = "[LED]: RED";
            byte[] bytesLED = LEDSend.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesLED);
        }
        if (LEDButtonclicks == 2) { //(2) Blue
            Log.i("*LED value = (BLUE) ", LEDButtonclicks + ".");
            //Send Function to Inform Pi Below
            String LEDSend = "[LED]: BLUE";
            byte[] bytesLED = LEDSend.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesLED);
        }
        if (LEDButtonclicks == 3) { //(3) Green
            Log.i("*LED value = (GREEN) ", LEDButtonclicks + ".");
            //Send Function to Inform Pi Below
            String LEDSend = "[LED]: GREEN";
            byte[] bytesLED = LEDSend.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesLED);
        }
        if (LEDButtonclicks == 4) { //(4) Yellow
            Log.i("*LED value = (YELLOW) ", LEDButtonclicks + ".");
            //Send Function to Inform Pi Below
            String LEDSend = "[LED]: YELLOW";
            byte[] bytesLED = LEDSend.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesLED);
        }
        if (LEDButtonclicks == 5) { //(5) Purple
            Log.i("*LED value = (PURPLE) ", LEDButtonclicks + ".");
            //Send Function to Inform Pi Below
            String LEDSend = "[LED]: PURPLE";
            byte[] bytesLED = LEDSend.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesLED);
        }
        if (LEDButtonclicks == 6) { //(6) Cyan
            Log.i("*LED value = (CYAN) ", LEDButtonclicks + ".");
            //Send Function to Inform Pi Below
            String LEDSend = "[LED]: CYAN";
            byte[] bytesLED = LEDSend.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesLED);
        }
        if (LEDButtonclicks == 7) { //(7) White
            Log.i("*LED value = (WHITE) ", LEDButtonclicks + ".");
            //Send Function to Inform Pi Below
            String LEDSend = "[LED]: WHITE";
            byte[] bytesLED = LEDSend.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesLED);
        }
        if (LEDButtonclicks == 8) { //(8) Light Show
            Log.i("*LED SHOW ", LEDButtonclicks + ".");
            //Send Function to Inform Pi Below
            String LEDSend = "[LED]: LED Show";
            byte[] bytesLED = LEDSend.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesLED);
        }
        if (LEDButtonclicks > 8) { //Return to Beginning, "(0) OFF"
            LEDButtonclicks = 0;
            Log.i("*LED value = (OFF) ", LEDButtonclicks + ".");
            //Send Function to Inform Pi Below
            String LEDSend = "[LED]: OFF";
            byte[] bytesLED = LEDSend.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytesLED);
        }
        return LEDButtonclicks;
    }
    public static void updateLEDText(@NonNull TextView textviewLED, int LEDclickcount) {
        //Method to Present LED Case to User
        int LEDTextViewcount = LEDclickcount;
        //LEDTextViewcount++;
        if (LEDTextViewcount == 0) {textviewLED.setText("Off");}
        if (LEDTextViewcount == 1) {textviewLED.setText("RED");}
        if (LEDTextViewcount == 2) {textviewLED.setText("BLUE");}
        if (LEDTextViewcount == 3) {textviewLED.setText("GREEN");}
        if (LEDTextViewcount == 4) {textviewLED.setText("YELLOW");}
        if (LEDTextViewcount == 5) {textviewLED.setText("PURPLE");}
        if (LEDTextViewcount == 6) {textviewLED.setText("CYAN");}
        if (LEDTextViewcount == 7) {textviewLED.setText("WHITE");}
        if (LEDTextViewcount == 8) {textviewLED.setText("LIGHT SHOW");}
        if (LEDTextViewcount > 8) {textviewLED.setText("LEDs OFF");}
    }

}
