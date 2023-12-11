package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.*;

/**
 * 实现文件传输
 */

public class FileClientService {
    public void sendFileToOne(String dest, String src, String senderId, String receiverId){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setReceiver(receiverId);
        message.setSendTime(new java.util.Date().toString());
        message.setSrc(src);
        message.setDest(dest);

        // 需要将文件读取
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int)new File(src).length()];
        boolean flag = true;
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);    // 将src文件读入到字节数组中
            message.setFileBytes(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!flag){
            System.out.println("文件不存在，取消本次发送!");
            return ;
        }
        System.out.println(senderId + " 发送文件到 " + receiverId);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.
                    getClientConnectServerThreadByID(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
