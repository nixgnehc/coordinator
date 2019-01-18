package cn.bbqiu.middleware.coordinator.nodes.ipstrategy;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class IPAddressUtil {

    /**
     * 获取本机ip地址信息(原始信息，不做任何加工处理)
     *
     * @throws SocketException
     */
    public static IPAddress loadLocalAddress() throws SocketException {
        IPAddress ipAddress = new IPAddress();
        ipAddress.setIpv4AddressList(new ArrayList<Inet4Address>());
        ipAddress.setIpv6AddressList(new ArrayList<Inet6Address>());

        Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress inetAddress = addresses.nextElement();
                if (inetAddress instanceof Inet4Address) {
                    ipAddress.getIpv4AddressList().add((Inet4Address) inetAddress);
                } else if (inetAddress instanceof Inet6Address) {
                    ipAddress.getIpv6AddressList().add((Inet6Address) inetAddress);
                }
            }
        }
        return ipAddress;
    }

}
