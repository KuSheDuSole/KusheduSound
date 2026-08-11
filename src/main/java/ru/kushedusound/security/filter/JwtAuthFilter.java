package ru.kushedusound.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.kushedusound.security.CustomPrincipial;
import ru.kushedusound.security.jwt.JwtService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    @NullMarked
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);

        try{
            if (!"access".equals(jwtService.extractType(token))){
                filterChain.doFilter(request, response);
                return;
            }
            String email = jwtService.extractEmail(token);
            String username = jwtService.extractUsername(token);
            Long id = jwtService.extractUserId(token);
            String authorityString = jwtService.extractAuthorities(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null
                    && !jwtService.isTokenExpired(token)){

                CustomPrincipial principial = new CustomPrincipial(id, email, username);

                List<SimpleGrantedAuthority> authorities = Arrays.stream(authorityString.split(","))
                        .map(SimpleGrantedAuthority::new).toList();

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(principial, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        catch (Exception e){

        }
        filterChain.doFilter(request, response);
    }
}