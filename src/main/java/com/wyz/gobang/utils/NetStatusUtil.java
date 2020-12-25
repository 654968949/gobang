package com.wyz.gobang.utils;

import com.wyz.gobang.message.HeartMessage;

import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * <p>
 *     网络状态工具类
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/16
 */
public class NetStatusUtil {

    /**
     * 发送心跳检测是否在线
     * @param isReady 我是否准备好的参数
     * @Return 连接是否成功
     */
    public static boolean monitorSocket(boolean isReady) {
        Socket socket = null;
        try {
            socket = new Socket(Global.getOppoIp(), Integer.parseInt(Global.getOppoPort()));
        } catch (Exception e) {
//            System.out.println("对手未上线！");
            return false;
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //发心跳和准备状态
            oos.writeObject(new HeartMessage(isReady,Global.myAccount));
            return true;
        } catch (Exception e) {
            System.out.println("心跳发送失败");
            return false;
        }
    }

}
