package com.genymobile.scrcpy.extend;

import static android.os.SystemClock.sleep;

import com.genymobile.scrcpy.extend.InjectTouch.ThreadSafeTouchController;
import com.genymobile.scrcpy.extend.InjectTouch.TouchController;

import java.io.File;

public class MapperMain {

    public static void TouchControllerInterface(){
        //仅作为触屏控制接口
        //接收命令 并操作触屏

    }

    public static void EventUdpListenerInterface(){
        //通过UDP接收设备事件并转换再操作触屏
        ThreadSafeTouchController touchController = new ThreadSafeTouchController(0);
        TouchMapper touchMapper = new TouchMapper(touchController);
        DeviceReader deviceReader = new DeviceReader("UDP",touchMapper);
        deviceReader.start();
//        listenForStop(deviceReader);
        touchController.mainLoop();
    }

    public static void FullTakeOverInterface(){
        //全接管

    }

    public static void listenForStop(DeviceReader deviceReader){
        new Thread(() -> {
            while (true){
                String filePath = "/sdcard/scrcpy/stop";
                if(!new File(filePath).exists()){
                    deviceReader.stop();
                }else{
                    sleep(1000);
                }
            }
        }).start();
    }
}