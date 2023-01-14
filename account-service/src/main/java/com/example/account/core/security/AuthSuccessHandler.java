package com.example.account.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
 
@Component
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
 
	// private UserService userService;
	
	@Override // disable redirect
	protected void handle(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
    	System.out.println("suc");
    	// userService.clearFailureCount(LoginUser.getId());
    	// userService.visit(LoginUser.getId());
    	super.onAuthenticationSuccess(request, response, authentication);
    }
 
}