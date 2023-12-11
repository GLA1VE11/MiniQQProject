package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread{
    // 该线程必须持有socket
    private Socket socket;

    @Override
    public void run() {
        // 因为Thread需要在后台和服务器通信，因此我们while循环
        while(true){
            try {
                System.out.println("客户端线程，等待从读取从服务器端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 这里很重要！如果这个线程会一直等待从管道中读取数据，如果管道中没有数据，那么这个进程就会一直阻塞在这里!
                Message ms = (Message)ois.readObject();
                // 判断读取的消息类型
                // 在线用户列表
                if(ms.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                    String[] s = ms.getContent().split(" ");
                    System.out.println("\n======当前在线用户列表======");
                    for (String userid : s) {
                        System.out.println("用户: " + userid);
                    }
                }else if(ms.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    System.out.println("\n" + ms.getSender() + " 于 " + ms.getSendTime() + " 对我说: \n"
                            + ms.getContent());
                }else if(ms.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    System.out.println("\n" + ms.getSender() + " 于 " + ms.getSendTime() + " 对大家说: \n"
                            + ms.getContent());
                }else if(ms.getMesType().equals(MessageType.MESSAGE_WRONG)){
                    System.out.println("\n服务器错误信息: " + ms.getContent());
                }else if(ms.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    System.out.println("\n 收到 " + ms.getSender() + " 给我发送的文件: " + ms.getSrc() +
                            "到我的电脑: " + ms.getDest());
                    FileOutputStream fileOutputStream = new FileOutputStream(ms.getDest());
                    fileOutputStream.write(ms.getFileBytes());
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
