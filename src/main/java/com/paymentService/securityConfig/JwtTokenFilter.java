package com.paymentService.securityConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;

/**
 * Filter for handling JWT token authentication in HTTP requests.
 * Extends OncePerRequestFilter to ensure the filter is only executed once per request.
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * Constructor for JwtTokenFilter.
     * @param jwtUtil The JWT utility class for token operations
     */
    public JwtTokenFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Core filter method that processes each HTTP request.
     * Validates JWT token from Authorization header and sets up Spring Security context.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @param chain The filter chain
     * @throws ServletException If there's a servlet error
     * @throws IOException If there's an I/O error
     */
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
     @NonNull HttpServletResponse response,
      @NonNull FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                List<SimpleGrantedAuthority> authorities = jwtUtil.extractRoles(token).stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
                User user = new User(username, "", authorities);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
