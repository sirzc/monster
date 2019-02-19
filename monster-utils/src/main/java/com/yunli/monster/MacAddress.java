package com.yunli.monster;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 获取当前机器的mac地址
 *
 * @author zhouchao
 * @date 2019-02-19 9:17
 */
public class MacAddress {

    public static String hexByte(byte b) {
        String s = "000000" + Integer.toHexString(b);
        return s.substring(s.length() - 2);
    }

    public static String hexBytes(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            s = '0' + s;
        }
        return s;
    }

    public static String getMac() {
        Enumeration<NetworkInterface> el;
        String mac = "";
        try {
            el = NetworkInterface.getNetworkInterfaces();
            while (el.hasMoreElements()) {
                byte[] macs = el.nextElement().getHardwareAddress();
                if (macs == null) {
                    continue;
                }
                mac = hexByte(macs[0]) + "-" + hexByte(macs[1]) + "-" + hexByte(macs[2]) + "-" + hexByte(macs[3]) + "-"
                        + hexByte(macs[4]) + "-" + hexByte(macs[5]);
                System.out.println("MAC地址：" + mac);
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return mac;
    }

    public static void main(String[] args) {
        MacAddress.getMac();
        try {
            InetAddress address = InetAddress.getLocalHost();
            System.err.println(address.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
