package com.example.navigationdrawer1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

//Import Java Files/ Classes with Functions
import static com.example.navigationdrawer1.LED.LED_Implementation;
import static com.example.navigationdrawer1.VolumeUP.VolumeUP_Implementation;
import static com.example.navigationdrawer1.VolumeDOWN.VolumeDOWN_Implementation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;

import org.w3c.dom.Text;

import java.nio.charset.Charset;

public class pgeFragment extends Fragment {
    //Refresh Count
    public static int count = 0;
    TextView LEDtext; //***************

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Declare Sliders (10)
        //Slider Mod1Slider;
        Slider Mod2Slider;
        Slider Mod3Slider;
        Slider Mod4Slider;
        Slider Mod5Slider;
        Slider Mod6Slider;
        Slider Mod7Slider;
        Slider Mod8Slider;
        Slider Mod9Slider;
        Slider Mod10Slider;

        //Inflate Layout for PGE
        View view = inflater.inflate(R.layout.fragment_pge, container, false);

        //Compare String(s) for Spinner
        String[] SpeakerList = {"1", "2", "3", "5"};

        //Declare & Initialize LED_Button; LED Method
        Button LED_button = (Button) view.findViewById(R.id.LED_button);
        LEDtext = view.findViewById(R.id.textView0);
        //Create Local Variable to Update LED TextView & Call Update LED TextView Function
        int localLED = MainActivity.LEDclickcount;
        updateLEDText(LEDtext, localLED);
        //Set "OnClickListener" for LED_button
        LED_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call LED Function for LED Cases
                MainActivity.LEDclickcount = LED_Implementation(MainActivity.LEDclickcount, bluetoothFragment.mBluetoothConnection);
                //Initialize LED Textview & Update Textview Contents
                LED.updateLEDText(LEDtext, MainActivity.LEDclickcount);
            }
        });

        //Declare & Initialize Volume UP & Down Buttons; Volume Methods
        Button VolumeUP_Button = (Button) view.findViewById(R.id.volumeUPButton);
        Button VolumeDOWN_Button = (Button) view.findViewById(R.id.volumeDOWNButton);
        //Create Local Variable to Update Volume TextView & Call Update Volume TextView Function
        TextView volumetext = (TextView) view.findViewById(R.id.textViewVolume);
        int localVOLUME = MainActivity.Volumeclickcount;
        updateVolumeText(volumetext, localVOLUME);
        //Set "OnClickListener" for VolumeUP_Button
        VolumeUP_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            //Method for Volume UP Control
            public void onClick(View v) {
                //Call VolumeUP Function for Master Volume
                MainActivity.Volumeclickcount = VolumeUP_Implementation(MainActivity.Volumeclickcount, bluetoothFragment.mBluetoothConnection);
                //Initialize Volume Textview & Update Volume Textview Contents (Case:UP)
                VolumeUP.updateVolumeText(volumetext, MainActivity.Volumeclickcount);
            }
        });
        //Set "OnClickListener" for VolumeDOWN_Button
        VolumeDOWN_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            //Method for Volume DOWN Control
            public void onClick(View v) {
                //Call VolumeDOWN Function for Master Volume
                MainActivity.Volumeclickcount = VolumeDOWN_Implementation(MainActivity.Volumeclickcount, bluetoothFragment.mBluetoothConnection);
                //Initialize Volume Textview & Update Volume Textview Contents (Case:UP)
                VolumeDOWN.updateVolumeText(volumetext, MainActivity.Volumeclickcount);
            }
        });

        //Method for Spinner (Speaker Selection)
        //Initialize Spinner & Adapter
        Spinner spin = (Spinner) view.findViewById(R.id.spinner);
        spin.getBackground().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.row, R.id.SpinnerText, SpeakerList);
        //Correct 1st Speaker (Index 1) in Spinner Upon Calling the PGE Fragment
        MainActivity.Storage[1] = MainActivity.SpeakerListclickcount[1];
        MainActivity.SpeakerListclickcount[1] = 3;
        Log.d(TAG, "Call 1: Storage " + MainActivity.Storage[1]);
        Log.d(TAG, "Call 1: " + MainActivity.SpeakerListclickcount[1]);
        //Set Adapter so Data will be Shown to Spinner
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Obtain Speaker Number & Log for Confirmation
                String StrSpin = spin.getSelectedItem().toString();
                int IntSpin = Integer.parseInt(StrSpin);
                Log.d(TAG, "IntSpin: " + IntSpin);
                //Declare Local Array
                int[] local = new int[6];
                //Get Stored Value of Selected Speaker and Place into Local Array
                local[IntSpin] = MainActivity.SpeakerListclickcount[IntSpin];
                Log.d(TAG, "Call2 1: " + local[IntSpin]);
                //If Selected Speaker is Off, turn it On
                int caseInt = 0;
                if(local[IntSpin] == 0) {caseInt = 2;}
                //If Selected Speaker is On, turn it Off
                if(local[IntSpin] == 1) {caseInt = 1;}
                //If the Fragment was Just Selected (Not the Speaker Necessarily)
                if(local[IntSpin] == 3) {caseInt = 0;}

                Log.i(TAG, "*******Spinner Item STR: " + StrSpin + ".");
                Log.i(TAG, "*******Spinner Item INT: " + IntSpin + ".");
                Log.i(TAG, "Speaker Value BEFORE " + IntSpin + ": " + MainActivity.SpeakerListclickcount[IntSpin] + ".");

                switch (caseInt) {
                    case 1: //Case to Turn Speaker Off
                        MainActivity.SpeakerListclickcount[IntSpin] = 0;
                        //Change Color of Selected Speakers (Speakers OFF)
                        //Toast Message to User of Speaker State
                        Context context_spinItemDeSelected = getContext().getApplicationContext();
                        String text_spinItemDeSelected = "Disconnecting Speaker " + IntSpin;
                        int duration_spinItemDeSelected = Toast.LENGTH_SHORT;
                        Toast toastspinItemDeSelected = Toast.makeText(context_spinItemDeSelected, text_spinItemDeSelected, duration_spinItemDeSelected);
                        toastspinItemDeSelected.show();
                        //Send OFFSPEAKER Signal to Pi HERE
                        break;
                    case 2: //Case to Turn Speaker On
                        MainActivity.SpeakerListclickcount[IntSpin] = 1;
                        //Change Color of Selected Speakers (Speakers ON)
                        //Toast Message to User of Speaker State
                        Context context_spinItemSelected = getContext().getApplicationContext();
                        String text_spinItemSelected = "Connecting to Speaker " + IntSpin;
                        int duration_spinItemSelected = Toast.LENGTH_SHORT;
                        Toast toastspinItemSelected = Toast.makeText(context_spinItemSelected, text_spinItemSelected, duration_spinItemSelected);
                        toastspinItemSelected.show();
                        //Send ONSPEAKER Signal to Pi HERE
                        break;
                    case 0: //Case for When the App is Created & Speaker "1" is Chosen
                        MainActivity.SpeakerListclickcount[IntSpin] = 1;
                        break;
                }
                Log.i(TAG, "BooleanAFTER " + IntSpin + ": " + MainActivity.SpeakerListclickcount[IntSpin] + ".");

                //Print Out Array Contents
                for (int i = 1; i < MainActivity.SpeakerListclickcount.length; i++) {Log.i(TAG, "SCC" + i + ": " + MainActivity.SpeakerListclickcount[i] + ".");}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}// Do Nothing
        });


        //Initialize Sliders
        //Slider1: Initialize & Method for Obtaining Thumb Status
        MainActivity.Mod1Slider = (Slider) view.findViewById(R.id.Mod1);
        MainActivity.Mod1Value = setSlider1Value(MainActivity.Mod1Slider, MainActivity.Mod1Value);
        MainActivity.Mod1Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {} //do nothing
            //Record Current Thumb Status
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int SliderName = 1;
                MainActivity.Mod1Value = slider.getValue();
                //Debug to View Variable Value in "Logcat"
                Log.i("*Modulator 1 value = ", MainActivity.Mod1Value + ".");
                //Send Function to Inform Pi Below
                MainActivity.Mod1Value = Slider1SendData(SliderName, MainActivity.Mod1Value, bluetoothFragment.mBluetoothConnection);
            }

            private float Slider1SendData(int SliderName, float ModValue, BluetoothConnectionService mBluetoothConnection) {
                int nameSlider = SliderName;
                float modulatorValue = ModValue;
                String Slider1Value = "[Slider " + nameSlider + "]: " + modulatorValue + " ";
                byte[] bytes1Slider = Slider1Value.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes1Slider);
                return modulatorValue;
            }
        }); //End of Slider1 Method

        //Slider2: Initialize & Method for Obtaining Thumb Status
        Mod2Slider = (Slider) view.findViewById(R.id.Mod2);
        MainActivity.Mod2Value = setSlider2Value(Mod2Slider, MainActivity.Mod2Value);
        Mod2Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {} //do nothing
            //Record Current Thumb Status
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int SliderName = 2;
                MainActivity.Mod2Value = slider.getValue();
                //Debug to View Variable Value in "Logcat"
                Log.i("*Modulator 2 value = ", MainActivity.Mod2Value + ".");
                //Send Function to Inform Pi Below
                MainActivity.Mod2Value = Slider2SendData(SliderName, MainActivity.Mod2Value, bluetoothFragment.mBluetoothConnection);
            }

            private float Slider2SendData(int SliderName, float ModValue, BluetoothConnectionService mBluetoothConnection) {
                int nameSlider = SliderName;
                float modulatorValue = ModValue;
                String Slider2Value = "[Slider " + nameSlider + "]: " + modulatorValue + " ";
                byte[] bytes2Slider = Slider2Value.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes2Slider);
                return modulatorValue;
            }
        }); //End of Slider2 Method

        //Slider3: Initialize & Method for Obtaining Thumb Status
        Mod3Slider = (Slider) view.findViewById(R.id.Mod3);
        MainActivity.Mod3Value = setSlider3Value(Mod3Slider, MainActivity.Mod3Value);
        Mod3Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {} //do nothing
            //Record Current Thumb Status
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int SliderName = 3;
                MainActivity.Mod3Value = slider.getValue();
                //Debug to View Variable Value in "Logcat"
                Log.i("*Modulator 3 value = ", MainActivity.Mod3Value + ".");
                //Send Function to Inform Pi Below
                MainActivity.Mod3Value = Slider3SendData(SliderName, MainActivity.Mod3Value, bluetoothFragment.mBluetoothConnection);
            }

            private float Slider3SendData(int SliderName, float ModValue, BluetoothConnectionService mBluetoothConnection) {
                int nameSlider = SliderName;
                float modulatorValue = ModValue;
                String Slider3Value = "[Slider " + nameSlider + "]: " + modulatorValue + " ";
                byte[] bytes3Slider = Slider3Value.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes3Slider);
                return modulatorValue;
            }
        }); //End of Slider3 Method

        //Slider4: Initialize & Method for Obtaining Thumb Status
        Mod4Slider = (Slider) view.findViewById(R.id.Mod4);
        MainActivity.Mod4Value = setSlider4Value(Mod4Slider, MainActivity.Mod4Value);
        Mod4Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {} //do nothing
            //Record Current Thumb Status
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int SliderName = 4;
                MainActivity.Mod4Value = slider.getValue();
                //Debug to View Variable Value in "Logcat"
                Log.i("*Modulator 4 value = ", MainActivity.Mod4Value + ".");
                //Send Function to Inform Pi Below
                MainActivity.Mod4Value = Slider4SendData(SliderName, MainActivity.Mod4Value, bluetoothFragment.mBluetoothConnection);
            }

            private float Slider4SendData(int SliderName, float ModValue, BluetoothConnectionService mBluetoothConnection) {
                int nameSlider = SliderName;
                float modulatorValue = ModValue;
                String Slider4Value = "[Slider " + nameSlider + "]: " + modulatorValue + " ";
                byte[] bytes4Slider = Slider4Value.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes4Slider);
                return modulatorValue;
            }
        }); //End of Slider4 Method

        //Slider5: Initialize & Method for Obtaining Thumb Status
        Mod5Slider = (Slider) view.findViewById(R.id.Mod5);
        MainActivity.Mod5Value = setSlider5Value(Mod5Slider, MainActivity.Mod5Value);
        Mod5Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {} //do nothing
            //Record Current Thumb Status
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int SliderName = 5;
                MainActivity.Mod5Value = slider.getValue();
                //Debug to View Variable Value in "Logcat"
                Log.i("*Modulator 5 value = ", MainActivity.Mod5Value + ".");
                //Send Function to Inform Pi Below
                MainActivity.Mod5Value = Slider5SendData(SliderName, MainActivity.Mod5Value, bluetoothFragment.mBluetoothConnection);
            }

            private float Slider5SendData(int SliderName, float ModValue, BluetoothConnectionService mBluetoothConnection) {
                int nameSlider = SliderName;
                float modulatorValue = ModValue;
                String Slider5Value = "[Slider " + nameSlider + "]: " + modulatorValue + " ";
                byte[] bytes5Slider = Slider5Value.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes5Slider);
                return modulatorValue;
            }
        }); //End of Slider5 Method

        //Slider6: Initialize & Method for Obtaining Thumb Status
        Mod6Slider = (Slider) view.findViewById(R.id.Mod6);
        MainActivity.Mod6Value = setSlider6Value(Mod6Slider, MainActivity.Mod6Value);
        Mod6Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {} //do nothing
            //Record Current Thumb Status
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int SliderName = 6;
                MainActivity.Mod6Value = slider.getValue();
                //Debug to View Variable Value in "Logcat"
                Log.i("*Modulator 6 value = ", MainActivity.Mod6Value + ".");
                //Send Function to Inform Pi Below
                MainActivity.Mod6Value = Slider6SendData(SliderName, MainActivity.Mod6Value, bluetoothFragment.mBluetoothConnection);
            }

            private float Slider6SendData(int SliderName, float ModValue, BluetoothConnectionService mBluetoothConnection) {
                 int nameSlider = SliderName;
                float modulatorValue = ModValue;
                String Slider6Value = "[Slider " + nameSlider + "]: " + modulatorValue + " ";
                byte[] bytes6Slider = Slider6Value.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes6Slider);
                return modulatorValue;
            }
        }); //End of Slider6 Method

        //Slider7: Initialize & Method for Obtaining Thumb Status
        Mod7Slider = (Slider) view.findViewById(R.id.Mod7);
        MainActivity.Mod7Value = setSlider7Value(Mod7Slider, MainActivity.Mod7Value);
        Mod7Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {} //do nothing
            //Record Current Thumb Status
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int SliderName = 7;
                MainActivity.Mod7Value = slider.getValue();
                //Debug to View Variable Value in "Logcat"
                Log.i("*Modulator 7 value = ", MainActivity.Mod7Value + ".");
                //Send Function to Inform Pi Below
                MainActivity.Mod7Value = Slider7SendData(SliderName, MainActivity.Mod7Value, bluetoothFragment.mBluetoothConnection);
            }

            private float Slider7SendData(int SliderName, float ModValue, BluetoothConnectionService mBluetoothConnection) {
                int nameSlider = SliderName;
                float modulatorValue = ModValue;
                String Slider7Value = "[Slider " + nameSlider + "]: " + modulatorValue + " ";
                byte[] bytes7Slider = Slider7Value.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes7Slider);
                return modulatorValue;
            }
        }); //End of Slider7 Method

        //Slider8: Initialize & Method for Obtaining Thumb Status
        Mod8Slider = (Slider) view.findViewById(R.id.Mod8);
        MainActivity.Mod8Value = setSlider8Value(Mod8Slider, MainActivity.Mod8Value);
        Mod8Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {} //do nothing
            //Record Current Thumb Status
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int SliderName = 8;
                MainActivity.Mod8Value = slider.getValue();
                //Debug to View Variable Value in "Logcat"
                Log.i("*Modulator 8 value = ", MainActivity.Mod8Value + ".");
                //Send Function to Inform Pi Below
                MainActivity.Mod8Value = Slider8SendData(SliderName, MainActivity.Mod8Value, bluetoothFragment.mBluetoothConnection);
            }

            private float Slider8SendData(int SliderName, float ModValue, BluetoothConnectionService mBluetoothConnection) {
                int nameSlider = SliderName;
                float modulatorValue = ModValue;
                String Slider8Value = "[Slider " + nameSlider + "]: " + modulatorValue + " ";
                byte[] bytes8Slider = Slider8Value.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes8Slider);
                return modulatorValue;
            }
        }); //End of Slider8 Method

        //Slider9: Initialize & Method for Obtaining Thumb Status
        Mod9Slider = (Slider) view.findViewById(R.id.Mod9);
        MainActivity.Mod9Value = setSlider9Value(Mod9Slider, MainActivity.Mod9Value);
        Mod9Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {} //do nothing
            //Record Current Thumb Status
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int SliderName = 9;
                MainActivity.Mod9Value = slider.getValue();
                //Debug to View Variable Value in "Logcat"
                Log.i("*Modulator 9 value = ", MainActivity.Mod9Value + ".");
                //Send Function to Inform Pi Below
                MainActivity.Mod9Value = Slider9SendData(SliderName, MainActivity.Mod9Value, bluetoothFragment.mBluetoothConnection);
            }

            private float Slider9SendData(int SliderName, float ModValue, BluetoothConnectionService mBluetoothConnection) {
                int nameSlider = SliderName;
                float modulatorValue = ModValue;
                String Slider9Value = "[Slider " + nameSlider + "]: " + modulatorValue + " ";
                byte[] bytes9Slider = Slider9Value.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes9Slider);
                return modulatorValue;
            }
        }); //End of Slider9 Method

        //Slider10: Initialize & Method for Obtaining Thumb Status
        Mod10Slider = (Slider) view.findViewById(R.id.Mod10);
        MainActivity.Mod10Value = setSlider10Value(Mod10Slider, MainActivity.Mod10Value);
        Mod10Slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {} //do nothing
            //Record Current Thumb Status
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int SliderName = 10;
                MainActivity.Mod10Value = slider.getValue();
                //Debug to View Variable Value in "Logcat"
                Log.i("*Modulator 10 value = ", MainActivity.Mod10Value + ".");
                //Send Function to Inform Pi Below
                MainActivity.Mod10Value = Slider10SendData(SliderName, MainActivity.Mod10Value, bluetoothFragment.mBluetoothConnection);
            }

            private float Slider10SendData(int SliderName, float ModValue, BluetoothConnectionService mBluetoothConnection) {
                int nameSlider = SliderName;
                float modulatorValue = ModValue;
                String Slider10Value = "[Slider " + nameSlider + "]: " + modulatorValue + " ";
                byte[] bytes10Slider = Slider10Value.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes10Slider);
                return modulatorValue;
            }
        }); //End of Slider10 Method

        return view;
    }
    //Set Initial Value of Sliders from the Previous State Before Switching Fragments/ Activities
    public static float setSlider1Value(Slider slider, float Mod1Value) {slider.setValue(MainActivity.Mod1Value); return MainActivity.Mod1Value;}
    public float setSlider2Value(Slider slider, float Mod2Value) {slider.setValue(MainActivity.Mod2Value); return MainActivity.Mod2Value;}
    public float setSlider3Value(Slider slider, float Mod3Value) {slider.setValue(MainActivity.Mod3Value); return MainActivity.Mod3Value;}
    public float setSlider4Value(Slider slider, float Mod4Value) {slider.setValue(MainActivity.Mod4Value); return MainActivity.Mod4Value;}
    public float setSlider5Value(Slider slider, float Mod5Value) {slider.setValue(MainActivity.Mod5Value); return MainActivity.Mod5Value;}
    public float setSlider6Value(Slider slider, float Mod6Value) {slider.setValue(MainActivity.Mod6Value); return MainActivity.Mod6Value;}
    public float setSlider7Value(Slider slider, float Mod7Value) {slider.setValue(MainActivity.Mod7Value); return MainActivity.Mod7Value;}
    public float setSlider8Value(Slider slider, float Mod8Value) {slider.setValue(MainActivity.Mod8Value); return MainActivity.Mod8Value;}
    public float setSlider9Value(Slider slider, float Mod9Value) {slider.setValue(MainActivity.Mod9Value); return MainActivity.Mod9Value;}
    public float setSlider10Value(Slider slider, float Mod10Value) {slider.setValue(MainActivity.Mod10Value); return MainActivity.Mod10Value;}

    //Method to Update the TextView of the LED Show
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

    //Method to Update the TextView of the Volume
    public static void updateVolumeText(@NonNull TextView textviewVolumeUP, int Volumeclickcount) {
        //Method to Present Volume to User
        int VolumeTextViewclickcount = Volumeclickcount;
        Log.i("*Volume Text value = ", VolumeTextViewclickcount+".");

        if(VolumeTextViewclickcount < 10) {textviewVolumeUP.setText("V: " + VolumeTextViewclickcount + " ");}
        if(VolumeTextViewclickcount == 10) {textviewVolumeUP.setText("Max.");}
    }

    //Refresh Functions (2)
    public static void content(){
        count++;
        //************if(MainActivity.LEDclickcount == 0) {LEDtext.setText("OFF");}
        //After end of content(), call this function [milliseconds]
        refresh(1000);
    }
    public static void refresh(int milliseconds) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                content();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }
}
