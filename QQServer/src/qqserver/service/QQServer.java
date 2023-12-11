package qqserver.service;

import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class QQServer {
    private ServerSocket ss = null;
    //这里我们也可以使用 ConcurrentHashMap, 可以处理并发的集合，没有线程安全
    //HashMap 没有处理线程安全，因此在多线程情况下是不安全
    //ConcurrentHashMap 处理的线程安全,即线程同步处理, 在多线程情况下是安全
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static { //在静态代码块，初始化 validUsers. 模拟数据库
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("皮卡丘", new User("皮卡丘", "123456"));
        validUsers.put("妙蛙种子", new User("妙蛙种子", "123456"));
        validUsers.put("杰尼龟", new User("杰尼龟", "123456"));
    }

    private boolean checkUser(String userId, String passwd) {
        User user = validUsers.get(userId);
        if(user == null || ManageClientThreads.getHm().containsKey(user.getUserId())) {//说明userId没有存在validUsers 的key中
            return  false;
        }
        if(!user.getPasswd().equals(passwd)) {//userId正确，但是密码错误
            return false;
        }
        return  true;
    }

    public QQServer(){
        try {
            System.out.println("服务器在9999端口监听...");
            ss = new ServerSocket(9999);
            new Thread(new SendNewstoAllService()).start();
            while(true){    //当和某个客户端连接后，会继续监听, 因此while
                Socket socket = ss.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User u = (User)ois.readObject();    //读取客户端发送的User对象
                Message message = new Message();
                if (this.checkUser(u.getUserId(), u.getPasswd())){
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(message);
                    // 创建一条线程和客户端保持通讯, 该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket, u.getUserId());
                    serverConnectClientThread.start();
                    // 该线程对象放入一个集合对象中进行管理
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);
                }else{
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    System.out.println("用户 " + u.getUserId() + ", 密码 " + u.getPasswd() + "验证失败...");
                    oos.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {  //如果服务器退出了while，说明服务器端不在监听，因此关闭ServerSocket
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
