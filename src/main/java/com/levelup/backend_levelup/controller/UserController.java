package com.levelup.backend_levelup.controller;

import com.levelup.backend_levelup.dto.UserResponseDto;
import com.levelup.backend_levelup.service.UserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.levelup.backend_levelup.model.Role;
import com.levelup.backend_levelup.model.User;
import com.levelup.backend_levelup.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncFirebaseUser(@RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token no válido");
        }

        String token = authorizationHeader.substring(7);

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();

            if (!userRepository.existsByFirebaseUid(uid)) {

                User newUser = User.builder()
                        .firebaseUid(uid)
                        .email(email)
                        .firstName(name != null ? name : "Usuario")
                        .lastName("Firebase")
                        .role(Role.USER)
                        .build();

                userRepository.save(newUser);
                return ResponseEntity.ok("Usuario sincronizado exitosamente.");
            }
            return ResponseEntity.ok("El usuario ya estaba sincronizado.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

}