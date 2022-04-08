//package com.genymobile.scrcpy.extend;
//
//import com.genymobile.scrcpy.extend.InjectTouch.TouchControllerInterface;
//import com.genymobile.scrcpy.extend.consts.EventCodes;
//
//import java.util.List;
//import java.util.concurrent.ArrayBlockingQueue;
//
//public class TouchMapper implements EventHandler {
//    private TouchControllerInterface touchController;
//
//    private int viedID = -1;
//    private int view_x = 500;
//    private int view_y = 500;
//
//
//    private ArrayBlockingQueue<List<InputEvent>> eventQueue = new ArrayBlockingQueue<>(100);
//
//
//    public TouchMapper(TouchControllerInterface controller) {
//        this.touchController = controller;
//    }
//
//    @Override
//    public void handelEvents(List<InputEvent> events, String deviceName) {
//        eventQueue.offer(events);
//    }
//
//    public void foreverRun(){
//        while (true){
//            try {
//                List<InputEvent> events = eventQueue.take();
//                int x = 0;
//                int y = 0;
//                for (InputEvent event : events) {
//                    if (event.type == EventCodes.EV_KEY && event.code == EventCodes.BTN_MOUSE) {
//                        if (event.value == EventCodes.DOWN) {
//                            viedID = touchController.requireTouch(500, 500);
//                            System.out.println("down" + viedID);
//                            view_x = 500;
//                            view_y = 500;
//                        } else if (event.value == EventCodes.UP) {
//                            System.out.println("up");
//                            viedID = touchController.releaseTouch(viedID);
//                        }
//                    } else if (event.type == EventCodes.EV_REL) {
//                        if (viedID != -1) {
//                            if (event.code == EventCodes.REL_X) {
//                                x = event.value;
//                            } else if (event.code == EventCodes.REL_Y) {
//                                y = event.value;
//                            }
//                        }
//                    }
//                }
//                if (x != 0 || y != 0) {
//                    view_x += x;
//                    view_y += y;
//                    touchController.touchMove(viedID, view_x, view_y);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}



package com.genymobile.scrcpy.extend;

import static android.os.SystemClock.sleep;

import com.genymobile.scrcpy.extend.InjectTouch.TouchControllerInterface;
import com.genymobile.scrcpy.extend.consts.EventCodes;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class TouchMapper implements EventHandler {
    private TouchControllerInterface touchController;

    private int viedID = -1;
    private int view_x = 500;
    private int view_y = 500;
    private boolean running = true;

    private ArrayBlockingQueue<List<InputEvent>> eventQueue = new ArrayBlockingQueue<>(100);


    public TouchMapper(TouchControllerInterface controller) {
        this.touchController = controller;
        new Thread(() -> wheelThread()).start();
        new Thread(() -> joystickRSMoveViewThread()).start();
    }

    public void wheelThread(){//WASD 轮盘控制线程
        while (running){

        }
    }
    public void joystickRSMoveViewThread(){//右摇杆控制视角线程
        while (running){

        }
    }

    @Override
    public void handelEvents(List<InputEvent> events, String deviceName) {
        long start = System.currentTimeMillis();
        int x = 0;
        int y = 0;
        for (InputEvent event : events) {
            if (event.type == EventCodes.EV_KEY && event.code == EventCodes.BTN_MOUSE) {
                if (event.value == EventCodes.DOWN) {
                    viedID = touchController.requireTouch(500, 500);
                    System.out.println("down" + viedID);
                    view_x = 500;
                    view_y = 500;
                } else if (event.value == EventCodes.UP) {
                    System.out.println("up");
                    viedID = touchController.releaseTouch(viedID);
                }
            } else if (event.type == EventCodes.EV_REL) {
                if (viedID != -1) {
                    if (event.code == EventCodes.REL_X) {
                        x = event.value;
                    } else if (event.code == EventCodes.REL_Y) {
                        y = event.value;
                    }
                }
            }
        }
        if (x != 0 || y != 0) {
            view_x += x;
            view_y += y;
            touchController.touchMove(viedID, view_x, view_y);
        }
        System.out.println("time:" + (System.currentTimeMillis() - start));
    }

    public void stop(){
        touchController.stop();
        running = false;
    }
}
