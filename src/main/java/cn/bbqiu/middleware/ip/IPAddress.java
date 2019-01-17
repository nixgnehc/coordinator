package cn.bbqiu.middleware.ip;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.ArrayList;
import java.util.List;

public class IPAddress {

    private List<Inet4Address> ipv4AddressList = new ArrayList<>();
    private List<Inet6Address> ipv6AddressList = new ArrayList<>();

    public List<Inet4Address> getIpv4AddressList() {
        return ipv4AddressList;
    }

    public void setIpv4AddressList(List<Inet4Address> ipv4AddressList) {
        this.ipv4AddressList = ipv4AddressList;
    }

    public List<Inet6Address> getIpv6AddressList() {
        return ipv6AddressList;
    }

    public void setIpv6AddressList(List<Inet6Address> ipv6AddressList) {
        this.ipv6AddressList = ipv6AddressList;
    }
}
