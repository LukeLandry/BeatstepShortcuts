package com.honeyflamemusic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ShortcutPage {

    private List<Shortcut> shortcutList = new ArrayList<>();

    public ShortcutPage(Collection<Shortcut> in, int pageNoteNumber) {
        shortcutList.addAll(in);
    }

    public List<Shortcut> get() {
        return shortcutList;
    }

    public Shortcut getItem(int i) {
        return shortcutList.get(i);
    }

}
