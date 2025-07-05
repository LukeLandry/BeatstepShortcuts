package com.honeyflamemusic.sysex;

public enum CCBehavior {
    TOGGLE(0),
    GATE(1);

    private int value;
    CCBehavior(int i) {
        value = i;
    }
    public int getValue() {
        return value;
    }
    }
