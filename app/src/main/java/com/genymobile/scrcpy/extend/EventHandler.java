package com.genymobile.scrcpy.extend;

import java.util.List;

public interface EventHandler {
    void handelEvents(List<InputEvent>  events , String deviceName);
    void stop();
}
