package cn.bbqiu.middleware.coordinator.peers.ipstrategy;

import cn.bbqiu.middleware.coordinator.peers.PeerInfoStrategy;

import java.net.Inet4Address;
import java.net.SocketException;
import java.util.List;

/**
 * @author: nixgnehc nixgnehc@163.com
 * @date: 19-1-16
 * @time: 下午3:08
 * @Description:
 */

public class IpPeerInfoStrategy implements PeerInfoStrategy {

    private static String localSign = "";


    @Override
    public String name() {

        if (localSign.length() > 0) {
            return localSign;
        }
        try {
            IPAddress ipAddress = IPAddressUtil.loadLocalAddress();
            List<Inet4Address> ipv4List = ipAddress.getIpv4AddressList();
            if (ipv4List.size() > 0) {
                int i = 0;
                do {
                    Inet4Address address = ipv4List.get(i);
                    String host = address.getHostAddress();
                    if (!host.substring(0, 2).equals("127")) {
                        localSign = host;
                    }
                    i++;
                } while (i < ipv4List.size() && localSign.length() == 0);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (localSign.length() == 0) {
            throw new RuntimeException("初始化本地标示错误");
        }
        return localSign;
    }
}
