package ru.kushedusound.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kushedusound.entity.User;
import ru.kushedusound.entity.dto.request.create.UserCreateRequestDto;
import ru.kushedusound.entity.dto.request.update.UserUpdateRequestDto;
import ru.kushedusound.entity.dto.response.UserResponseDto;
import ru.kushedusound.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto createUser(UserCreateRequestDto dto){
        if (userRepository.existsByEmail(dto.email())){
            throw new IllegalArgumentException("Email уже занят: " + dto.email());
        }
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        return UserResponseDto.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Пользователь не найден, id = " + id)
        );
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("Пользователь не найден, email = " + email));
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserDtoById(Long id){
        return UserResponseDto.from(getUserById(id));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers(){
        return userRepository.findAll()
                .stream().map(UserResponseDto::from).toList();
    }

    public UserResponseDto updateUser(Long id, UserUpdateRequestDto dto){
        User user = getUserById(id);
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        return UserResponseDto.from(userRepository.save(user));
    }

    public void deleteUser(Long id){
        User user = getUserById(id);
        userRepository.delete(user);
    }

}