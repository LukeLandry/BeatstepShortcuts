package com.honeyflamemusic;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;
import com.honeyflamemusic.sysex.LightState;
import com.honeyflamemusic.sysex.SysexMessages;

import java.util.*;
import java.util.stream.IntStream;

public class BeatstepShortcutsExtension extends ControllerExtension
{

   private Model model;
   private Controls controls;
   private SysexMessages sysexMessages;
   private KnobsController knobsController;
   private ShortcutPreferences shortcutPreferences;
   private int shortcutPageNumber = -1;

   private final UUID eqPlusId = UUID.fromString("e4815188-ba6f-4d14-bcfc-2dcb8f778ccb");

   private List<ShortcutPage> shortcutPages = new ArrayList<>();

   private CursorTrack mCursorTrack;
   private CursorDevice mCursorDevice;
   private CursorDeviceLayer mCursorDeviceLayer;
   private DeviceBank mCursorDeviceBank;
   private Application mApplication;
   private MidiIn midiIn;
   private MidiOut midiOut;

   protected BeatstepShortcutsExtension(final BeatstepShortcutsExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   @Override
   public void init()
   {
      final ControllerHost host = getHost();
      model = Model.getInstance(host);

      controls = new Controls(host);
      sysexMessages = new SysexMessages(host);

      mTransport = host.createTransport();
      midiIn = host.getMidiInPort(0);
      midiOut = host.getMidiOutPort(0);

      midiIn.setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi0(msg));
      midiIn.setSysexCallback((String data) -> onSysex0(data));

      sysexMessages.enableShiftButton();

      initializeShortcuts();
      initializeKnobs();
      updatePageDisplay(0);

      mApplication = host.createApplication();
      mCursorTrack = host.createCursorTrack(0, 0);
      mCursorDevice = mCursorTrack.createCursorDevice();
      mCursorDeviceLayer = mCursorDevice.createCursorLayer();
      mCursorDeviceLayer.exists().markInterested();
      mCursorDeviceBank = mCursorDeviceLayer.createDeviceBank(1);


   }


   private void initializeShortcuts() {
      shortcutPreferences = new ShortcutPreferences(getHost());

      for (int shortcutPage = 0; shortcutPage < 8; shortcutPage++) {
         List<SettableStringValue> deviceIds = shortcutPreferences.getShortcutIds(shortcutPage);
         SettableStringValue pageName = shortcutPreferences.getShortcutPageName(shortcutPage);
         int ccNumber = 0;
         List<Shortcut> shortcutList = new ArrayList<>();
         for (SettableStringValue deviceId : deviceIds) {
            shortcutList.add(new Shortcut(getHost(), deviceId, 2, ccNumber));
            ccNumber++;
         }
         ShortcutPage sp = new ShortcutPage(shortcutList, pageName);
         shortcutPages.add(sp);
      }

      setShortcutPage(0);
   }


   private void updatePageDisplay(int pageNumber) {
      getHost().println("Setting page to " + pageNumber);
      IntStream.range(0, 8).boxed()
              .forEach(i-> getHost().scheduleTask(()->sysexMessages.updatePadLight(i, i == pageNumber ? LightState.RED : LightState.OFF), 100*i));
   }

   private void setShortcutPage(int pageNumber) {
      getHost().println("pagenumber = " + pageNumber);
      int oldPageNumber = shortcutPageNumber;
      shortcutPageNumber = pageNumber;
      String shortcutPageName = shortcutPages.get(shortcutPageNumber).getPageName();
      if (pageNumber >= 0 && pageNumber < 8 && oldPageNumber != pageNumber) {
         getHost().showPopupNotification("Beatstep Shortcuts Page " + (shortcutPageNumber + 1) + " " + shortcutPageName);
         getHost().scheduleTask(()->sysexMessages.updatePadLight(shortcutPageNumber, LightState.RED), 1000);
         getHost().scheduleTask(()->sysexMessages.updatePadLight(oldPageNumber, LightState.OFF), 1500);
      }

   }

   private void initializeKnobs() {
      knobsController = new KnobsController(getHost());
   }

   @Override
   public void exit()
   {
      // TODO: Perform any cleanup once the driver exits
      // For now just show a popup notification for verification that it is no longer running.
      //getHost().showPopupNotification("BeatstepShortcuts Exited");
   }

   @Override
   public void flush()
   {

   }

   /** Called when we receive short MIDI message on port 0. */
   private void onMidi0(ShortMidiMessage msg) 
   {
      if (msg.isNoteOn() && msg.getData1() == Controls.SHIFT) {
         Model.getInstance(getHost()).setShifted(true);
      } else if (msg.isNoteOff() && msg.getData1() == Controls.SHIFT) {
            Model.getInstance(getHost()).setShifted(false);
      } else if (Model.getInstance(getHost()).isShifted()) {
         if (msg.getStatusByte() == 0x82 && msg.getData1() < 8) {
            setShortcutPage(msg.getData1());
         }
      } else {
         if (msg.isNoteOn() && msg.getData1() != Controls.SHIFT) {
            int note = msg.getData1();
            if (note == Controls.ADD_INSTRUMENT_TRACK) {
               mApplication.createInstrumentTrack(-1);
            } else if (note == Controls.ADD_AUDIO_TRACK) {
               mApplication.createAudioTrack(-1);
            } else if (note == Controls.ADD_EFFECT_TRACK) {
               mApplication.createEffectTrack(-1);
            } else if (note == Controls.WINDOW_STATE) {
               mCursorDevice.isWindowOpen().toggle();
            } else if (note == Controls.ENABLE) {
               mCursorDevice.isEnabled().toggle();
            } else if (note == Controls.DELETE_DEVICE) {
               mCursorDevice.deleteObject();
            } else if (note == Controls.UNDO) {
               mApplication.undo();
            } else if (note == Controls.TAP_TEMPO) {
               mTransport.tapTempo();
            } else if (note < 8) {
               getHost().println("Adding shortcut #" + note);
               shortcutPages.get(shortcutPageNumber).get().forEach(s -> s.onMidiMsg(msg));
            }
         }
      }

   }

   /** Called when we receive sysex MIDI message on port 0. */
   private void onSysex0(final String data) 
   {
      // MMC Transport Controls:
      if (data.equals("f07f7f0605f7"))
            mTransport.rewind();
      else if (data.equals("f07f7f0604f7"))
            mTransport.fastForward();
      else if (data.equals("f07f7f0601f7"))
            mTransport.stop();
      else if (data.equals("f07f7f0602f7"))
            mTransport.play();
      else if (data.equals("f07f7f0606f7"))
            mTransport.record();
   }

   private Transport mTransport;
}
