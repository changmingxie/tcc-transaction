package org.mengyun.tcctransaction.dashboard.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author huabao.fang
 * @Date 2022/6/8 11:49
 */
public class TccAccessDeniedHandler implements AccessDeniedHandler {

    private Logger logger = LoggerFactory.getLogger(TccAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        logger.error("Responding with unauthorized error. Message:{}, url:{}", accessDeniedException.getMessage(), request.getRequestURI());
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN");
    }
}
