package com.honeyflamemusic;

import com.bitwig.extension.controller.api.SettableStringValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ShortcutPage {

    private List<Shortcut> shortcutList = new ArrayList<>();
    private SettableStringValue pageName;

    public ShortcutPage(Collection<Shortcut> in, SettableStringValue pageName) {
        shortcutList.addAll(in);
        this.pageName = pageName;
    }

    public List<Shortcut> get() {
        return shortcutList;
    }

    public Shortcut getItem(int i) {
        return shortcutList.get(i);
    }

    public String getPageName() {
        return pageName.get();
    }

}
