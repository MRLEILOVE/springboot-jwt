package com.leigq.www.jwt.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WebUtils
 *
 * @author leigq
 * @date 2021 -05-13 10:49:11
 */
public final class WebUtils {

    /**
     * Gets HttpServletRequest.
     *
     * @return the request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }


    /**
     * Gets HttpServletResponse.
     *
     * @return the response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * Gets ServletRequestAttributes.
     *
     * @return the request attributes
     */
    private static ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes());
    }

}
