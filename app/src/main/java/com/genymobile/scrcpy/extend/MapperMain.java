package com.genymobile.scrcpy.extend;

import static android.os.SystemClock.sleep;

import android.os.IBinder;

import com.genymobile.scrcpy.Device;
import com.genymobile.scrcpy.Ln;
import com.genymobile.scrcpy.extend.InjectTouch.ThreadSafeTouchController;
import com.genymobile.scrcpy.extend.InjectTouch.TouchController;
import com.genymobile.scrcpy.wrappers.SurfaceControl;

import java.io.File;

public class MapperMain {

    public static void TouchControllerInterface(){
        //仅作为触屏控制接口
        //接收命令 并操作触屏

    }

    public static void offScreen(){
        IBinder d = SurfaceControl.getBuiltInDisplay();
        if (d == null) {
            Ln.e("Could not get built-in display");

        }
        SurfaceControl.setDisplayPowerMode(d, Device.POWER_MODE_OFF);
    }


    public static void EventUdpListenerInterface(String ...args){
        //有一说一 有点慢
        //这个inputManager在1KHZ的情况下久感觉出来延迟了
        //而直接写eventX的方式有42KHZ

        ThreadSafeTouchController touchController = new ThreadSafeTouchController(0);//0号代表手机屏幕 可指定显示器
        TouchMapper touchMapper = new TouchMapper(touchController);
        DeviceReader deviceReader = new DeviceReader("UDP",touchMapper);
        deviceReader.start();
//        listenForStop(deviceReader);
//        offScreen();
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