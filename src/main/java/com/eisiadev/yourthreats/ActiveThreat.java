package com.eisiadev.yourthreats;

public final class ActiveThreat {
    private static volatile ThreatLevel level = ThreatLevel.DEFAULT;

    private ActiveThreat() {}

    public static ThreatLevel get() {
        return level;
    }

    static void set(ThreatLevel newLevel) {
        level = newLevel;
    }
}
