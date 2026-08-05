package ru.kushedusound.controller.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kushedusound.entity.dto.request.JwtRefreshRequestDto;
import ru.kushedusound.entity.dto.request.LoginRequestDto;
import ru.kushedusound.entity.dto.request.create.UserCreateRequestDto;
import ru.kushedusound.entity.dto.response.JwtAccessResponseDto;
import ru.kushedusound.entity.dto.response.JwtResponseDto;
import ru.kushedusound.entity.dto.response.UserResponseDto;
import ru.kushedusound.service.UserService;
import ru.kushedusound.service.security.AuthService;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    private final String REFRESH_COOKIE_NAME = "refreshToken";
    private final String REFRESH_COOKIE_PATH = "/auth/refresh";

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserCreateRequestDto dto){
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAccessResponseDto> login(
            @RequestBody LoginRequestDto dto,
            HttpServletResponse response){
        JwtResponseDto tokens = authService.login(dto.email(), dto.password());
        setRefreshCookie(response, tokens.refreshToken(), Duration.ofDays(7));
        return ResponseEntity.ok(new JwtAccessResponseDto(tokens.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAccessResponseDto> refresh(
            @CookieValue(REFRESH_COOKIE_NAME) JwtRefreshRequestDto dto){
        return ResponseEntity.ok(authService.refresh(dto.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(REFRESH_COOKIE_NAME) JwtRefreshRequestDto dto,
            HttpServletResponse response){

        authService.logout(dto.refreshToken());
        setRefreshCookie(response, "", Duration.ZERO);
        return ResponseEntity.noContent().build();
    }


    private void setRefreshCookie (HttpServletResponse response, String value, Duration maxAge){
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
