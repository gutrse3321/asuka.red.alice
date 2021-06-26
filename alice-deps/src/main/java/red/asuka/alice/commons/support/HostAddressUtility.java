package red.asuka.alice.commons.support;

import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @Author: Tomonori
 * @Date: 2019/10/28 19:00
 * @Desc:
 */
@UtilityClass
public class HostAddressUtility {

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
     * @param request
     * @return
     */
    public String getIpAddress(HttpServletRequest request) {
        //获取请求主机IP地址，如果通过代理进来，则透过防火墙获取真实Ip地址
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Forwarded-For");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            ip = request.getRemoteAddr();
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    public long localHostAfterTwo() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            byte[] ip = localHost.getAddress();
            long ipa = 0;
            ipa |= ((ip[3] << 8) & 0xff00);
            ipa |= (ip[2] & 0xff);
            if (ipa < 0) {
                ipa = ipa ^ (-1L << 32);
            }
            return ipa;
        } catch (UnknownHostException e) {
            return 0;
        }
    }

    public long localHostAfterTwo(InetAddress inetAddress) {
        byte[] ip = inetAddress.getAddress();
        long ipa = 0;
        ipa |= ((ip[3] << 8) & 0xff00);
        ipa |= (ip[2] & 0xff);
        if (ipa < 0) {
            ipa = ipa ^ (-1L << 32);
        }
        return ipa;
    }

    public Set<InetAddress> multiGetLocalAddress(String... ignoreHost) throws SocketException {
        List<String> ignores = new ArrayList<>();
        if (null != ignoreHost)
            ignores.addAll(Arrays.asList(ignoreHost));
        return multiGetLocalAddress(ignores);
    }

    public Set<InetAddress> multiGetLocalAddress(List<String> ignore) throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        Set<InetAddress> ips = new HashSet<>();
        Set<String> ignoreIps = new HashSet<>();
        ignoreIps.add("127.0.0.1");
        ignoreIps.add("255.255.255.255");
        if (null != ignore) ignoreIps.addAll(ignore);
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                String hostAddress = inetAddress.getHostAddress();
                if (matchIpv4(hostAddress) && !ignoreIps.contains(hostAddress)) {
                    ips.add(inetAddress);
                }
            }
        }
        return ips;
    }

    public boolean matchIpv4(String ip) {
        if (!StringUtility.hasText(ip)) return false;
        Pattern p = Pattern.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
        return p.matcher(ip).matches();
    }

}
