package com.task.mgmt.tracker.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenValidationFilter extends OncePerRequestFilter {

	@Autowired
	private TokenValidationService tokenValidationService;

	public static final String ATTR_USERNAME = "validatedUsername";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			TokenValidationService.IntrospectionResponse introspection = tokenValidationService
					.validateAndExtractUser(token);

			if (introspection != null && introspection.isActive()) {
				request.setAttribute(ATTR_USERNAME, introspection.getUsername());
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.getWriter()
						.write("{\"error\": \"Invalid or expired token\", \"message\": \"Token validation failed\"}");
				return;
			}
		} else if (!isPublicEndpoint(request.getRequestURI())) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter()
					.write("{\"error\": \"Missing token\", \"message\": \"Authorization header required\"}");
			return;
		}

		filterChain.doFilter(request, response);
	}

	private boolean isPublicEndpoint(String path) {
		String[] publicEndpoints = { "/api/v1/auth", "/api/v1/otp", "/public", "/external", "/actuator/health", "/ws",
				"/swagger-ui", "/v3/api-docs", "/webjars" };

		for (String endpoint : publicEndpoints) {
			if (path.startsWith(endpoint)) {
				return true;
			}
		}
		return false;
	}
}