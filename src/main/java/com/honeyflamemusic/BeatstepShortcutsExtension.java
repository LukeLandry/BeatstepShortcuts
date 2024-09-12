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


      // TODO: Perform your driver initialization here.
      // For now just show a popup notification for verification that it is running.
      host.showPopupNotification("BeatstepShortcuts Initialized");
   }

   private void initializeShortcuts() {

      shortcutList = Arrays.asList(
              new Shortcut(getHost(), "e4815188-ba6f-4d14-bcfc-2dcb8f778ccb", Shortcut.BITWIG_DEVICE, 0, 16),
              new Shortcut(getHost(), "56535437364353636C612D3736207374", Shortcut.VST3_DEVICE, 0, 17),
              new Shortcut(getHost(), "/Users/luke/Documents/Bitwig Studio/Library/Presets/Kontakt 7/Noire.bwpreset", Shortcut.FILE, 0, 18),
              new Shortcut(getHost(), "/Users/luke/Documents/Bitwig Studio/Library/Presets/Kontakt 7/NI Mark I.bwpreset", Shortcut.FILE, 0, 19),
              new Shortcut(getHost(), "/Users/luke/Documents/Bitwig Studio/Library/Presets/Kontakt 7/Scarbee Rick.bwpreset", Shortcut.FILE, 0, 20),
              new Shortcut(getHost(), "/Users/luke/Documents/Bitwig Studio/Library/Presets/Guitar Rig 6/SW Clean Tweed.bwpreset", Shortcut.FILE, 0, 21),
              new Shortcut(getHost(), "/Users/luke/Documents/Bitwig Studio/Library/Presets/Guitar Rig 6/SW Clean Fender.bwpreset", Shortcut.FILE, 0, 22),
              new Shortcut(getHost(), "/Users/luke/Documents/Bitwig Studio/Library/Presets/Guitar Rig 6/GR Vox.bwpreset", Shortcut.FILE, 0, 23)


      );

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
