package qqclient.service;

import java.util.HashMap;

/**
 * 该类管理客户端连接到服务器端的线程的类
 */

public class ManageClientConnectServerThread {
    // 把多个线程放入一个HashMap集合，key 就是用户id, value 就是线程
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();

    // 将某个线程加入到集合
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread ccst){
        hm.put(userId, ccst);
    }

    //通过userId 可以得到对应线程
    public static ClientConnectServerThread getClientConnectServerThreadByID(String userId) {
        return hm.get(userId);
    }
}
