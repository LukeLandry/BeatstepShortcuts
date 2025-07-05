package com.honeyflamemusic.sysex;

public enum LightState {
    OFF(0),
    BLUE(16),
    RED(1),
    PURPLE(17);

    byte value;
    LightState(int i) {
        value = (byte)i;
    }
    public byte getValue() {
        return value;
    }
}

