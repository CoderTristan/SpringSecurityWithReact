package com.example.fullsite;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    @NullMarked
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Quick exit if there's no Bearer token header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract and trim the token string
        jwt = authHeader.substring(7).trim();

        // 2. Defensive Check: Identify if the frontend accidentally sent garbage or empty data
        if (jwt.isEmpty() ||
                jwt.equalsIgnoreCase("undefined") ||
                jwt.equalsIgnoreCase("null") ||
                !jwt.contains(".")) {

            // Fail safely: Let the request proceed unauthenticated so Spring Security can block it properly
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Exception Safety Net: Try to parse the token. If it's corrupted, don't crash the API.
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Log it if necessary, then pass through unauthenticated
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Authenticate the user if the token claim is valid and no security context exists yet
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}