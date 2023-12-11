package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
* 该类完成用户登录验证和用户注册等功能.
* */
public class UserClientService {
    //因为我们可能在其他地方用使用user信息, 因此作出成员属性
    private User u = new User();
    //因为Socket在其它地方也可能使用，因此作出属性
    private Socket socket;

    //根据userId 和 pwd 到服务器验证该用户是否合法
    public boolean CheckUser(String userId, String passwd){
        boolean flag = false;
        u.setPasswd(passwd);
        u.setUserId(userId);

        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message)ois.readObject();

            if(ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){  // 登录成功
                // 创建一个和服务器端保持通讯的线程 -> ClientConnectServerThread 类
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                clientConnectServerThread.start();
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                flag = true;
            }else{      // 登录失败，不能启动和服务器通讯的线程
                socket.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    // 向服务器端请求在线用户列表
    public void onlineFriendList(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.
                    getClientConnectServerThreadByID(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 退出客户端，并给服务器发送消息
    public void LogOut(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());   // 必须指明谁退出
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.
                    getClientConnectServerThreadByID(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId() + "退出系统...");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
