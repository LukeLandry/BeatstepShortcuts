package com.honeyflamemusic.sysex;

public enum MessageType {
    MODE(1),
    CHANNEL(2),
    CC_OR_NOTE(3),
    OFF_VALUE(4),
    ON_VALUE(5),
    BEHAVIOR(6),
    LED(16);

    private byte value;
    MessageType(int i) {
        this.value = (byte)i;
    }
    public byte getValue() {
        return value;
    }
}