package com.task.mgmt.tracker.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AppUtils {

    public static final String USERNAME_ATTRIBUTE = "validatedUsername";

    public static String getLoggedInUser() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            return (String) request.getAttribute(USERNAME_ATTRIBUTE);
        }
        return null;
    }

    public static boolean isUserAuthenticated() {
        return getLoggedInUser() != null;
    }

    private static HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}