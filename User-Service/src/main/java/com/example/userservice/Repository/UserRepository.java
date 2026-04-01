package com.example.userservice.repository;

import com.example.userservice.entity.User;
import com.example.userservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailAndIsActiveTrue(String email);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);


    Collection<Object> findByIsActiveTrue();
}
