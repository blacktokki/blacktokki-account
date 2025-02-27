package com.example.account.core.security;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.account.core.dto.BaseUserDto;

import lombok.RequiredArgsConstructor;
 
@Component
@RequiredArgsConstructor
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private final JwtTokenProvider jwtTokenProvider;

	private final Logger logger = Logger.getGlobal();

	// private UserService userService;
	
	@Override // disable redirect
	protected void handle(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
    	logger.info("login success");
    	// userService.clearFailureCount(LoginUser.getId());
    	// userService.visit(LoginUser.getId());
    	super.onAuthenticationSuccess(request, response, authentication);
		String token = jwtTokenProvider.createToken(((BaseUserDto) authentication.getPrincipal()).getUsername(), null, null);
		response.addHeader("Authorization", "JWT " + token);
    }
 
}