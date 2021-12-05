<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.example.navigationdrawer1">

    <!--Bluetooth Permission-->
    <uses-feature android:name="android.hardware.bluetooth" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <!--Permission only grant to system apps - removed: <uses-permission android:name="android.permission.BLUETOOTH_PRIVILEGED" /> -->

    <!--PEFViewer-->
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

    <!--If you do not include (android:hardwareAccelerated="false"), the app will crash on the proper device-->
    <application
        android:allowBackup="true"
        android:icon="@mipmap/cubedneonsquare"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/cubedneonsquare_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.NavigationDrawer1"
        android:hardwareAccelerated="false"
        android:largeHeap="true">
        <activity
            android:name=".MainActivity"
            android:theme="@style/Theme.NavigationDrawer1.NoActionBar"
            android:exported="true"
            android:screenOrientation="sensorLandscape"
            tools:ignore="LockedOrientationActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <meta-data
            android:name="preloaded_fonts"
            android:resource="@array/preloaded_fonts" />
    </application>

</manifest>
