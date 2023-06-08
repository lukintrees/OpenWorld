package com.lukin.openworld.utils;

import com.badlogic.gdx.Gdx;
import com.lukin.openworld.LKGame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TCPMultiplayer implements Multiplayer{
    private static final int PORT = 12345;
    private LKGame game;
    private ServerConnectThread serverConnectThread;
    

    public TCPMultiplayer() {
        game = LKGame.getInstance();
        if (game == null) {
            throw new IllegalStateException("You need to initialize game instance");
        }
    }

    @Override
    public boolean isStarted() {
        return serverConnectThread != null &&  serverConnectThread.isAlive();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void enableMultiplayer() {
    }

    @Override
    public Set<MultiplayerManagerThread.Device> getPairedConnections() {
        InetAddress address = getLocalAddress();
        if (address != null){
            String subnet = address.toString().substring(1, address.toString().lastIndexOf("."));
            return checkHosts(subnet, address.toString().substring(1));
        }
        return new HashSet<>();
    }

    private Set<MultiplayerManagerThread.Device> checkHosts(String subnet, String localAddress) {
        Set<MultiplayerManagerThread.Device> addresses = new HashSet<>();
        int timeout = 300;
        ExecutorService executor = Executors.newFixedThreadPool(255);
        for (int i = 1; i < 256; i++) {
            final String host = subnet + "." + i;
            if (!host.equals(localAddress)) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Socket socket = new Socket();
                        Gdx.app.log("TCP", "trying to connect on " + new InetSocketAddress(host, PORT));
                        try {
                            socket.connect(new InetSocketAddress(host, PORT), timeout);
                            if (socket.isConnected()) {
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(0x01);
                                outputStream.write("get|name".getBytes());
                                outputStream.write(0x04);
                                byte[] name = new byte[128];
                                int bytesRead = socket.getInputStream().read(name);
                                addresses.add(new MultiplayerManagerThread.Device(host, new String(name, 1, bytesRead - 1)));
                            }
                        } catch (IOException e) {
                            Gdx.app.error("TCP", "Exception: " + e.getLocalizedMessage());
                        }
                    }
                });
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    private static InetAddress getLocalAddress()  {
        try {
            List<NetworkInterface> netInts = Collections.list(NetworkInterface.getNetworkInterfaces());
            if (netInts.size() == 1) {
                return InetAddress.getLocalHost();
            }
            for (NetworkInterface net : netInts) {
                if (!net.isLoopback() && !net.isVirtual() && net.isUp()) {
                    Enumeration<InetAddress> addrEnum = net.getInetAddresses();
                    while (addrEnum.hasMoreElements()) {
                        InetAddress addr = addrEnum.nextElement();
                        if (addr instanceof Inet4Address && !addr.isLoopbackAddress() && !addr.isAnyLocalAddress()
                                && !addr.isLinkLocalAddress() && !addr.isMulticastAddress()
                        ) {
                            return addr;
                        }
                    }
                }
            }
        }catch (Exception ignored){
            return null;
        }
        return null;
    }

    @Override
    public boolean connectToServerDevice(MultiplayerManagerThread.Device device) {
        try{
            Socket socket = new Socket(device.address, PORT);
            new ManageThread(socket, LKGame.getMultiplayerManagerThread()).start();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void startListeningForClientConnections() {
        Gdx.app.log("TCP", "Listening required");
        serverConnectThread = new ServerConnectThread();
        serverConnectThread.start();
    }

    @Override
    public boolean stopListeningForClientConnections() {
        serverConnectThread.cancel();
        return true;
    }

    private static class ServerConnectThread extends Thread {
        private final ServerSocket mmServerSocket;
        public ManageThread manageThread;

        public ServerConnectThread() {
            ServerSocket tmp = null;
            try {
                tmp = new ServerSocket(PORT, 0, getLocalAddress());
                Gdx.app.log("TCP", "Listening on " + tmp.getLocalSocketAddress().toString());
            } catch (IOException e) {
                Gdx.app.error("TCP", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            Socket socket;
            while (true) {
                try {
                    Gdx.app.log("TCP", "Listening...");
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Gdx.app.error("TCP", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    manageThread = new ManageThread(socket, LKGame.getMultiplayerManagerThread());
                    manageThread.start();
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Gdx.app.error("TCP", "Could not close the connect socket", e);
            }
        }
    }
    private static class ManageThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final MultiplayerManagerThread multiplayerManagerThread;
        private final MultiplayerManagerThread.Device device;
        private byte[] mmBuffer;

        public ManageThread(Socket socket, MultiplayerManagerThread multiplayerManagerThread) {
            this.multiplayerManagerThread = multiplayerManagerThread;
            this.device = new MultiplayerManagerThread.Device(socket.getLocalAddress().toString(), null);
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Gdx.app.log("TCP", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Gdx.app.log("TCP", "Error occurred when creating output stream", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            multiplayerManagerThread.addDevice(device, mmOutStream);
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes;
            write("connected|".getBytes());
            ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
            boolean messageStarted = false;
            while (true) {
                try {
                    numBytes = mmInStream.read(mmBuffer);
                    for (int i = 0; i < numBytes; i++) {
                        byte currentByte = mmBuffer[i];
                        if (currentByte == 0x01) {
                            messageStarted = true;
                            messageBuffer.reset();
                        } else if (currentByte == 0x04) {
                            if (messageStarted) {
                                multiplayerManagerThread.read(device, messageBuffer.toByteArray(), messageBuffer.size());
                                messageStarted = false;
                            }
                        } else {
                            if (messageStarted) {
                                messageBuffer.write(currentByte);
                            }
                        }
                    }
                } catch (IOException e) {
                    Gdx.app.debug("TCP", "Input stream was disconnected", e);
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(0x01);
                mmOutStream.write(bytes);
                mmOutStream.write(0x04);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
