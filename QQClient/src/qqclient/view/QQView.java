package qqclient.view;

import qqclient.service.FileClientService;
import qqclient.service.MessageClinetService;
import qqclient.service.UserClientService;
import qqclient.utils.Utility;

public class QQView {
    private boolean loop = true;
    private String key = "";    // 处理用户的键盘输入
    private UserClientService userClientService = new UserClientService();      // 用于登录服务器等
    private MessageClinetService messageClinetService = new MessageClinetService();     // 用于发送消息等
    private FileClientService fileClientService = new FileClientService(); // 用于传输文件
    private void mainMenu(){
        while(loop){
            System.out.println("=============欢迎登陆系统===============");
            System.out.println("\t\t 1. 登录系统");
            System.out.println("\t\t 9. 退出系统");
            System.out.print("请输入你的选择: ");
            key = Utility.readString(1);

            switch (key) {
                case "1":
                    System.out.print("请输入用户号: ");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密  码: ");
                    String pwd = Utility.readString(50);
                    if (userClientService.CheckUser(userId, pwd)) {     //还没有写完, 先把整个逻辑打通....
                        System.out.println("===========欢迎 (用户 " + userId + " 登录成功) ===========");
                        //进入到二级菜单
                        while (loop) {
                            System.out.println("\n=========网络通信系统二级菜单(用户 " + userId + " )=======");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("请输入你的选择: ");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.println("请输入想对大家(在线)说的话...");
                                    String s = Utility.readString(100);
                                    messageClinetService.sendToAll(s, userId);
                                    break;
                                case "3":
                                    System.out.println("请输入想聊天的用户...");
                                    String getterID = Utility.readString(10);
                                    System.out.println("请输入想说的话...");
                                    String content = Utility.readString(100);
                                    messageClinetService.sendToOne(content, userId, getterID);
                                    break;
                                case "4":
                                    System.out.println("请输入文件接受方用户:");
                                    String getterID2 = Utility.readString(10);
                                    System.out.println("请输入发送文件的路径:(d:\\xx\\a.jpg) ");
                                    String src = Utility.readString(100);
                                    System.out.println("请输入发送文件到对方的路径:(d:\\xx\\a.jpg) ");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileToOne(dest, src, userId, getterID2);
                                    break;
                                case "9":
                                    userClientService.LogOut();
                                    loop = false;
                                    break;
                            }
                        }
                    }else{ //登录服务器失败
                        System.out.println("=========登录失败=========");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
        System.out.println("系统已退出...");
    }


    public static void main(String[] args) {
        new QQView().mainMenu();
    }
}
