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

    private String deviceId;
    private String deviceType;
    private int midiChannel;
    private int noteNumber;
    private CursorTrack cursorTrack;
    private ControllerHost host;


    public Shortcut(ControllerHost host, String deviceId, int midiChannel, int noteNumber) {
        init(host, deviceId, midiChannel, noteNumber);
    }


    private void init(ControllerHost host, String deviceId, int midiChannel, int noteNumber) {
        this.host = host;

        if (deviceId.startsWith("VST2:")) {
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
            this.deviceId = deviceId;
            this.deviceType = FILE;
        }

        this.midiChannel = midiChannel;
        this.noteNumber = noteNumber;

        cursorTrack = host.createCursorTrack(0, 0);

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
                    throw new RuntimeException("Unexpected deviceType: " + deviceType);
            }
        }
    }

//    private DeviceMatcher initializeDeviceMatcher() {
//        switch (deviceType) {
//            case BITWIG_DEVICE:
//                return host.createBitwigDeviceMatcher(UUID.fromString(deviceId));
//            case VST3_DEVICE:
//                return host.createVST3DeviceMatcher(deviceId);
//            case VST2_DEVICE:
//                return host.createVST2DeviceMatcher(Integer.getInteger(deviceId));
//            case FILE:
//                return host.createActiveDeviceMatcher();
//            default:
//                throw new RuntimeException("Unexpected deviceType: " + deviceType);
//        }
//    }

    public void flush() {
        // shouldn't need this if observer works correctly
    }


}
