package com.pdf.backend.controllers;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.pdf.backend.entities.ERole;
import com.pdf.backend.entities.Role;
import com.pdf.backend.entities.User;
import com.pdf.backend.payload.request.LoginRequest;
import com.pdf.backend.payload.request.SignupRequest;
import com.pdf.backend.payload.response.CommonResponse;
import com.pdf.backend.payload.response.JwtResponse;
import com.pdf.backend.repository.RoleRepository;
import com.pdf.backend.repository.UserRepository;
import com.pdf.backend.security.jwt.JwtUtils;
import com.pdf.backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new CommonResponse(true, "logged_successful", new JwtResponse(jwt,
                        userDetails.getId(),
                        userDetails.getName(),
                        userDetails.getUsername(),
                        roles)));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                return ResponseEntity
                        .badRequest()
                        .body(new CommonResponse(false, "username_taken", null));
            }

            User user = new User();
            user.setName(signUpRequest.getName());
            user.setUsername(signUpRequest.getUsername());
            user.setPassword(encoder.encode(signUpRequest.getPassword()));
            user.setEmail(signUpRequest.getEmail());
            user.setPhone(signUpRequest.getPhone());

            Set<String> strRoles = signUpRequest.getRole();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null) {
                Role userRole = roleRepository.findByName(ERole.ROLE_WORKER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "super_admin":
                            Role adminRole = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(adminRole);
                            break;
                        case "admin":
                            Role modRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(modRole);
                            break;

                        case "supervisor":
                            Role teacherRole = roleRepository.findByName(ERole.ROLE_SITE_SUPERVISOR)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(teacherRole);
                            break;

                        default:
                            Role parentRole = roleRepository.findByName(ERole.ROLE_WORKER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(parentRole);
                            break;
                    }
                });
            }

            user.setRoles(roles);
            userRepository.save(user);

            return ResponseEntity.ok(new CommonResponse(true, "regis_successful", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonResponse(false, "regis_unsuccessful", e.getMessage()));
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + " " + errorMessage);
        });
        return ResponseEntity.ok(new CommonResponse(false, "bad_request", null, errors));
    }

    @GetMapping
    public ResponseEntity<CommonResponse> getAllTasks() {
        List<User> users = userRepository.findAll();
        CommonResponse response = new CommonResponse(true, "Tasks retrieved successfully", users);
        return ResponseEntity.ok(response);
    }
}
