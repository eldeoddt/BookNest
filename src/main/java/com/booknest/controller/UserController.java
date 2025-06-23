package com.booknest.controller;

import com.booknest.model.UserEntity;
import com.booknest.dto.ResponseDTO;
import com.booknest.dto.UserDTO;
import com.booknest.security.TokenProvider;
import com.booknest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(value = "{\"username\": \"jisu\", \"password\": \"12345\"}")
                    )
            )
    )
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {

            if (userDTO == null || userDTO.getPassword() == null) {
                throw new RuntimeException("Invalid password value.");
            }
            UserEntity user = UserEntity.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build();

            UserEntity registeredUser = userService.create(user);

            UserDTO responseUserDTO = UserDTO.builder()
                    .id(registeredUser.getId())
                    .username(registeredUser.getUsername())
                    .password(null)
                    .build();

            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @Operation(
            summary = "로그인",
            description = "사용자 로그인 후 JWT 토큰을 반환합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(value = "{\"username\": \"jisu\", \"password\": \"12345\"}")
                    )
            )
    )
    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
        UserEntity user = userService.getByCredential(
                userDTO.getUsername(),
                userDTO.getPassword(),
                passwordEncoder);

        if (user != null) {
            final String token = tokenProvider.create(user);
            log.info("[UserController] 로그인 성공 - userId: {}", user.getId());
            log.info("[UserController] 로그인 성공 - token: {}", token);
            final UserDTO responseUserDTO = UserDTO.builder()
                    .token(token)
                    .username(user.getUsername())
                    .password(null)
                    .id(user.getId())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        } else {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("Login Failed").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
