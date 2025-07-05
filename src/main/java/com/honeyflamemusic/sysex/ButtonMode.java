package com.honeyflamemusic.sysex;

public enum ButtonMode {
    OFF(0),
    SILENT_CC(1),
    MMC(7),
    CC(8),
    NOTE(9),
    PROGRAM_CHANGE(11);

    private byte value;
    ButtonMode(int i) {
        value = (byte)i;
    }
    public byte getValue() {
        return value;
    }
}