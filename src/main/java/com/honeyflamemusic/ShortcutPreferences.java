package com.honeyflamemusic;

import com.bitwig.extension.controller.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShortcutPreferences {

    private final Preferences settings;
    private final List<SettableStringValue> shortcutPageNames = new ArrayList<>();
    private final List<List<SettableStringValue>> shortcutIds = new ArrayList<>();

    private List<Runnable> observers = new ArrayList<>();

    public ShortcutPreferences(ControllerHost host) {

        settings = host.getPreferences();

        initialize();
    }

    public void initialize() {

        for (int page = 0; page < 8; page++) {
            shortcutPageNames.add(settings.getStringSetting("Shortcut Page " + page + " Name", "Shortcut Page " + page, 256, ""));
            List<SettableStringValue> pageValues = new ArrayList<>();
            for (int sc = 0; sc < 8; sc++) {
                pageValues.add(settings.getStringSetting("Shortcut Page " + page + " Item " + sc, "Shortcut Page " + page, 256, ""));
            }
            shortcutIds.add(pageValues);
        }

        shortcutPageNames.stream().forEach(a->a.addValueObserver(this::onChange));
        shortcutIds.stream().forEach(a->a.stream().forEach(b->b.addValueObserver(this::onChange)));

    }

    public List<SettableStringValue> getShortcutIds(int pageId) {
        return shortcutIds.get(pageId);
    }

    public SettableStringValue getShortcutPageName(int pageId) {
        return shortcutPageNames.get(pageId);
    }

    public void addObserver(Runnable callback) {
        observers.add(callback);
    }


    private void onChange(String value) {

        observers.stream().forEach(Runnable::run);
    }


}
