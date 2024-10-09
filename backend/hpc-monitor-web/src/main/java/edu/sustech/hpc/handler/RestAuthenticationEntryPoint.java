package edu.sustech.hpc.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sustech.hpc.result.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

// Refer: https://www.devglan.com/spring-security/exception-handling-in-spring-security
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException{
		ApiResponse response = new ApiResponse(401, "Unauthorized, no JWT or the JWT may is expired");
		httpServletResponse.setStatus(401);
		OutputStream out = httpServletResponse.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, response);
		out.flush();
	}
}