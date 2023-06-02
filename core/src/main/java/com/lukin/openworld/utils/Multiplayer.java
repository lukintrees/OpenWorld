package com.lukin.openworld.utils;

import java.util.Set;

public interface Multiplayer {
    boolean isStarted();
    boolean isEnabled();

    void enableMultiplayer();

    Set<MultiplayerManagerThread.Device> getPairedConnections();

    boolean connectToServerDevice(MultiplayerManagerThread.Device device);

    void startListeningForClientConnections();
    boolean stopListeningForClientConnections();
}
