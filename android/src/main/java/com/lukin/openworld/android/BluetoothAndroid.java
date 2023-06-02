package com.lukin.openworld.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.lukin.openworld.LKGame;
import com.lukin.openworld.utils.Multiplayer;
import com.lukin.openworld.utils.MultiplayerManagerThread;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BluetoothAndroid implements Multiplayer {
    private final BluetoothAdapter mBluetoothAdapter;
    private final Context context;
    private final Activity activity;
    private ServerConnectThread serverConnectThread;
    public LKGame game;
    public static final String TAG = "LKOpenWorld";

    public BluetoothAndroid(Context context, Activity activity, LKGame game) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        this.activity = activity;
        this.game = game;
        if (!mBluetoothAdapter.isEnabled()) {
            if (activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Try to request permissions");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    activity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                } else{
                    activity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, 2);
                }
                enableMultiplayer();
            }
        }
    }

    @Override
    public boolean isStarted() {
        return serverConnectThread.isAlive();
    }

    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public void enableMultiplayer() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 3);
        }
    }

    public Set<MultiplayerManagerThread.Device> getPairedConnections() {
        HashSet<MultiplayerManagerThread.Device> result = new HashSet<>();
        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()){
            result.add(new MultiplayerManagerThread.Device(device.getAddress(), device.getName()));
        }
        return result;
    }

    public boolean connectToServerDevice(MultiplayerManagerThread.Device device) {
        BluetoothDevice bluetoothDevice = null;
        for (BluetoothDevice BLdevice : mBluetoothAdapter.getBondedDevices()){
            if (BLdevice.getAddress().equals(device.address)){
                bluetoothDevice = BLdevice;
            }
        }
        if(bluetoothDevice == null) return false;
        new ClientConnectThread(bluetoothDevice, mBluetoothAdapter, this).start();
        return true;
    }

    public void startListeningForClientConnections() {
        serverConnectThread = new ServerConnectThread(mBluetoothAdapter, this);
        serverConnectThread.start();
    }

    @Override
    public boolean stopListeningForClientConnections() {
        if (serverConnectThread != null){
            serverConnectThread.cancel();
            return true;
        }
        return false;
    }

    private static class ServerConnectThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        private final BluetoothAndroid bluetoothAndroid;
        public ManageThread manageThread;

        public ServerConnectThread(BluetoothAdapter bluetoothAdapter, BluetoothAndroid bluetoothAndroid) {
            this.bluetoothAndroid = bluetoothAndroid;
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(BluetoothAndroid.TAG, UUID.fromString("93e1efe3-c7b6-4edd-863f-6000393762f8"));
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    manageThread = new ManageThread(socket, bluetoothAndroid);
                    manageThread.start();
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private static class ClientConnectThread extends Thread {
        private final BluetoothSocket mBluetoothSocket;
        private final BluetoothAdapter bluetoothAdapter;
        private final BluetoothAndroid bluetoothAndroid;
        public ManageThread manageThread;


        public ClientConnectThread(BluetoothDevice serverDevice, BluetoothAdapter bluetoothAdapter, BluetoothAndroid bluetoothAndroid){
            this.bluetoothAdapter = bluetoothAdapter;
            this.bluetoothAndroid = bluetoothAndroid;
            try {
                mBluetoothSocket = serverDevice.createRfcommSocketToServiceRecord(UUID.fromString("93e1efe3-c7b6-4edd-863f-6000393762f8"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mBluetoothSocket.connect();
            } catch (Exception connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mBluetoothSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageThread = new ManageThread(mBluetoothSocket, bluetoothAndroid);
            manageThread.start();
        }

        public void cancel() {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
}
