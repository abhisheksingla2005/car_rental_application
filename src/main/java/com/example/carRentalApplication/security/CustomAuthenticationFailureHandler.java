package com.example.carRentalApplication.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "Login failed. Please check your credentials.";

        if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid email or password. Please try again.";
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "User not found. Please check your email address.";
        } else if (exception instanceof DisabledException) {
            errorMessage = "Your account has been disabled. Please contact support.";
        }

        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect("/login.html?error=true&message=" + encodedMessage);
    }
}
