package com.example.navigationdrawer1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

public class bluetoothFragment extends Fragment implements AdapterView.OnItemClickListener{


    @Nullable
    //Make Global Bluetooth Adapter
    BluetoothAdapter mBluetoothAdapter;
    //Create ArrayList to Hold Devices Discovered
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    //Create DeviceListAdapter;
    public DeviceListAdapter mDeviceListAdapter;
    //Create Listview
    ListView lvNewDevices;
    //Create Context "this"
    Context mmmContext;
    //public bluetoothFragment(Context context) {mContext = context;}

    //Need UUID (Of Device?) (UUID 1 of 2)
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    //Create BluetoothDevice
    BluetoothDevice mBTDevice;

    //Create BluetoothConnectionService Object
    public static BluetoothConnectionService mBluetoothConnection;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Get Default Adapter (Initialize BT Adapter Object)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Inflate Layout for Bluetooth
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        //Broadcast Receiver 1
        //Enable/Disable Bluetooth & ACTION_FOUND
        //Catches ALL State Changes to the BT
        final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //When discovery finds a device
                if(action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                    //Define Integer to Define State From Intent
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                    //Switch State with State Changes
                    switch(state) {
                        case BluetoothAdapter.STATE_OFF:
                            Log.d(TAG, "onReceive:STATE_OFF");
                            //Toast Message to User of BR1 State
                            Context contextBR1_OFF = getContext().getApplicationContext();
                            String text_OFF = "Bluetooth OFF";
                            int duration_OFF = Toast.LENGTH_SHORT;
                            Toast toastBR1_OFF = Toast.makeText(contextBR1_OFF, text_OFF, duration_OFF);
                            toastBR1_OFF.show();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.d(TAG, "mBroadcastReceiver1:STATE_TURNING_OFF");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d(TAG, "mBroadcastReceiver1:STATE_ON");
                            //Toast Message to User of BR1 State
                            Context contextBR1_ON = getContext().getApplicationContext();
                            String text_ON = "Bluetooth ON";
                            int duration_ON = Toast.LENGTH_SHORT;
                            Toast toastBR1_ON = Toast.makeText(contextBR1_ON, text_ON, duration_ON);
                            toastBR1_ON.show();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d(TAG, "mBroadcastReceiver1:STATE_TURNING_ON");
                            break;
                    }
                }
            }
        }; //End of BR1

        //Broadcast Receiver 2
        //Discoverability
        final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                //Getting Action & Detecting Different Scan Modes from BR1
                if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                    //Mode is Represented by an Integer
                    int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                    switch (mode) {
                        //Device in Discoverable Mode
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                            Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                            break;
                        //Device Not in Discoverable Mode
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                            Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled. Able to Receive Connections.");
                            break;
                        case BluetoothAdapter.SCAN_MODE_NONE:
                            Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not Able to Receive Connections.");
                            break;
                        case BluetoothAdapter.STATE_CONNECTING:
                            Log.d(TAG, "mBroadcastReceiver2: Connecting...");
                            break;
                        case BluetoothAdapter.STATE_CONNECTED:
                            Log.d(TAG, "mBroadcastReceiver2: Connected.");
                            break;
                    }
                }
            }
        }; //End of BR2

        //Broadcast Receiver 3
        //Discover Devices & List Devices That Are Not Yet Paired
        BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                Log.d(TAG, "onReceive: ACTION FOUND.");
                if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    mBTDevices.add(device);
                    //Log to Obtain Properties (Name, Address) of the Device
                    Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress() + ".");
                    mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                    //Set Adapter to the List
                    lvNewDevices.setAdapter(mDeviceListAdapter);
                }
            }
        }; //End of BR3

        //Broadcast Receiver 4
        //Pairing Devices
        BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                //Looking for Action Bond State Change
                if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //3 Cases:
                    //Case 1: Device Already Bonded
                    if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {Log.d(TAG, "mBroadcastReceiver4: BOND_BONDED.");}
                    //Case 2: Creating a Bond
                    if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {Log.d(TAG, "mBroadcastReceiver4: BOND_BONDING.");}
                    //Case 3: Bond is Broken
                    if(mDevice.getBondState() == BluetoothDevice.BOND_NONE) {Log.d(TAG, "mBroadcastReceiver4: BOND_NONE.");}
                }
            }
        }; //End of BR4

        //Initialize ListView
        lvNewDevices = (ListView) view.findViewById(R.id.lvNewDevices);
        //Initialize ArrayList
        mBTDevices = new ArrayList<>();
        //Create IntentFilter to Catch When Bond State Changes for BT Pairing Feature
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //Register Another BroadcastReceiver (4) & Pass Filter
        requireActivity().registerReceiver(mBroadcastReceiver4, filter);
        //Set Item Click Listener
        lvNewDevices.setOnItemClickListener(bluetoothFragment.this);
        //Initialize Bluetooth Textview
        TextView bluetooth_textview = (TextView) view.findViewById(R.id.bluetooth_textview);

        //Declare & Initialize BTButton; Bluetooth Method
        ImageButton BTButton = (ImageButton) view.findViewById(R.id.BTButton);
        BTButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                enableDisableBT(mBroadcastReceiver1, mBroadcastReceiver2, mBluetoothAdapter, bluetooth_textview);
            }
        });
        
        //Declare & Initialize btnFindUnpairedDevices; Bluetooth Method
        Button btnFindUnpairedDevices = (Button) view.findViewById(R.id.btnFindUnpairedDevices);
        btnFindUnpairedDevices.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                btnDiscover(mBroadcastReceiver3, mBluetoothAdapter);
            }
        });
        Button btnStartConnection = (Button) view.findViewById(R.id.btnStartConnection);
        //OnClickListeners for Buttons
        btnStartConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startConnection();
                //Send "hi"
                String StartConnection = "Hello Pi";
                byte[] bytesSC = StartConnection.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytesSC);
            }
        });

        return view;
    }

    private void enableDisableBT(BroadcastReceiver mBroadcastReceiver1, BroadcastReceiver mBroadcastReceiver2, BluetoothAdapter mBluetoothAdapter, TextView bluetooth_textview) {
        //Call Bluetooth Enable/Disable Function
        Log.d(TAG, "onClick: enabling/disabling bluetooth called.");
        //Method Here
        //3 Cases:
        //(1) BT Adapter null (device cannot support BT)
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDiableBT: DOes not have BT capabilites.");
        }
        //(2) BT Adapter Not Enabled (Disabled)
        if (!mBluetoothAdapter.isEnabled()) {
            //Log to see the changes in the LOGCAT as Bluetooth does not work on Emulators
            Log.d(TAG, "enableDisableBT: enabling BT.");
            //Use Intent to Enable BT
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
            //Filter that Intercepts Changes in the BT Status and Logs those changes
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            //mBroadcastReceiver1 will catch the BT Adapter "ACTION_STATE_CHANGED" State Changes & Log Those Changes
            requireActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
            //Turn on Discoverability
            Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");
            //Intent
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //Extra to Define Discoverable Duration (seconds) to Other Devices
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            //Intent Filter so BR2 Can Intercept the State Change
            IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            //BR2 Will be Looking for ACTION_SCAN_MODE
            requireActivity().registerReceiver(mBroadcastReceiver2, intentFilter);
        }
        //(3) BT Adapter Enabled
        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: disabling BT.");
            //Disable BT Adapter
            mBluetoothAdapter.disable();
            //Intent Filter to Catch State Change
            //Filter that Intercepts Changes in the BT Status and Logs those changes
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            //mBroadcastReceiver1 will catch the BT Adapter "ACTION_STATE_CHANGED" State Changes & Log Those Changes
            requireActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
            //Turn Off Discoverability (Discoverability is Memory Intensive)
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    private void btnDiscover(BroadcastReceiver mBroadcastReceiver3, BluetoothAdapter mBluetoothAdapter) {
        //Log btnDiscover() Method Called
        Log.d(TAG, "btnDiscover: Looking for Unpaired Devices.");

        //Clear ArrayList for ListView
        mBTDevices.clear();

        //Case 1: If BT is Already in Discovery Mode (if the Device is Already Looking for Devices), End Discovering
        if(mBluetoothAdapter.isDiscovering()) {
            //Cancel Discovery
            mBluetoothAdapter.cancelDiscovery();

            //*Note: If Device is Greater than LOLLIPOP, Require Special Permission Check to Start Discovery
            checkBTPermissions();

            //Start Discovery
            mBluetoothAdapter.startDiscovery();
            //Intent to Catch with BroadcastReceiver (uses ACTION_FOUND on BT Device)
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            //Register Receiver
            requireActivity().registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        //Case 2: If Not Discovering, Start Discovering
        if(!mBluetoothAdapter.isDiscovering()) {
            //Check BT Permissions in Manifest
            checkBTPermissions();
            //Start Discovering
            mBluetoothAdapter.startDiscovery();
            //Intent to Catch with BroadcastReceiver (uses ACTION_FOUND on BT Device)
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            //Register Receiver
            requireActivity().registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    //Method to Check BT Permission for startDiscovery()
    //Method Required for All Devices Running API23+
    //Putting the Proper Permissions in the Manifest is Not Enough -> Android Must Programatically Check Permsisions for BT
    private void checkBTPermissions() {
        //Method will Only Execute on Versions > LOLLIPOP; It is Not Needed Otherwise
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.requireActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.requireActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissionCheck != 0)  {this.requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);}
            //1001 could be any number
            else{Log.d(TAG, "checkBTPermissions: No Need to Check Permissions. SDK Version < LOLLIPOP.");}
        }
    }

    //Method to Pair Bluetooth Devices
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Cancel Discovery (Memory Intensive)
        mBluetoothAdapter.cancelDiscovery();
        //Log Click
        Log.d(TAG, "onItemClick: Device Clicked.");
        //Get Device Name & Address Then Log Information
        String deviceName = mBTDevices.get(i).getName();
        String deviceAdresss = mBTDevices.get(i).getAddress();
        Log.d(TAG, "onItemClick: deviceName = " + deviceName + ".");
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAdresss + ".");
        Log.d(TAG, "INT I: " + i + ".");
        Log.d(TAG, "long l: " + l + ".");
        //Create Bond
        //*Note: Check API Version because "createBond()" Requires API19+
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();
            //Assign Bluetooth Device
            mBTDevice = mBTDevices.get(i);
            //Start Connection Service
            mBluetoothConnection = new BluetoothConnectionService(mmmContext);
        }
    }

    //Create "startConnection()" Method
    //*Note: Connection will Fail & App will Crash if Pairing Has Not Occured First
    //Button Pressed When AcceptThread has Already Been Started & We are Ready to Start a Connection & Try to Initiate the ConnectedThread
    public void startConnection() {
        //Start Bluetooth Connection Method
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }

    //Method to Initiate/ Start Chat Service
    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        //Call "startClient()" Method with BluetoothConnection Object & Pass Device & UUID
        mBluetoothConnection.startClient(device, uuid);
    }
}
