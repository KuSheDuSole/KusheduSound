package ru.kushedusound.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.kushedusound.entity.dto.response.JwtAccessResponseDto;
import ru.kushedusound.entity.dto.response.JwtResponseDto;
import ru.kushedusound.service.security.jwt.JwtService;
import ru.kushedusound.service.security.jwt.TokenBlacklistService;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public JwtResponseDto login(String username, String password){
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        return new JwtResponseDto(accessToken, refreshToken);
    }

    public JwtAccessResponseDto refresh(String refreshToken){
        if (!"refresh".equals(jwtService.extractType(refreshToken))){
            throw new IllegalArgumentException("Неверный тип токена");
        }
        if (jwtService.isTokenExpired(refreshToken)){
            throw new IllegalStateException("Refresh token истек, требуется повторный логин");
        }

        String jti = jwtService.extractJti(refreshToken);
        if (tokenBlacklistService.isBlacklisted(jti)){
            throw new IllegalStateException("Refresh token отозван");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails freshUserDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateAccessToken(freshUserDetails);

        return new JwtAccessResponseDto(newAccessToken);
    }

    public void logout(String refreshToken){
        String jti = jwtService.extractJti(refreshToken);
        tokenBlacklistService.addToBlacklist(jti, jwtService.extractExpirations(refreshToken));
    }
}