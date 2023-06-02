package com.lukin.openworld.android;

import static com.lukin.openworld.android.BluetoothAndroid.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import com.lukin.openworld.utils.MultiplayerManagerThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
@SuppressLint("MissingPermission")
public class ManageThread extends Thread{
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final MultiplayerManagerThread bluetoothManagerThread;
    private final MultiplayerManagerThread.Device device;
    private byte[] mmBuffer;

    public ManageThread(BluetoothSocket socket, BluetoothAndroid bluetoothAndroid) {
        mmSocket = socket;
        bluetoothManagerThread = bluetoothAndroid.game.getMultiplayerManagerThread();
        BluetoothDevice bluetoothDevice = socket.getRemoteDevice();
        this.device = new MultiplayerManagerThread.Device(bluetoothDevice.getAddress(), bluetoothDevice.getName());
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        bluetoothManagerThread.addDevice(device, mmOutStream);
    }

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                bluetoothManagerThread.read(device, mmBuffer, numBytes);
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}