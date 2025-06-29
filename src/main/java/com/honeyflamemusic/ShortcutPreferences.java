package com.honeyflamemusic;

import com.bitwig.extension.controller.api.*;
import com.google.gson.Gson;

import java.io.Serializable;
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
                pageValues.add(getSetting(page, sc));
            }
            shortcutNames.add(pageValues);
        }

    }

    private SettableStringValue getSetting(int page, int item) {
        return settings.getStringSetting("Shortcut Page " + page + " Item " + item, "Shortcut Page " + page, 256, "");
    }

    public List<String> getShortcutNamesForPage(int pageId) {
        return shortcutNames.get(pageId).stream().map(SettableStringValue::get).collect(Collectors.toList());
    }

    public void storeDevicePreferences(int pageId, int shortcutId, String shortcutValue) {
        shortcutNames.get(pageId).get(shortcutId).set(shortcutValue);
    }

    public String exportShortcutPreferencesAsJson() {

        ShortcutPages pages = new ShortcutPages();
        for (List<SettableStringValue> pageValues : shortcutNames) {
            ShortcutPage page = new ShortcutPage();
            pages.addPage(page);
            for (SettableStringValue itemValues : pageValues) {
                page.addItem(itemValues.get());
            }
        }

        Gson gson = new Gson();
        String json = gson.toJson(pages);
        return json;
    }

}

class ShortcutPage implements Serializable {
    private List<String> item = new ArrayList<>();

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

    public void addItem(String item) {
        if (this.item == null) {
            this.item = new ArrayList<>();
        }
        this.item.add(item);
    }
}

class ShortcutPages implements Serializable {
    private List<ShortcutPage> page = new ArrayList<>();

    public List<ShortcutPage> getPage() {
        return page;
    }

    public void setPage(List<ShortcutPage> page) {
        this.page = page;
    }

    public void addPage(ShortcutPage page) {
        if (this.page == null) {
            this.page = new ArrayList<>();
        }
        this.page.add(page);
    }
}