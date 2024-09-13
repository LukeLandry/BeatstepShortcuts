package com.honeyflamemusic;

import com.bitwig.extension.controller.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShortcutPreferences {

    private final Preferences settings;
    private final List<List<SettableStringValue>> shortcutNames = new ArrayList<>();

    public ShortcutPreferences(ControllerHost host) {

        settings = host.getPreferences();

        initialize();
    }

    public void initialize() {

        for (int page = 0; page < 16; page++) {
            List<SettableStringValue> pageValues = new ArrayList<>();
            for (int sc = 0; sc < 16; sc++) {
                pageValues.add(settings.getStringSetting("Shortcut Page " + page + " Item " + sc, "Shortcut Page " + page, 256, ""));
            }
            shortcutNames.add(pageValues);
        }

    }

    public List<String> getShortcutNamesForPage(int pageId) {
        return shortcutNames.get(pageId).stream().map(SettableStringValue::get).collect(Collectors.toList());
    }


}
