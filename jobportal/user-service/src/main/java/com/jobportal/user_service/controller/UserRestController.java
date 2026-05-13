package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.UserDto;
import com.jobportal.user_service.entity.Users;
import com.jobportal.user_service.repository.UsersRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UsersRepository usersRepository;

    public UserRestController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    // Called by recruiter-service, jobseeker-service, etc.
    @GetMapping("/by-email/{email:.+}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return ResponseEntity.ok(toDto(user));
    }

    // Called when consumer only has userId
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable int id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return ResponseEntity.ok(toDto(user));
    }

    // Called by api-gateway's GatewayUserDetailsService to authenticate users
    @GetMapping("/credentials/{email:.+}")
    public ResponseEntity<UserCredentialsDto> getUserCredentials(@PathVariable String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return ResponseEntity.ok(new UserCredentialsDto(
                user.getEmail(),
                user.getPassword(),
                user.getUserTypeId().getUserTypeName()
        ));
    }

    private UserDto toDto(Users user) {
        return new UserDto(
                user.getUserId(),
                user.getEmail(),
                user.getUserTypeId().getUserTypeId(),
                user.getUserTypeId().getUserTypeName()
        );
    }

    public record UserCredentialsDto(String email, String encodedPassword, String role) {}
}
