package com.laulem.vectopath.infra.conf.mdc;

import com.laulem.vectopath.client.tool.UserTools;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class MdcUserFilter extends OncePerRequestFilter {

    private static final String TRANSACTION_USER = "transaction.user";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MDC.put(TRANSACTION_USER, UserTools.getUsername());

        filterChain.doFilter(request, response);
    }
}

