package com.honeyflamemusic.sysex;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiOut;
import com.honeyflamemusic.Controls;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SysexMessages {

    private ControllerHost host;
    private MidiOut midiOut;

    public SysexMessages(ControllerHost host) {
        this.host = host;
        midiOut = host.getMidiOutPort(0);
    }

    private static final String SYSEX_HEADER = "F0 00 20 6B 7F 42 02 00";

    public static final String SYSEX_END = "F7";

    private static final byte PAD_NUMBER_OFFSET = 0x70;
    private static final byte PLAY_BUTTON = 0x58;
    private static final byte STOP_BUTTON = 0x59;
    private static final byte MODE_BUTTON = 0x5a;
    private static final byte SYNC_BUTTON = 0x5b;
    private static final byte RECALL_BUTTON = 0x5c;
    private static final byte STORE_BUTTON = 0x5d;
    private static final byte SHIFT_BUTTON = 0x5e;
    private static final byte CHANNEL_BUTTON = 0x5f;

    public void enableShiftButton() {
        List<String> data = new ArrayList<>();
        data.add(toHexString(MessageType.MODE.getValue()));
        data.add(toHexString(SHIFT_BUTTON));
        data.add(toHexString(ButtonMode.NOTE.getValue()));
        sendSysexData(data);

        data.clear();
        data.add(toHexString(MessageType.CC_OR_NOTE.getValue()));
        data.add(toHexString(SHIFT_BUTTON));
        data.add(toHexString((byte)Controls.SHIFT));
        host.scheduleTask(()->sendSysexData(data), 500);
    }

    public void updatePadLight(int padNumber, LightState lightState) {
        List<String> data = new ArrayList<>();
        data.add(toHexString(MessageType.LED.getValue()));
        data.add(toHexString((byte)(PAD_NUMBER_OFFSET + padNumber)));
        data.add(toHexString(lightState.getValue()));
        sendSysexData(data);
    }


    private void sendSysexData(List<String> data) {
        List<String> parts = new ArrayList<>();
        parts.add(SYSEX_HEADER);
        parts.addAll(data);
        parts.add(SYSEX_END);
        String midiSysex = parts.stream().collect(Collectors.joining(" "));
        host.println("Sending sysex: " + midiSysex);
        midiOut.sendSysex(midiSysex);
    }


    private String toHexString(byte i) {
        String hexString = "00" + Integer.toHexString(i);
        return hexString.substring(hexString.length() - 2);
    }





}
