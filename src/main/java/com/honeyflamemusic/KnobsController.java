package com.honeyflamemusic;

import com.bitwig.extension.controller.api.*;

public class KnobsController {

    private ControllerHost host;
    private MidiIn midiIn;
    private HardwareSurface surface;
    private CursorRemoteControlsPage projectRemotes;
    private CursorRemoteControlsPage trackRemotes;

    public KnobsController(ControllerHost host) {
        this.host = host;
        midiIn = host.getMidiInPort(0);
        surface = host.createHardwareSurface();
        projectRemotes = host.getProject().getRootTrackGroup().createCursorRemoteControlsPage(8);
        for (int i = 0; i < 8; i++) {
            final RelativeHardwareKnob knob = surface.createRelativeHardwareKnob("CC_PRJ_RMT_" + (i + 1));
            final RelativeHardwareValueMatcher matcher = midiIn.createRelative2sComplementValueMatcher(
                    String.format("(status == 178 && data1 == %d )", (20 + i)), "data2-64", 8, 1);
            knob.setAdjustValueMatcher(matcher);
            final RemoteControl param = projectRemotes.getParameter(i);
            param.addBinding(knob);
            knob.setStepSize(1);
            knob.setSensitivity(0.02);
        }
        trackRemotes = host.createCursorTrack(0, 0).createCursorRemoteControlsPage(8);
        for (int i = 0; i < 8; i++) {
            final RelativeHardwareKnob knob = surface.createRelativeHardwareKnob("CC_TRK_RMT_" + (i + 1));
            final RelativeHardwareValueMatcher matcher = midiIn.createRelative2sComplementValueMatcher(
                    String.format("(status == 178 && data1 == %d )", (30 + i)), "data2-64", 8, 1);
            knob.setAdjustValueMatcher(matcher);
            final RemoteControl param = trackRemotes.getParameter(i);
            param.addBinding(knob);
            knob.setStepSize(1);
            knob.setSensitivity(0.02);
        }

    }
}
