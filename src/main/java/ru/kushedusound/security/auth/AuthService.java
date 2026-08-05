package ru.kushedusound.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.kushedusound.entity.User;
import ru.kushedusound.entity.dto.response.JwtAccessResponseDto;
import ru.kushedusound.entity.dto.response.JwtResponseDto;
import ru.kushedusound.security.jwt.JwtService;
import ru.kushedusound.security.jwt.TokenBlacklistService;
import ru.kushedusound.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public JwtResponseDto login(String username, String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = userService.getUserByEmail(username);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

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

        String email = jwtService.extractEmail(refreshToken);
        User user = userService.getUserByEmail(email);

        String newAccessToken = jwtService.generateAccessToken(user);

        return new JwtAccessResponseDto(newAccessToken);
    }

    public void logout(String refreshToken){
        String jti = jwtService.extractJti(refreshToken);
        tokenBlacklistService.addToBlacklist(jti, jwtService.extractExpirations(refreshToken));
    }
}