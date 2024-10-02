package com.pdf.backend.controllers;

import com.pdf.backend.payload.request.SignupRequest;
import com.pdf.backend.payload.request.UpdateRequest;
import com.pdf.backend.payload.response.CommonResponse;
import com.pdf.backend.repository.RoleRepository;
import com.pdf.backend.entities.ERole;
import com.pdf.backend.entities.Role;
import com.pdf.backend.entities.User;
import com.pdf.backend.repository.UserRepository;
import com.pdf.backend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/getToken")
    public ResponseEntity<?> getDOBSToken() {
        String jwt = jwtUtils.generateDobsJwtToken("sonnet");
        return ResponseEntity.ok(jwt);
    }

    @GetMapping("/verifyToken")
    public ResponseEntity<?> verifyToken(@RequestParam String token) {
        return ResponseEntity.ok(jwtUtils.getClaims(token));
    }

    @PostMapping("/create_users")
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
                Role userRole = roleRepository.findByName(ERole.ROLE_SAMPLERS)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    if (role.equals("admin")) {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                    } else {
                        Role parentRole = roleRepository.findByName(ERole.ROLE_SAMPLERS)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(parentRole);
                    }
                });
            }

            user.setRoles(roles);
            userRepository.save(user);

            return ResponseEntity.ok(new CommonResponse(true, "User Created Successfully", user));
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonResponse(false, "User Creation Failed", e.getMessage()));
        }
    }

    @PutMapping("/update_user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateRequest updateRequest) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Error: User not found."));

            user.setName(updateRequest.getName());
            user.setUsername(updateRequest.getUsername());
            if (!updateRequest.getPassword().isEmpty()) {
                user.setPassword(encoder.encode(updateRequest.getPassword()));
            }
            user.setEmail(updateRequest.getEmail());
            user.setPhone(updateRequest.getPhone());
            userRepository.save(user);

            return ResponseEntity.ok(new CommonResponse(true, "User Updated Successfully", user));
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonResponse(false, "User Update Failed", e.getMessage()));
        }
    }

    @DeleteMapping("/delete_user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Error: User not found."));

            userRepository.delete(user);

            return ResponseEntity.ok(new CommonResponse(true, "User Deleted Successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonResponse(false, "User Deletion Failed", e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<CommonResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(new CommonResponse(true, "Clients retrieved successfully", users));
    }
}
