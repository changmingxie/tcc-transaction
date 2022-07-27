package org.mengyun.tcctransaction.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

public class NetUtils {

    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class.getSimpleName());

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    private static final String ANYHOST_VALUE = "0.0.0.0";
    private static final String LOCALHOST_VALUE = "127.0.0.1";

    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static InetSocketAddress toInetSocketAddress(String address) {
        int i = address.indexOf(':');
        String host;
        int port;
        if (i > -1) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
            port = 0;
        }
        return new InetSocketAddress(host, port);
    }

    public static String parseSocketAddress(SocketAddress socketAddress) {
        if (socketAddress != null) {
            String addr = socketAddress.toString();
            int index = addr.lastIndexOf("/");
            return (index != -1) ? addr.substring(index + 1) : addr;
        }
        return null;
    }

    /**
     * Find first valid IP from local network card
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static Optional<InetAddress> toValidAddress(InetAddress address) {
        if (isValidV4Address(address)) {
            return Optional.of(address);
        }
        return Optional.empty();
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;

        try {
            NetworkInterface networkInterface = findNetworkInterface();
            if (networkInterface != null) {
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    Optional<InetAddress> addressOp = toValidAddress(addresses.nextElement());
                    if (addressOp.isPresent()) {
                        try {
                            if (addressOp.get().isReachable(100)) {
                                return addressOp.get();
                            }
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("", e);
        }

        try {
            localAddress = InetAddress.getLocalHost();
            Optional<InetAddress> addressOp = toValidAddress(localAddress);
            if (addressOp.isPresent()) {
                return addressOp.get();
            }
        } catch (Throwable e) {
            logger.warn("", e);
        }


        return localAddress;
    }

    public static NetworkInterface findNetworkInterface() {

        List<NetworkInterface> validNetworkInterfaces = emptyList();
        try {
            validNetworkInterfaces = getValidNetworkInterfaces();
        } catch (Throwable e) {
            logger.warn("", e);
        }

        NetworkInterface result = null;

        for (NetworkInterface networkInterface : validNetworkInterfaces) {
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                Optional<InetAddress> addressOp = toValidAddress(addresses.nextElement());
                if (addressOp.isPresent()) {
                    try {
                        if (addressOp.get().isReachable(100)) {
                            result = networkInterface;
                            break;
                        }
                    } catch (IOException e) {
                        //ignore
                    }
                }
            }
        }

        if (result == null) {
            if ((validNetworkInterfaces.isEmpty())) {
                return null;
            }
            return validNetworkInterfaces.get(0);
        }

        return result;
    }

    private static List<NetworkInterface> getValidNetworkInterfaces() throws SocketException {
        List<NetworkInterface> validNetworkInterfaces = new LinkedList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (ignoreNetworkInterface(networkInterface)) { // ignore
                continue;
            }
            validNetworkInterfaces.add(networkInterface);
        }
        return validNetworkInterfaces;
    }

    private static boolean ignoreNetworkInterface(NetworkInterface networkInterface) throws SocketException {
        return networkInterface == null
                || networkInterface.isLoopback()
                || networkInterface.isVirtual()
                || !networkInterface.isUp();
    }

    private static boolean isValidV4Address(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }

        String name = address.getHostAddress();
        return (name != null
                && IP_PATTERN.matcher(name).matches()
                && !ANYHOST_VALUE.equals(name)
                && !LOCALHOST_VALUE.equals(name));
    }
}
