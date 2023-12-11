package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 提供和消息相关的方法
 * */
public class MessageClinetService {
    public void sendToOne(String content, String sender, String getter){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSender(sender);
        message.setReceiver(getter);
        message.setContent(content);
        message.setSendTime(new java.util.Date().toString());
        System.out.println("我 对 " + getter + " 说 " + content);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.
                    getClientConnectServerThreadByID(sender).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAll(String content, String sender){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
        message.setSender(sender);
        message.setContent(content);
        message.setSendTime(new java.util.Date().toString());
        System.out.println("我 对大家说 :" + content);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.
                    getClientConnectServerThreadByID(sender).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
