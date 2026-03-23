package com.example.User_Service.Repository;

import com.example.User_Service.Entity.User;
import com.example.User_Service.Enums.Role;
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
