package com.genymobile.scrcpy.extend.InjectTouch;

public interface TouchControllerInterface {
    int requireTouch(int x, int y);

    int releaseTouch(int id);

    int touchMove(int id, int x, int y);

    void stop();
}
