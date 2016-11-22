package ru.cardiacare.cardiacare.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/* ??? */

public class BluetoothService {

//	 interface DoSomething {
//		  void getECGData(int[] ecg_buffer);
//		 }
//	 DoSomething myDoSomethingCallBack;

    // Debugging
    private static final String TAG = "BluetoothService";
    private static final boolean D = true;

    // Member fields
//    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private String mBTAddress;
    private boolean isStop = false;

    // Constants that indicate the current connection state
    private static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    private static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    private static final int STATE_CONNECTED = 3;  // now connected to a remote device

//     Constructor. Prepares a new Act_Main session.
//     @param context  The UI Activity Context
//     @param handler  A Handler to send messages back to the UI Activity

    public BluetoothService(Context context, Handler handler) {
//        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

//     Set the current state of the connection
//     @param state  An integer defining the current connection state

    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(BluetoothFindActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    // Return the current connection state.
    public synchronized int getState() {
        return mState;
    }

    // Start the ConnectThread to initiate a connection to a remote device.
    // @param device  The BluetoothDevice to connect
    public synchronized void connect(String BTAddress) {
        mBTAddress = BTAddress;
        Log.i(TAG, "address  " + BTAddress);

        // Get the BLuetoothDevice object
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(BTAddress);

        if (D) Log.i(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING)
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.run();
        setState(STATE_CONNECTING);
        isStop = false;
//        connected(mConnectThread.mmSocket,device);
    }

    // Start the ConnectedThread to begin managing a Bluetooth connection
    // @param socket  The BluetoothSocket on which the connection was made
    // @param device  The BluetoothDevice that has been connected
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(BluetoothFindActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothFindActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    // Stop all threads
    public synchronized void stop() {
        isStop = true;

        if (D)
            Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            Thread moribund = mConnectThread;
            mConnectThread = null;
            moribund.interrupt();
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            Thread moribund = mConnectedThread;
            mConnectedThread = null;
            moribund.interrupt();
        }

        setState(STATE_NONE);
    }

//     Write to the ConnectedThread in an unsynchronized manner
//     @param out The bytes to write
//     @see ConnectedThread#write(byte[])

//    public void write(byte[] out)
//    {
//        // Create temporary object
//        ConnectedThread r;
//
//        // Synchronize a copy of the ConnectedThread
//        synchronized (this)
//        {
//            Log.d(TAG, "BT_SEND_MESSAGE");
//
//            if (mState != STATE_CONNECTED)
//                return;
//
//            r = mConnectedThread;
//        }
//
//        // Perform the write unsynchronized
//        r.write(out);
//    }

    // Indicate that the connection attempt failed and notify the UI Activity.

    private void connectionFailed() {
        try {
            synchronized (this) {
                this.wait(3000);
            }
            connect(mBTAddress);
        } catch (InterruptedException ex) {
            Log.e(TAG, "WAIT_EXCEPTION:" + ex.getMessage());
        }
    }

    // Indicate that the connection was lost and notify the UI Activity.

    private void connectionLost() {
        if (!isStop)
            connect(mBTAddress);
    }

//     This thread runs while attempting to make an outgoing connection
//     with a device. It runs straight through; the connection either
//     succeeds or fails.

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (Exception e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
//            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
//            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a successful connection or an exception
//                Log.i(TAG, "in try connect");
                mmSocket.connect();
//                Log.i(TAG, "after connect");
            } catch (IOException e) {
                connectionFailed();

                // Close the socket
                try {
//                    Log.i(TAG, "in try close");
                    mmSocket.close();
                } catch (IOException e2) {
//                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
//               Log.i(TAG, "run()");
            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
//                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

//     This thread runs during a connection with a remote device.
//     It handles all incoming and outgoing transmissions.

    private class ConnectedThread extends Thread {
        final int Data = 1;
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
//        private OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
//                OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
//                    tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
        }

        public void run() {
//            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer;
            int bytes = 0;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    if (BluetoothFindActivity.deviceType == 0) {
                        //Clear buffer
                        buffer = new byte[4096];
                        // Read from the InputStream
                        bytes = mmInStream.read(buffer);

                        int[] int_buf = new int[4096];
                        for (int i = 0; i < buffer.length; i++) {
                            int_buf[i] = buffer[i];
                            //Log.i("TAG", ""+int_buf[i]);
                        }

                        //FIXME
                        if ((int_buf[0] == 0) && (int_buf[1] == -2)) {

                            if (int_buf[6] == -86) {
                                int ecg_length = ((buffer[7] & 0x00FF) << 8) + (buffer[8] & 0x00FF);

                                int[] ecg_buffer = new int[ecg_length - 5];
                                for (int i = 0; i < ecg_length - 5; i++) {
                                    ecg_buffer[i] = buffer[i + 11] & 0xFF;
                                    //Log.i("TAG", ""+ecg_buffer[i]);
                                }

                                //Log.i("TAG", ""+s);
                                mHandler.obtainMessage(Data, bytes, -1, ecg_buffer).sendToTarget();
                            }
                        }
                    }
                    if (BluetoothFindActivity.deviceType == 1) {
                        Log.i("TAG", "Device 2");
                    }

                }
                catch (IOException e) {
                    //String bufferStr = new String(buffer, 0, buffer.length);
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

//         Write to the connected OutStream.
//         @param buffer  The bytes to write

//        public void write(byte[] buffer)
//        {
//            try
//            {
//                mmOutStream.write(buffer);
//            }
//            catch (IOException e)
//            {
//                Log.e(TAG, "Exception during write", e);
//            }
//        }

        public void cancel() {
//            if (mmOutStream != null)
//            {
//                try {mmOutStream.close();} catch (Exception e) { Log.e(TAG, "close() of outputstream failed", e); }
//                mmOutStream = null;
//            }

            if (mmInStream != null) {
                try {
                    mmInStream.close();
                } catch (Exception e) {
//                    Log.e(TAG, "close() of inputstream failed", e);
                }
                mmInStream = null;
            }

            if (mmSocket != null) {
                try {
                    mmSocket.close();
                } catch (Exception e) {
//                    Log.e(TAG, "close() of connect socket failed", e);
                }
                mmSocket = null;
            }
        }
    }
}
