package com.example.navigationdrawer1;

import static com.example.navigationdrawer1.LED.updateLEDText;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    //Debugging TAG
    private static final String TAG = "BluetoothConnectionServ";
    //Chat Service Name
    private static final String appName = "MYAPP";
    //Create UUID (Unique Universal Identification Number) Variable (Of Device?) (UUID 2 of 2)
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    //Create Bluetooth Adapter to Handle Bluetooth Objects & Commands & Context
    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    //Variable for InsecureAcceptThread
    private AcceptThread mInsecureAcceptThread;
    //Create ConnectThread Variable
    private ConnectThread mConnectThread;
    //Create Bluetooth Device Object
    private BluetoothDevice mmDevice;
    //Create Global UUID
    private UUID deviceUUID;
    //Create Progress Dialog
    ProgressDialog mProgressDialog;
    //Create ConnectedThread Object
    private ConnectedThread mConnectedThread;
    //For TextView Updates
    private BluetoothConnectionService activity;
    //public TextView LEDtext;

    //Constructor
    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //When BluetoothConnection Object is Created, Call "start()" Method [public synchronized]
        //Initiates AcceptThread
        start();
    }

    //Thread that will Always be Waiting for a Connection & Runs until a Connection is Accepted or Cancelled
    //Will run on another thread so it does not use up the main resources on MainActivity thread
    private class AcceptThread extends Thread { //AcceptThread Class
        //Local Bluetooth Server Socket
        private final BluetoothServerSocket mmServerSocket;

        //AcceptThread Constructor to Declare Server Socket
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            //Create New Listening Server Socket for Other Devices to Connect to with "appName" & "UUID"
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                //Log
                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            } catch (IOException e) {Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());}
            mmServerSocket = tmp;
        }

        //Run Method (will automatically execute inside the thread so do not worry about calling the method)
        public void run() {
            Log.d(TAG, "run: AcceptThread Running.:");
            BluetoothSocket socket = null;
            try {
                //A Blocking Call & Will Only Return on a Successful Connection or Exception
                Log.d(TAG, "run: RFCOM server socket start...");
                //Code with Sit & Wait Until a Connection is Made or Fails
                socket = mmServerSocket.accept();
                //If Successful Connection, Code Advances to this Line
                Log.d(TAG, "run: RFCOM server socket accepted connection.");
            } catch (IOException e) {Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());}
            //Test if Socket is Null (if yes, move on to the next step)
            if (socket != null) {connected(socket, mmDevice);}
            Log.i(TAG, "END mAcceptThread.");
        }

        //End/ Cancel AcceptThread Method
        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {mmServerSocket.close();
                 }
            catch (IOException e) {Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed." + e.getMessage());}
        }
    }

    //Create ConnectThread Class (Initiates Bluetooth Connection with AcceptThread)
    //Thread will Run wile Attempting to Make an Outgoing Connection with a Device
    //*Note: Runs straight through -> connection succeeds or fails
    private class ConnectThread extends Thread {
        //Create Bluetooth Socket
        private BluetoothSocket mmSocket;

        //Create "ConnectThread" Default Constructor
        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device; //Bluetooth Device
            deviceUUID = uuid; //UUID
        }

        //Create Run Method (like what is in the AcceptThread)
        //Automatically Executes When a ConnectThread Object is Created
        public void run() {
            //Create Bluetooth Socket
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread.");
            //Get BluetoothSocket for Connection with BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to Create InsecureRfcommSocket using UUID: " + MY_UUID_INSECURE);
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not Create InsecureRfcommSocket " + e.getMessage());
            }
            //Assign Socket to Temporary Variable
            mmSocket = tmp;
            //Cancel Discovery if Connection is Made (Memory Intensive)
            mBluetoothAdapter.cancelDiscovery();
            //A Blocking Call & Will Only Return on a Successful Connection or Exception
            try {
                mmSocket.connect();
                Log.d(TAG, "run: ConnectThread connected."); //Connection Successfully Established
            } catch (IOException e) {
                //Close Socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to Close Connection in Socket " + e1.getMessage());
                }
                //If Connection Fails & Exception Thrown, Display Message
                Log.d(TAG, "run: ConnectThread: Could Not Connect to UUID: " + MY_UUID_INSECURE);
            }
            //If Exception was Not Thrown, Advance to This Part
            //Call Method "connected()" & Pass Socket "mmSocket" & Device "mmDevice"
            connected(mmSocket, mmDevice);
        }
        //Cancel Method to Cancel Connection
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            }
            catch (IOException e) {Log.e(TAG, "cancel: close() of mmSocket in ConnectThread failed." + e.getMessage());}
        }
    }

    //Method to Start Connection Service
    //Specifically Starts/Initiates AcceptThread to Begin "Listening (Server)" Mode
    //Called by Activity onResume()
    public synchronized void start() {
        //Method Specifically Starts AcceptThread
        Log.d(TAG, "start:");
        //If AcceptThread Exists, Cancel It & Start a New One (Cancel Any Thread Attempting to Make a Connection)
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        //If AcceptThread Does Not Exist, Start One
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start(); //*Note, this "start()" is not the same as the "start()" of this method
            //The "start()" Method Here is Native to the Thread Class & Initiates the Thread that Calls It
        }
    }

    //Method "StartClient" to Initiate the ConnectThread
    //While AcceptThread is Waiting for a Connection, ConnectThread Starts & Attempts to Make a Connection with Other Devices' "AcceptThread()"
    public void startClient(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startClient: Started.");
        //Create Progress Dialog Box to Appear when a Connection is Trying to be Made
        mProgressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth", "Please Wait...", true);
        //Declare ConnectThread Object & Start ConnectThread
        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start(); //*Note, this "start()" is not the same as the "start()" of this method
    }

    //"ConnectedThread" Class to Manage the Connection
    private class ConnectedThread extends Thread {
        //Create Variables (Socket, Input Stream, Output Stream)
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //Create Default Constructor
        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");
            //Declare Variables (Socket, Input Stream, Output Stream)
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            //Dismiss ProgressDialog Box when Connection is Established
            try {
                mProgressDialog.dismiss();
            } catch (NullPointerException e) {e.printStackTrace();}


            //Get Inputs & Outputs from Streams
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Declare Input & Output Streams
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        //Create "run()" Method
        public void run() {
            //Create Byte-Array Object to Get Input from the Input Stream (aka Buffer Store for Stream)
            byte[] buffer = new byte[1024];
            //Create Integer to Read Input from Input Stream (bytes returned from "read()")
            int bytes;
            //While Loop: Keep Listening to the InputStream Until an Exception Occurs
            while (true) {
                //Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    //Create String to Convert Incoming Message from "buffer" & "bytes" to "String"
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                    //Do Something with Incoming Message HERE****
                    String PiUpdate = incomingMessage;
                    //LED Cases
                    if(incomingMessage.charAt(1) == 'L') {
                        //LED Message -> "[LED]: color_here"
                        //TextView LEDText = (TextView) mProgressDialog.findViewById(R.id.textView0);//*************
                        Log.d(TAG, "Pi Updating LEDs");
                        if(incomingMessage.charAt(7) == 'R') {
                            //LED is set to "RED" (option 1)
                            Log.d(TAG, "LEDs are RED from Pi");
                            MainActivity.PILEDclickcount = 1;
                            MainActivity.LEDclickcount = 1;
                            Log.d(TAG, "LEDs are RED from Pi_Reassign");
                            Log.d(TAG, "Pi LED Click Count" + MainActivity.LEDclickcount);
                            //Update LED Textview
                           // LED.updateLEDText(LEDText, MainActivity.PILEDclickcount); //*************
                        }
                        if(incomingMessage.charAt(7) == 'B') {
                            //LED is set to "BLUE" (option 2)
                            Log.d(TAG, "LEDs are Blue from Pi");
                            MainActivity.PILEDclickcount = 2;
                            MainActivity.LEDclickcount = 2;
                            Log.d(TAG, "LEDs are BLUE from Pi_Reassign");
                            Log.d(TAG, "Pi LED Click Count" + MainActivity.LEDclickcount);
                            //Update LED Textview
                        }
                        if(incomingMessage.charAt(7) == 'G') {
                            //LED is set to "GREEN" (option 3)
                            Log.d(TAG, "LEDs are GREEN from Pi");
                            MainActivity.PILEDclickcount = 3;
                            MainActivity.LEDclickcount = 3;
                            Log.d(TAG, "LEDs are GREEN from Pi_Reassign");
                            Log.d(TAG, "Pi LED Click Count" + MainActivity.LEDclickcount);
                            //Update LED Textview
                        }
                        if(incomingMessage.charAt(7) == 'Y') {
                            //LED is set to "RED" (option 4)
                            Log.d(TAG, "LEDs are YELLOW from Pi");
                            MainActivity.PILEDclickcount = 4;
                            MainActivity.LEDclickcount = 4;
                            Log.d(TAG, "LEDs are YELLOW from Pi_Reassign");
                            Log.d(TAG, "Pi LED Click Count" + MainActivity.LEDclickcount);
                            //Update LED Textview
                        }
                        if(incomingMessage.charAt(7) == 'P') {
                            //LED is set to "PURPLE" (option 5)
                            Log.d(TAG, "LEDs are PURPLE from Pi");
                            MainActivity.PILEDclickcount = 5;
                            MainActivity.LEDclickcount = 5;
                            Log.d(TAG, "LEDs are PURPLE from Pi_Reassign");
                            Log.d(TAG, "Pi LED Click Count" + MainActivity.LEDclickcount);
                            //Update LED Textview
                        }
                        if(incomingMessage.charAt(7) == 'C') {
                            //LED is set to "CYAN" (option 6)
                            Log.d(TAG, "LEDs are CYAN from Pi");
                            MainActivity.PILEDclickcount = 6;
                            MainActivity.LEDclickcount = 6;
                            Log.d(TAG, "LEDs are CYAN from Pi_Reassign");
                            Log.d(TAG, "Pi LED Click Count" + MainActivity.LEDclickcount);
                            //Update LED Textview
                        }
                        if(incomingMessage.charAt(7) == 'W') {
                            //LED is set to "WHITE" (option 7)
                            Log.d(TAG, "LEDs are WHITE from Pi");
                            MainActivity.PILEDclickcount = 7;
                            MainActivity.LEDclickcount = 7;
                            Log.d(TAG, "LEDs are WHITE from Pi_Reassign");
                            Log.d(TAG, "Pi LED Click Count" + MainActivity.LEDclickcount);
                            //Update LED Textview
                        }
                        if(incomingMessage.charAt(7) == 'L') {
                            //LED is set to "LED Show" (option 8)
                            Log.d(TAG, "LEDs are LED SHOW from Pi");
                            MainActivity.PILEDclickcount = 8;
                            MainActivity.LEDclickcount = 8;
                            Log.d(TAG, "LEDs are LED SHOW from Pi_Reassign");
                            Log.d(TAG, "Pi LED Click Count" + MainActivity.LEDclickcount);
                            //Update LED Textview
                        }
                        if(incomingMessage.charAt(7) == 'O') {
                            //LED is set to "OFF" (option 0)
                            Log.d(TAG, "LEDs are OFF from Pi");
                            MainActivity.PILEDclickcount = 9;
                            MainActivity.LEDclickcount = 9;
                            Log.d(TAG, "LEDs are LED SHOW from Pi_Reassign");
                            Log.d(TAG, "Pi LED Click Count" + MainActivity.LEDclickcount);
                            //Update LED Textview
                        }
                    } //End of Pi LED Show Incoming Message
                    //Volume Cases
                    if(incomingMessage.charAt(1) == 'V') {
                        //LED Message -> "[VOLUME]: value_here"
                        Log.d(TAG, "Pi Updating Master Volume");
                        int PiVolumeValue = 0;
                        PiVolumeValue = Integer.parseInt(String.valueOf(incomingMessage.charAt(10)));
                        Log.d(TAG, "Volume is " + PiVolumeValue + " from Pi");
                        MainActivity.PIVOLUMEclickcount = PiVolumeValue;
                        MainActivity.Volumeclickcount = PiVolumeValue;
                        //Update Volume Textview
                    }//End of Pi Volume Incoming Message
                    //Slider Cases
                    if((incomingMessage.charAt(1) == 'S') && (incomingMessage.charAt(2) == 'L')) {
                        //Slider Message -> "[SLIDER #]: value_here
                        Log.d(TAG, "Pi Updating a Slider");
                        //Pi Changes Slider 1
                        if(Integer.parseInt(String.valueOf(incomingMessage.charAt(8))) == 1) {
                            //Obtain Slider Value from Pi String
                            int PiSlider1Value = 0;
                            int PiSlider1ValueP1 = 0;
                            int PiSlider1ValueP2 = 0;
                            int PiSlider1SIGN;// = Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));

                            if(incomingMessage.charAt(12) == '-') {
                                //Pi Updating Slider to -#
                                Log.d(TAG, "Pi is Updating -#");
                                PiSlider1ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider1ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(14)));
                                PiSlider1Value = -1 * (PiSlider1ValueP1 + PiSlider1ValueP2);}
                            else if(Integer.parseInt(String.valueOf(incomingMessage.charAt(12))) == 0) {
                                //Pi Updating Slider to 0
                                Log.d(TAG, "Pi is Updating to 0");
                                PiSlider1Value =0;}
                            else 
                                //Pi Updating Slider to +#
                            {
                                Log.d(TAG, "Pi is Updating +#");
                                PiSlider1ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));
                                PiSlider1ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider1Value = PiSlider1ValueP1 + PiSlider1ValueP2;
                            }
                            //Store Value into Global Variable
                            MainActivity.Mod1Value = PiSlider1Value;
                            //Set Slider 1 Value from Pi Value
                            MainActivity.Mod1Value = pgeFragment.setSlider1Value(MainActivity.Mod1Slider, PiSlider1Value);
                            //*Note: The Thumb of the Slider will NOT Update unless Prompted -> Programmatically Refresh pgeFragment every 1 [second]
                        }
                        //Pi Changes Slider 2
                        if(Integer.parseInt(String.valueOf(incomingMessage.charAt(8))) == 2) {
                            //Obtain Slider Value from Pi String
                            int PiSlider2Value = 0;
                            int PiSlider2ValueP1 = 0;
                            int PiSlider2ValueP2 = 0;
                            int PiSlider1SIGN;// = Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));

                            if(incomingMessage.charAt(12) == '-') {
                                //Pi Updating Slider to -#
                                Log.d(TAG, "Pi is Updating -#");
                                PiSlider2ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider2ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(14)));
                                PiSlider2Value = -1 * (PiSlider2ValueP1 + PiSlider2ValueP2);}
                            else if(Integer.parseInt(String.valueOf(incomingMessage.charAt(12))) == 0) {
                                //Pi Updating Slider to 0
                                Log.d(TAG, "Pi is Updating to 0");
                                PiSlider2Value =0;}
                            else 
                            //Pi Updating Slider to +#
                            {
                                Log.d(TAG, "Pi is Updating +#");
                                PiSlider2ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));
                                PiSlider2ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider2Value = PiSlider2ValueP1 + PiSlider2ValueP2;
                            }
                            //Store Value into Global Variable
                            MainActivity.Mod2Value = PiSlider2Value;
                            //Set Slider 1 Value from Pi Value
                            MainActivity.Mod2Value = pgeFragment.setSlider1Value(MainActivity.Mod2Slider, PiSlider2Value);
                            //*Note: The Thumb of the Slider will NOT Update unless Prompted -> Programmatically Refresh pgeFragment every 1 [second]
                        }
                        //Pi Changes Slider 3
                        if(Integer.parseInt(String.valueOf(incomingMessage.charAt(8))) == 3) {
                            //Obtain Slider Value from Pi String
                            int PiSlider3Value = 0;
                            int PiSlider3ValueP1 = 0;
                            int PiSlider3ValueP2 = 0;
                            int PiSlider1SIGN;// = Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));

                            if(incomingMessage.charAt(12) == '-') {
                                //Pi Updating Slider to -#
                                Log.d(TAG, "Pi is Updating -#");
                                PiSlider3ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider3ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(14)));
                                PiSlider3Value = -1 * (PiSlider3ValueP1 + PiSlider3ValueP2);}
                            else if(Integer.parseInt(String.valueOf(incomingMessage.charAt(12))) == 0) {
                                //Pi Updating Slider to 0
                                Log.d(TAG, "Pi is Updating to 0");
                                PiSlider3Value =0;}
                            else
                            //Pi Updating Slider to +#
                            {
                                Log.d(TAG, "Pi is Updating +#");
                                PiSlider3ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));
                                PiSlider3ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider3Value = PiSlider3ValueP1 + PiSlider3ValueP2;
                            }
                            //Store Value into Global Variable
                            MainActivity.Mod3Value = PiSlider3Value;
                            //Set Slider 1 Value from Pi Value
                            MainActivity.Mod3Value = pgeFragment.setSlider1Value(MainActivity.Mod3Slider, PiSlider3Value);
                            //*Note: The Thumb of the Slider will NOT Update unless Prompted -> Programmatically Refresh pgeFragment every 1 [second]
                        }
                        //Pi Changes Slider 4
                        if(Integer.parseInt(String.valueOf(incomingMessage.charAt(8))) == 4) {
                            //Obtain Slider Value from Pi String
                            int PiSlider4Value = 0;
                            int PiSlider4ValueP1 = 0;
                            int PiSlider4ValueP2 = 0;
                            int PiSlider1SIGN;// = Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));

                            if(incomingMessage.charAt(12) == '-') {
                                //Pi Updating Slider to -#
                                Log.d(TAG, "Pi is Updating -#");
                                PiSlider4ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider4ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(14)));
                                PiSlider4Value = -1 * (PiSlider4ValueP1 + PiSlider4ValueP2);}
                            else if(Integer.parseInt(String.valueOf(incomingMessage.charAt(12))) == 0) {
                                //Pi Updating Slider to 0
                                Log.d(TAG, "Pi is Updating to 0");
                                PiSlider4Value =0;}
                            else 
                            //Pi Updating Slider to +#
                            {
                                Log.d(TAG, "Pi is Updating +#");
                                PiSlider4ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));
                                PiSlider4ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider4Value = PiSlider4ValueP1 + PiSlider4ValueP2;
                            }
                            //Store Value into Global Variable
                            MainActivity.Mod4Value = PiSlider4Value;
                            //Set Slider 1 Value from Pi Value
                            MainActivity.Mod4Value = pgeFragment.setSlider1Value(MainActivity.Mod4Slider, PiSlider4Value);
                            //*Note: The Thumb of the Slider will NOT Update unless Prompted -> Programmatically Refresh pgeFragment every 1 [second]
                        }
                        //Pi Changes Slider 5
                        if(Integer.parseInt(String.valueOf(incomingMessage.charAt(8))) == 5) {
                            //Obtain Slider Value from Pi String
                            int PiSlider5Value = 0;
                            int PiSlider5ValueP1 = 0;
                            int PiSlider5ValueP2 = 0;
                            int PiSlider1SIGN;// = Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));

                            if(incomingMessage.charAt(12) == '-') {
                                //Pi Updating Slider to -#
                                Log.d(TAG, "Pi is Updating -#");
                                PiSlider5ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider5ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(14)));
                                PiSlider5Value = -1 * (PiSlider5ValueP1 + PiSlider5ValueP2);}
                            else if(Integer.parseInt(String.valueOf(incomingMessage.charAt(12))) == 0) {
                                //Pi Updating Slider to 0
                                Log.d(TAG, "Pi is Updating to 0");
                                PiSlider5Value =0;}
                            else 
                            //Pi Updating Slider to +#
                            {
                                Log.d(TAG, "Pi is Updating +#");
                                PiSlider5ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));
                                PiSlider5ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider5Value = PiSlider5ValueP1 + PiSlider5ValueP2;
                            }
                            //Store Value into Global Variable
                            MainActivity.Mod5Value = PiSlider5Value;
                            //Set Slider 1 Value from Pi Value
                            MainActivity.Mod5Value = pgeFragment.setSlider1Value(MainActivity.Mod5Slider, PiSlider5Value);
                            //*Note: The Thumb of the Slider will NOT Update unless Prompted -> Programmatically Refresh pgeFragment every 1 [second]
                        }
                        //Pi Changes Slider 6
                        if(Integer.parseInt(String.valueOf(incomingMessage.charAt(8))) == 6) {
                            //Obtain Slider Value from Pi String
                            int PiSlider6Value = 0;
                            int PiSlider6ValueP1 = 0;
                            int PiSlider6ValueP2 = 0;
                            int PiSlider1SIGN;// = Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));

                            if(incomingMessage.charAt(12) == '-') {
                                //Pi Updating Slider to -#
                                Log.d(TAG, "Pi is Updating -#");
                                PiSlider6ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider6ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(14)));
                                PiSlider6Value = -1 * (PiSlider6ValueP1 + PiSlider6ValueP2);}
                            else if(Integer.parseInt(String.valueOf(incomingMessage.charAt(12))) == 0) {
                                //Pi Updating Slider to 0
                                Log.d(TAG, "Pi is Updating to 0");
                                PiSlider6Value =0;}
                            else 
                            //Pi Updating Slider to +#
                            {
                                Log.d(TAG, "Pi is Updating +#");
                                PiSlider6ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));
                                PiSlider6ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider6Value = PiSlider6ValueP1 + PiSlider6ValueP2;
                            }
                            //Store Value into Global Variable
                            MainActivity.Mod6Value = PiSlider6Value;
                            //Set Slider 1 Value from Pi Value
                            MainActivity.Mod6Value = pgeFragment.setSlider1Value(MainActivity.Mod6Slider, PiSlider6Value);
                            //*Note: The Thumb of the Slider will NOT Update unless Prompted -> Programmatically Refresh pgeFragment every 1 [second]
                        }
                        //Pi Changes Slider 7
                        if(Integer.parseInt(String.valueOf(incomingMessage.charAt(8))) == 7) {
                            //Obtain Slider Value from Pi String
                            int PiSlider7Value = 0;
                            int PiSlider7ValueP1 = 0;
                            int PiSlider7ValueP2 = 0;
                            int PiSlider1SIGN;// = Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));

                            if(incomingMessage.charAt(12) == '-') {
                                //Pi Updating Slider to -#
                                Log.d(TAG, "Pi is Updating -#");
                                PiSlider7ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider7ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(14)));
                                PiSlider7Value = -1 * (PiSlider7ValueP1 + PiSlider7ValueP2);}
                            else if(Integer.parseInt(String.valueOf(incomingMessage.charAt(12))) == 0) {
                                //Pi Updating Slider to 0
                                Log.d(TAG, "Pi is Updating to 0");
                                PiSlider7Value =0;}
                            else 
                            //Pi Updating Slider to +#
                            {
                                Log.d(TAG, "Pi is Updating +#");
                                PiSlider7ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));
                                PiSlider7ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider7Value = PiSlider7ValueP1 + PiSlider7ValueP2;
                            }
                            //Store Value into Global Variable
                            MainActivity.Mod7Value = PiSlider7Value;
                            //Set Slider 1 Value from Pi Value
                            MainActivity.Mod7Value = pgeFragment.setSlider1Value(MainActivity.Mod7Slider, PiSlider7Value);
                            //*Note: The Thumb of the Slider will NOT Update unless Prompted -> Programmatically Refresh pgeFragment every 1 [second]
                        }
                        //Pi Changes Slider 8
                        if(Integer.parseInt(String.valueOf(incomingMessage.charAt(8))) == 8) {
                            //Obtain Slider Value from Pi String
                            int PiSlider8Value = 0;
                            int PiSlider8ValueP1 = 0;
                            int PiSlider8ValueP2 = 0;
                            int PiSlider1SIGN;// = Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));

                            if(incomingMessage.charAt(12) == '-') {
                                //Pi Updating Slider to -#
                                Log.d(TAG, "Pi is Updating -#");
                                PiSlider8ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider8ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(14)));
                                PiSlider8Value = -1 * (PiSlider8ValueP1 + PiSlider8ValueP2);}
                            else if(Integer.parseInt(String.valueOf(incomingMessage.charAt(12))) == 0) {
                                //Pi Updating Slider to 0
                                Log.d(TAG, "Pi is Updating to 0");
                                PiSlider8Value =0;}
                            else 
                            //Pi Updating Slider to +#
                            {
                                Log.d(TAG, "Pi is Updating +#");
                                PiSlider8ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));
                                PiSlider8ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider8Value = PiSlider8ValueP1 + PiSlider8ValueP2;
                            }
                            //Store Value into Global Variable
                            MainActivity.Mod8Value = PiSlider8Value;
                            //Set Slider 1 Value from Pi Value
                            MainActivity.Mod8Value = pgeFragment.setSlider1Value(MainActivity.Mod8Slider, PiSlider8Value);
                            //*Note: The Thumb of the Slider will NOT Update unless Prompted -> Programmatically Refresh pgeFragment every 1 [second]
                        }
                        //Pi Changes Slider 9
                        if(Integer.parseInt(String.valueOf(incomingMessage.charAt(8))) == 9) {
                            //Obtain Slider Value from Pi String
                            int PiSlider9Value = 0;
                            int PiSlider9ValueP1 = 0;
                            int PiSlider9ValueP2 = 0;
                            int PiSlider1SIGN;// = Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));

                            if(incomingMessage.charAt(12) == '-') {
                                //Pi Updating Slider to -#
                                Log.d(TAG, "Pi is Updating -#");
                                PiSlider9ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider9ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(14)));
                                PiSlider9Value = -1 * (PiSlider9ValueP1 + PiSlider9ValueP2);}
                            else if(Integer.parseInt(String.valueOf(incomingMessage.charAt(12))) == 0) {
                                //Pi Updating Slider to 0
                                Log.d(TAG, "Pi is Updating to 0");
                                PiSlider9Value =0;}
                            else 
                            //Pi Updating Slider to +#
                            {
                                Log.d(TAG, "Pi is Updating +#");
                                PiSlider9ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));
                                PiSlider9ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider9Value = PiSlider9ValueP1 + PiSlider9ValueP2;
                            }
                            //Store Value into Global Variable
                            MainActivity.Mod9Value = PiSlider9Value;
                            //Set Slider 1 Value from Pi Value
                            MainActivity.Mod9Value = pgeFragment.setSlider1Value(MainActivity.Mod9Slider, PiSlider9Value);
                            //*Note: The Thumb of the Slider will NOT Update unless Prompted -> Programmatically Refresh pgeFragment every 1 [second]
                        }
                        //Pi Changes Slider 10
                        if(Integer.parseInt(String.valueOf(incomingMessage.charAt(8))) == 10) {
                            //Obtain Slider Value from Pi String
                            int PiSlider10Value = 0;
                            int PiSlider10ValueP1 = 0;
                            int PiSlider10ValueP2 = 0;
                            int PiSlider1SIGN;// = Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));

                            if(incomingMessage.charAt(12) == '-') {
                                //Pi Updating Slider to -#
                                Log.d(TAG, "Pi is Updating -#");
                                PiSlider10ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider10ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(14)));
                                PiSlider10Value = -1 * (PiSlider10ValueP1 + PiSlider10ValueP2);}
                            else if(Integer.parseInt(String.valueOf(incomingMessage.charAt(12))) == 0) {
                                //Pi Updating Slider to 0
                                Log.d(TAG, "Pi is Updating to 0");
                                PiSlider10Value =0;}
                            else 
                            //Pi Updating Slider to +#
                            {
                                Log.d(TAG, "Pi is Updating +#");
                                PiSlider10ValueP1 = 10 * Integer.parseInt(String.valueOf(incomingMessage.charAt(12)));
                                PiSlider10ValueP2 = Integer.parseInt(String.valueOf(incomingMessage.charAt(13)));
                                PiSlider10Value = PiSlider10ValueP1 + PiSlider10ValueP2;
                            }
                            //Store Value into Global Variable
                            MainActivity.Mod10Value = PiSlider10Value;
                            //Set Slider 1 Value from Pi Value
                            MainActivity.Mod10Value = pgeFragment.setSlider1Value(MainActivity.Mod10Slider, PiSlider10Value);
                            //*Note: The Thumb of the Slider will NOT Update unless Prompted -> Programmatically Refresh pgeFragment every 1 [second]
                        }
                    } //End of Pi Slider Incoming Message
                    if((incomingMessage.charAt(1) == 'S') && (incomingMessage.charAt(2) == 'P')) {
                        //Pi Found New Speakers Message -> "[SPEAKERS]: Speaker #, Speaker #, ...
                        int numberofNewSpeakers = 0;
                        for(int i = 0; i < incomingMessage.length(); i++) {
                            //Count the Number fo Commas (Same as Speaker Count)
                            if((incomingMessage.charAt(i)) == ',') {MainActivity.NewSpeakerCount++;}
                        }
                        //Put Speakers into their Respective Positions********
                    }
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading InputStream." + e.getMessage());
                    break; //If there is a Problem with the InputStream, you want to Break the Loop & End the Connection
                }
            }
        }

        //Write Method to Send Data to Remote Device (called from MainActivity)
        public void write(byte[] bytes) {
            //Create String from the Bytes we are to Send to the "write()" Method
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to OutputStream: " + text);
            //Write to Output Stream
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to OutputStream." + e.getMessage());
            }
        }

        //Cancel Method to Shut Down the Connection (called from MainActivity)
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    //Create Method "connected()"
    //Manage Connection & Perform OutputStream Transmissions & Grab InputStream Transmissions
    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");
        //Thread to Manage Connection & Perform Transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        //Start ConnectedThread
        mConnectedThread.start();
    }

    //Another Write Method (previous method won't be accessible from MainActivity)
    //This Write Method can Access the Connection Service which can Access the ConnectedThread
    public void write(byte[] out) {
        //Create Temporary Object Thread
        ConnectedThread r;
        //Perform Write
        Log.d(TAG, "write: Write Called.");
        mConnectedThread.write(out);
    }
}
