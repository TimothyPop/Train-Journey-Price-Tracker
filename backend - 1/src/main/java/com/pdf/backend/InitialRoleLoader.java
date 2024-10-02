package com.pdf.backend;

import com.pdf.backend.entities.ERole;
import com.pdf.backend.entities.Role;
import com.pdf.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class InitialRoleLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (roleRepository.findByName(ERole.ROLE_SAMPLERS).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_SAMPLERS));
        }

        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
        }

    }
}
