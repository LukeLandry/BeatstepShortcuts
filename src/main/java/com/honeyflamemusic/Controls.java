package com.honeyflamemusic;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.HardwareSurface;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controls {

    public static final int SHIFT = 50;

    public static final int ADD_INSTRUMENT_TRACK = 8;
    public static final int ADD_AUDIO_TRACK = 9;
    public static final int ADD_EFFECT_TRACK = 10;

    public static final int BIG_KNOB = 40;


    public static final int PAD_NOTE_RANGE_LOW = 16;
    public static final int PAD_COUNT = 8;

    private final ControllerHost host;
    private final List<HardwareButton> shortcutButtons;
    private final HardwareSurface surface;
    private final HardwareButton shiftButton;
    private final HardwareButton addInstrumentTrackButton;
    private final HardwareButton addAudioTrackButton;
    private final HardwareButton addEffectTrackButton;
    private final HardwareButton intoLayerButton;
    private final HardwareButton outOfLayerButton;
    private final HardwareButton previousLayerButton;
    private final HardwareButton nextLayerButton;



    public Controls(ControllerHost host) {

        this.host = host;
        surface = host.createHardwareSurface();
        shiftButton = surface.createHardwareButton("SHIFT");
        shortcutButtons = IntStream.range(0, 8).boxed().map(i->surface.createHardwareButton("SHORTCUT_BUTTON_" + i)).collect(Collectors.toList());

        addInstrumentTrackButton = surface.createHardwareButton("ADD_INSTRUMENT_BUTTON");
        addAudioTrackButton = surface.createHardwareButton("ADD_AUDIO_BUTTON");
        addEffectTrackButton = surface.createHardwareButton("ADD_EFFECT_BUTTON");
        intoLayerButton = surface.createHardwareButton("INTO_LAYER_BUTTON");
        outOfLayerButton = surface.createHardwareButton("OUT_OF_LAYER_BUTTON");
        previousLayerButton = surface.createHardwareButton("PREVIOUS_LAYER_BUTTON");
        nextLayerButton = surface.createHardwareButton("NEXT_LAYER_BUTTON");


    }

    public HardwareButton getShortcutButtons(int i) {
        return shortcutButtons.get(i);
    }

    public HardwareButton getShiftButton() {
        return shiftButton;
    }

    public HardwareButton getAddInstrumentTrackButton() {
        return addInstrumentTrackButton;
    }

    public HardwareButton getAddAudioTrackButton() {
        return addAudioTrackButton;
    }

    public HardwareButton getAddEffectTrackButton() {
        return addEffectTrackButton;
    }

    public HardwareButton getIntoLayerButton() {
        return intoLayerButton;
    }

    public HardwareButton getOutOfLayerButton() {
        return outOfLayerButton;
    }

    public HardwareButton getPreviousLayerButton() {
        return previousLayerButton;
    }

    public HardwareButton getNextLayerButton() {
        return nextLayerButton;
    }



    private String toHex(int i) {
        String value = "00" + Integer.toHexString(i);
        return value.substring(value.length() - 2);
    }

}
