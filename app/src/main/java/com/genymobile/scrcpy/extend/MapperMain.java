package com.genymobile.scrcpy.extend;

import static android.os.SystemClock.sleep;

import android.os.IBinder;

import com.genymobile.scrcpy.Device;
import com.genymobile.scrcpy.InvalidDisplayIdException;
import com.genymobile.scrcpy.Ln;
import com.genymobile.scrcpy.extend.InjectTouch.ThreadSafeTouchController;
import com.genymobile.scrcpy.extend.InjectTouch.TouchController;
import com.genymobile.scrcpy.wrappers.SurfaceControl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MapperMain {
    static int TouchActionRequire  = 0x00;
    static int TouchActionRelease  = 0x01;
    static int TouchActionMove     = 0x02;
    static int ControlOffscreen = 0x10;
    static int ControlOnscreen = 0x11;

    public static void TouchControllerInterface(String... args){
        //仅作为触屏控制接口
        //接收命令 并操作触屏
        int displayId;
        if (args.length == 0) {
            displayId = 0;
        }else if (args.length == 1) {
               try{
                   displayId = Integer.parseInt(args[0]);
               }catch (NumberFormatException e){
                   System.out.println("usage: [displayId] ;set displayId (Optional),default 0");
                   return;
               }
        }else{
            System.out.println("usage: [displayId] ;set displayId (Optional),default 0");
            return;
        }
        TouchController touchController;
        try{
            touchController = new TouchController(displayId);
            System.out.println("inputManager is now running \nusing displayId:"+displayId);
        }catch (InvalidDisplayIdException e){
            System.out.println("InvalidDisplayID : "+e.getDisplayId()+"\nAvailableDisplayIds : " + Arrays.toString(e.getAvailableDisplayIds()) );
            return;
        }
        try {
            DatagramSocket socket = new DatagramSocket(61068);
            byte[] buf = new byte[10];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            System.out.println("listening for udp control packet on port 61068");
            while (true) {
                socket.receive(packet);
                byte [] data = packet.getData();
                int action = data[0];
                int id = data[1];
                int x = ByteBuffer.wrap( new byte[]{ data[5] , data[4] , data[3] , data[2]  }).getInt();
                int y = ByteBuffer.wrap( new byte[]{ data[9] , data[8] , data[7] , data[6]  }).getInt();
                if(action == TouchActionRequire){
                    touchController.requireTouch(x,y);
                }else if (action == TouchActionRelease){
                    touchController.releaseTouch(id);
                }else if (action == TouchActionMove){
                    touchController.touchMove(id,x,y);
                }else if (action == ControlOffscreen){
                    setDisplayPowerMode(Device.POWER_MODE_OFF);
                }else if (action == ControlOnscreen){
                    setDisplayPowerMode(Device.POWER_MODE_NORMAL);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setDisplayPowerMode(int mode){
        IBinder d = SurfaceControl.getBuiltInDisplay();
        if (d == null) {
            Ln.e("Could not get built-in display");
        }
        SurfaceControl.setDisplayPowerMode(d, mode);
    }
}