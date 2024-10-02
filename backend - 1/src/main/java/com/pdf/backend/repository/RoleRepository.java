package com.pdf.backend.repository;

import java.util.Optional;

import com.pdf.backend.entities.ERole;
import com.pdf.backend.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
