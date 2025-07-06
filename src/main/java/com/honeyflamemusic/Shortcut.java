package com.honeyflamemusic;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.*;

import java.util.UUID;

public class Shortcut {

    public static final String BITWIG_DEVICE = "BITWIG";
    public static final String VST3_DEVICE = "VST3";
    public static final String VST2_DEVICE = "VST2";
    public static final String CLAP_DEVICE = "CLAP";
    public static final String FILE = "FILE";
    public static final String NULL_DEVICE = "NULL";

    private String deviceId;
    private String deviceType;
    private int midiChannel;
    private int noteNumber;
    private CursorTrack cursorTrack;
    private ControllerHost host;

    public Shortcut(ControllerHost host, SettableStringValue deviceId, int midiChannel, int noteNumber) {
        this.host = host;
        this.midiChannel = midiChannel;
        this.noteNumber = noteNumber;
        cursorTrack = host.createCursorTrack(0, 0);

        onDeviceChange(deviceId.get());
        deviceId.addValueObserver(this::onDeviceChange);
    }

    private void onDeviceChange(String deviceId) {

        if (deviceId == null || deviceId.trim().length() == 0) {
            deviceType = NULL_DEVICE;
            this.deviceId = "";
        } else if (deviceId.startsWith("VST2:")) {
            this.deviceId = deviceId.substring(5).trim();
            deviceType = VST2_DEVICE;
        } else if (deviceId.startsWith("VST3:")) {
            this.deviceId = deviceId.substring(5).trim();
            deviceType = VST3_DEVICE;
        } else if (deviceId.startsWith("BITWIG:")) {
            this.deviceId = deviceId.substring(7).trim();
            deviceType = BITWIG_DEVICE;
        } else if (deviceId.startsWith("CLAP:")) {
            this.deviceId = deviceId.substring(5).trim();
            deviceType = CLAP_DEVICE;
        } else {
            // remove unescaped single quotes
            if (deviceId.startsWith("'")) {
                deviceId = deviceId.substring(1);
            }
            if (deviceId.endsWith("'")) {
                deviceId = deviceId.substring(0, deviceId.length() - 1);
            }
            this.deviceId = deviceId;
            this.deviceType = FILE;
        }

    }

    public void onMidiMsg(ShortMidiMessage msg) {
        if (msg.getStatusByte() == 0x90 + midiChannel && msg.getData1() == noteNumber) {
            InsertionPoint ip = cursorTrack.endOfDeviceChainInsertionPoint();
            host.println("Creating new device");
            switch (deviceType) {
                case BITWIG_DEVICE:
                    ip.insertBitwigDevice(UUID.fromString(deviceId));
                    break;
                case VST3_DEVICE:
                    ip.insertVST3Device(deviceId);
                    break;
                case VST2_DEVICE:
                    ip.insertVST2Device(Integer.getInteger(deviceId));
                    break;
                case CLAP_DEVICE:
                    ip.insertCLAPDevice(deviceId);
                    break;
                case FILE:
                    ip.insertFile(deviceId);
                    break;
                default:
            }
        }
    }



}
