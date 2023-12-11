package qqserver.service;

import qqcommon.Message;
import qqcommon.MessageType;
import qqserver.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigestSpi;
import java.util.HashMap;
import java.util.Iterator;

public class SendNewstoAllService implements Runnable {
    @Override
    public void run() {
        while(true) {
            System.out.println("请输入服务器要推送的新闻[输入exit退出推送服务]...");
            String news = Utility.readString(100);
            if("exit".equals(news)){
                break;
            }
            Message message = new Message();
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setSendTime(new java.util.Date().toString());
            message.setSender("服务器");
            message.setContent(news);
            System.out.println("服务器发送新闻: " + news);
            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while(iterator.hasNext()){
                String onlineUser = iterator.next().toString();
                message.setReceiver(onlineUser);
                try {
                    ObjectOutputStream oos =
                            new ObjectOutputStream(hm.get(onlineUser).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
