package com.eisiadev.yourthreats;

public final class ClientThreatState {
    public static volatile ThreatLevel level = ThreatLevel.DEFAULT;

    private ClientThreatState() {}
}
