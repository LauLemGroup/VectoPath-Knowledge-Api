package com.laulem.vectopath.infra.conf.mdc;

import com.laulem.vectopath.client.tool.UserTools;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcFilter extends OncePerRequestFilter {

    private static final String TRANSACTION_ID = "transaction.id";
    private static final String TRANSACTION_IP = "transaction.ip";
    private static final String TRANSACTION_USER = "transaction.user";
    private static final String TRANSACTION_PATH = "transaction.path";
    private static final String TRANSACTION_QUERY = "transaction.query";
    private static final String TRANSACTION_STATUS = "transaction.status";
    private static final String GOAL_LOG = "goal.log";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            populateMdc(request);
            logger.info("Starting request processing");

            filterChain.doFilter(request, response);

            MDC.put(TRANSACTION_STATUS, String.valueOf(response.getStatus()));
            logger.info("End request processing");
        } finally {
            clearMdc();
        }
    }

    private void populateMdc(HttpServletRequest request) {
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
        MDC.put(TRANSACTION_IP, UserTools.getIpAddr(request));
        MDC.put(TRANSACTION_PATH, request.getRequestURI());
        MDC.put(TRANSACTION_QUERY, request.getQueryString());
        MDC.put(TRANSACTION_STATUS, "");
        MDC.put(GOAL_LOG, "");
    }

    private void clearMdc() {
        MDC.remove(TRANSACTION_ID);
        MDC.remove(TRANSACTION_IP);
        MDC.remove(TRANSACTION_USER);
        MDC.remove(TRANSACTION_PATH);
        MDC.remove(TRANSACTION_QUERY);
        MDC.remove(TRANSACTION_STATUS);
        MDC.remove(GOAL_LOG);
        MDC.clear();
    }
}
