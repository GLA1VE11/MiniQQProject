package qqserver.service;

import qqcommon.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();
    // 离线消息存储
    private static ConcurrentHashMap<String, ArrayList<Message>> offlineMessageDb = new ConcurrentHashMap<>();

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    // 存储离线消息
    public static void addOfflineMessage(String userId, Message message) {
        if (offlineMessageDb.containsKey(userId)) {
            ArrayList<Message> list = offlineMessageDb.get(userId);
            list.add(message);
            offlineMessageDb.put(userId, list);
        } else {
            ArrayList<Message> list = new ArrayList<>();
            list.add(message);
            offlineMessageDb.put(userId, list);
        }
    }

    // 接收离线消息
    public static ArrayList<Message> getOfflineMessage(String userId) {
        ArrayList<Message> list = offlineMessageDb.get(userId);
        offlineMessageDb.remove(userId);//读取过一次就删除
        return list;
    }

    public static boolean hasOfflineMessage(String userId){
        return offlineMessageDb.containsKey(userId);
    }

    //添加线程对象到 hm 集合
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    //移除某个对象
    public static void removeServerConnectClientThreadById(String userId) {
        hm.remove(userId);
    }

    //根据userId 返回ServerConnectClientThread线程
    public static ServerConnectClientThread getServerConnectClientThreadById(String userId) {
        return hm.get(userId);
    }

    //判断有无userId 登录
    public static boolean containsServerConnectClientThreadById(String userId) {
        return hm.containsKey(userId);
    }

    // 获取在线用户列表
    public static String getOnlineUser(){
        Iterator<String> iterator = hm.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        while(iterator.hasNext()){
            sb.append(iterator.next().toString() + " ");
        }
        return sb.toString();
    }
}
