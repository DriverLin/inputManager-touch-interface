package com.genymobile.scrcpy.extend;

public class InputEvent {
    public final int type;
    public final int code;
    public final int value;

    public InputEvent(int type, int code, int value) {
        this.type = type;
        this.code = code;
        this.value = value;
    }

    @Override
    public String toString() {
        return "InputEvent{" +
                "type=" + type +
                ", code=" + code +
                ", value=" + value +
                '}';
    }
}
