package com.genymobile.scrcpy.extend;

import com.genymobile.scrcpy.Ln;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceReader {
    private String path;
    private EventHandler eventHandeller;
    private Boolean running = true;
    public DeviceReader(String path, EventHandler eventHandler){
        this.path = path;
        this.eventHandeller = eventHandler;
    }

    public void initUDPServer(){
        try {
            DatagramSocket socket = new DatagramSocket(9999);
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            System.out.println("Waiting for UDP packet");
            HashMap<Integer,String> index_name = new HashMap();
            while (running) {
                socket.receive(packet);
                byte [] data = packet.getData();
                int dev_index = data[0];
                int package_count = data[1];
                if(dev_index == 0 && package_count == 0) {
                    String devName = new String(data, 2, packet.getLength() - 2);
                    System.out.println("Received devName packet: " + devName);
                    for (String s : devName.split("\\|")) {
                        if(s.indexOf(":") > 0) {
                            int index = Integer.parseInt(s.split(":")[0]);
                            String name = s.split(":")[1];
                            index_name.put(index, name);
                        }
                    }
                }else{
                    String name = index_name.containsKey(dev_index) ? index_name.get(dev_index) : "event" + dev_index;
                    ArrayList<InputEvent> events = new ArrayList<>();
                    for(int i = 0; i < package_count; i++) {
                        int type = ByteBuffer.wrap( new byte[]{ data[i * 8 + 3] , data[i * 8 + 2]  }).getShort();
                        int code = ByteBuffer.wrap( new byte[]{ data[i * 8 + 5] , data[i * 8 + 4]  }).getShort();
                        int value = ByteBuffer.wrap( new byte[]{ data[i * 8 + 9] , data[i * 8 + 8] , data[i * 8 + 7] , data[i * 8 + 6]  }).getInt();
                        events.add(new InputEvent(type, code, value));
                    }
                    eventHandeller.handelEvents(events, name);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
//        initUDPServer();// !important 不能在非主线程调用touchController  why ？
        new Thread(() -> initUDPServer()).start();
    }

    public void stop(){
        running = false;
        eventHandeller.stop();
    }
}
