package qqserver.service;

import qqcommon.Message;
import qqcommon.MessageType;

import javax.naming.ldap.SortKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类的一个对象和某个客户端保持通信
 */
public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userId;  // 连接到服务端的userID
    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {     //这里线程处于run的状态，可以发送/接收消息
        // 看一下有无离线消息，有则展示出来
        if(ManageClientThreads.hasOfflineMessage(userId)){
            System.out.println("有" + userId + "的离线信息!");
            ArrayList<Message> message_list = ManageClientThreads.getOfflineMessage(userId);
            try {
                // 转发离线消息
                for (Message message_: message_list) {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(message_);
                };
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while(true){
            try {
                System.out.println("服务端和客户端" + userId + " 保持通信，读取数据...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message)ois.readObject();
                // 根据message的类型进行相应的操作
                if(message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                    System.out.println(message.getSender() + "请求在线用户列表...");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    Message message1 = new Message();
                    message1.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message1.setContent(onlineUser);
                    message1.setReceiver(message.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message1);
                }else if(message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)){
                    System.out.println(message.getSender() + "请求退出系统...");
                    ManageClientThreads.removeServerConnectClientThreadById(message.getSender());
                    socket.close(); //关闭连接
                    // 退出run方法
                    break;
                }else if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    if(ManageClientThreads.containsServerConnectClientThreadById(message.getReceiver())){
                        // 根据message获取getter，得到对应线程
                        ServerConnectClientThread receive_thread =
                                ManageClientThreads.getServerConnectClientThreadById(message.getReceiver());
                        // 转发
                        ObjectOutputStream oos = new ObjectOutputStream(receive_thread.socket.getOutputStream());
                        oos.writeObject(message);       // 如果做成离线的，就写入数据库即可。
                    }else{      // 离线，先存到数据库里
                        ManageClientThreads.addOfflineMessage(message.getReceiver(), message);
                    }
                }else if(message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while(iterator.hasNext()){
                        String onlineUser = iterator.next().toString();
                        if(!onlineUser.equals(message.getSender())){
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUser).socket.getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                }else if(message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    if(ManageClientThreads.containsServerConnectClientThreadById(message.getReceiver())){   // 在线
                        ServerConnectClientThread receive_thread =
                                ManageClientThreads.getServerConnectClientThreadById(message.getReceiver());
                        // 转发
                        ObjectOutputStream oos = new ObjectOutputStream(receive_thread.socket.getOutputStream());
                        oos.writeObject(message);
                    }else{  // 离线，不方便接受文件
                        Message wrong_message = new Message();
                        wrong_message.setMesType(MessageType.MESSAGE_WRONG);
                        wrong_message.setContent("文件接收对象不在线! 文件发送失败!");
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(wrong_message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
