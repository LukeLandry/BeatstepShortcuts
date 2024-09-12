package com.honeyflamemusic;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.PopupBrowser;

public class Model {

    private static Model instance = null;

    private ControllerHost host;
    private CursorTrack cursorTrack;
    private CursorDevice cursorDevice;
    private PopupBrowser popupBrowser;
    private boolean shifted = false;

    public static Model getInstance(ControllerHost host) {
        if (instance != null && host == instance.host) {
            return instance;
        } else {
            initializeModel(host);
            return instance;
        }
    }

    private static void initializeModel(ControllerHost host) {
        host.println("Model initialized?");
        instance = new Model(host);
    }

    public Model(ControllerHost host) {
        this.host = host;
        cursorTrack = host.createCursorTrack(0, 0);
        cursorDevice = cursorTrack.createCursorDevice();
        popupBrowser = host.createPopupBrowser();

    }

    public ControllerHost getHost() {
        return host;
    }

    public CursorTrack getCursorTrack() {
        return cursorTrack;
    }

    public CursorDevice getCursorDevice() {
        return cursorDevice;
    }

    public PopupBrowser getPopupBrowser() { return popupBrowser; }

    public boolean isShifted() {
        return shifted;
    }

    public void setShifted(boolean shifted) {
        this.shifted = shifted;
    }
}
