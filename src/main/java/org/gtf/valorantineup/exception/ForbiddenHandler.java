package org.gtf.valorantineup.exception;

import org.gtf.valorantineup.security.jwt.AuthEntryPointJwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class ForbiddenHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        logger.error("Forbidden error {}: {}" + "\n",request.getRequestURL(), accessDeniedException.getMessage());
        //Error from this class cannot be catched by ExceptionHandler as it is from a Filter and not from DispatcherServlet, so we'll make an output buffer to write the JSON manually.
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getOutputStream().println("{ \"timestamp\": \"" + (new Date()).getTime() + "\", \"message\":\"" + accessDeniedException.getMessage() + "\",\"httpCode\":\"403\",\"httpCodeMessage\":\"Forbidden\"  }");
    }
}