package com.honeyflamemusic;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;

import java.util.*;

public class BeatstepShortcutsExtension extends ControllerExtension
{

   private Model model;
   private KnobsController knobsController;
   private ShortcutPreferences shortcutPreferences;
   private int shortcutPage = 0;

   private final UUID eqPlusId = UUID.fromString("e4815188-ba6f-4d14-bcfc-2dcb8f778ccb");

   private List<Shortcut> shortcutList = Collections.emptyList();


   protected BeatstepShortcutsExtension(final BeatstepShortcutsExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   @Override
   public void init()
   {
      final ControllerHost host = getHost();
      model = Model.getInstance(host);

      mTransport = host.createTransport();
      host.getMidiInPort(0).setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi0(msg));
      host.getMidiInPort(0).setSysexCallback((String data) -> onSysex0(data));

      host.getMidiOutPort(0).sendSysex("F0 00 20 6B 7F 42 02 00 01 5E 09 F7");

      initializeShortcuts();
      initializeKnobs();

   }

   private void initializeShortcuts() {
      shortcutPreferences = new ShortcutPreferences(getHost());
      setShortcutPage(0);
   }

   private void setShortcutPage(int pageNumber) {
      if (pageNumber >= 0 && pageNumber < 16) {
         shortcutPage = pageNumber;
         List<String> names = shortcutPreferences.getShortcutNamesForPage(shortcutPage);
         int ccNumber = 0;
         shortcutList = new ArrayList<>();
         for (String name : names) {
            if (name != null && !name.isEmpty()) {
               shortcutList.add(new Shortcut(getHost(), name, 2, ccNumber));
            }
            ccNumber++;
         }
         getHost().showPopupNotification("Beatstep Shortcuts Page " + (shortcutPage + 1));
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
      getHost().showPopupNotification("BeatstepShortcuts Exited");
   }

   @Override
   public void flush()
   {
      shortcutList.forEach(Shortcut::flush);

   }

   /** Called when we receive short MIDI message on port 0. */
   private void onMidi0(ShortMidiMessage msg) 
   {
      if (msg.getData1() == Controls.SHIFT) {
         if (msg.getStatusByte() == 0x90) {
            Model.getInstance(getHost()).setShifted(true);
         } else if (msg.getStatusByte() == 0x80) {
            Model.getInstance(getHost()).setShifted(false);
         }
      } else if (Model.getInstance(getHost()).isShifted()) {
         if (msg.getStatusByte() == 0x92) {
            setShortcutPage(msg.getData1());
         }

      } else {
         shortcutList.forEach(s -> s.onMidiMsg(msg));
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
