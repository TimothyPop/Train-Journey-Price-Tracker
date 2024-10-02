package com.pdf.backend.repository;


import com.pdf.backend.entities.ERole;
import com.pdf.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") ERole roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name IN (:roleNames)")
    List<User> findUsersByAllRoleName(@Param("roleNames") List<ERole> roleNames);
}
