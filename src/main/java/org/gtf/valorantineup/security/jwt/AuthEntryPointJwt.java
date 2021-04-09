package org.gtf.valorantineup.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	//This is the authentication entry point. commence() will be triggered in case unauthenticated user tries to log in and AuthenticationException is thrown

	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

	@Override
	public void commence(HttpServletRequest request,
						 HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
		logger.error("Unauthorized error {}: {}" + "\n",request.getRequestURL(), authException.getMessage());
		//Error from this class isn't catched by ExceptionHandler as it is from a Filter and not from DispatcherServlet, so we'll make an output buffer to write the JSON manually.
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getOutputStream().println("{ \"timestamp\": \"" + (new Date()).getTime() + "\", \"message\":\"" + authException.getMessage() + "\",\"httpCode\":\"401\",\"httpCodeMessage\":\"Unauthorized\"  }");
		//FixMe: somehow cannot get message parameter working
		//response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: " + authException.getMessage());
		}
}
