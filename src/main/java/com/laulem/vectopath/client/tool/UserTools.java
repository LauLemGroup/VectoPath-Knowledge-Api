package com.laulem.vectopath.client.tool;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserTools {
    private static final Logger logger = LoggerFactory.getLogger(UserTools.class);
    private static final String LOCALHOST_IP = "0:0:0:0:0:0:0:1";
    private static final List<String> IP_HEADERS = Arrays.asList("X-FORWARDED-FOR", "X-FORWARDED-FRONT", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR");


    private UserTools() {
    }

    public static String getIpAddr(final HttpServletRequest request) {
        String ipAdresse = Stream.concat(Stream.ofNullable(request.getRemoteAddr()), UserTools.IP_HEADERS.stream().map(request::getHeader))
                .filter(Objects::nonNull)
                .flatMap(str -> Arrays.asList(str.split(",")).reversed().stream())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(UserTools::isNonLocalIp)
                .collect(Collectors.joining(","));

        if (ipAdresse.isBlank()) {
            return "UNKNOWN";
        }
        return ipAdresse;
    }

    private static boolean isNonLocalIp(final String ip) {
        try {
            return Strings.isNotBlank(ip) && !UserTools.LOCALHOST_IP.equals(ip) && !InetAddress.getByName(ip).isSiteLocalAddress();
        } catch (final Exception e) {
            return false;
        }
    }

    public static String getUsername() {
        try {
            return SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();
        } catch (final Exception e) {
            logger.error("Error getting username from SecurityContext", e);
            return null;
        }
    }
}
